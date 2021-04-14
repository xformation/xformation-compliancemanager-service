/*
 * */
package com.synectiks.process.server.periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.NoTargetIndexException;
import com.synectiks.process.server.indexer.cluster.Cluster;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indices.HealthStatus;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.indexer.indices.TooManyAliasesException;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategy;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.system.activities.Activity;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class IndexRotationThread extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(IndexRotationThread.class);

    private NotificationService notificationService;
    private final IndexSetRegistry indexSetRegistry;
    private final Cluster cluster;
    private final ActivityWriter activityWriter;
    private final Indices indices;
    private final NodeId nodeId;
    private final Map<String, Provider<RotationStrategy>> rotationStrategyMap;

    @Inject
    public IndexRotationThread(NotificationService notificationService,
                               Indices indices,
                               IndexSetRegistry indexSetRegistry,
                               Cluster cluster,
                               ActivityWriter activityWriter,
                               NodeId nodeId,
                               Map<String, Provider<RotationStrategy>> rotationStrategyMap) {
        this.notificationService = notificationService;
        this.indexSetRegistry = indexSetRegistry;
        this.cluster = cluster;
        this.activityWriter = activityWriter;
        this.indices = indices;
        this.nodeId = nodeId;
        this.rotationStrategyMap = rotationStrategyMap;
    }

    @Override
    public void doRun() {
        // Point deflector to a new index if required.
        if (cluster.isConnected()) {
            indexSetRegistry.forEach((indexSet) -> {
                try {
                    if (indexSet.getConfig().isWritable()) {
                        checkAndRepair(indexSet);
                        checkForRotation(indexSet);
                    } else {
                        LOG.debug("Skipping non-writable index set <{}> ({})", indexSet.getConfig().id(), indexSet.getConfig().title());
                    }
                } catch (Exception e) {
                    LOG.error("Couldn't point deflector to a new index", e);
                }
            });
        } else {
            LOG.debug("Elasticsearch cluster isn't healthy. Skipping index rotation.");
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    protected void checkForRotation(IndexSet indexSet) {
        final IndexSetConfig config = indexSet.getConfig();
        final Provider<RotationStrategy> rotationStrategyProvider = rotationStrategyMap.get(config.rotationStrategyClass());

        if (rotationStrategyProvider == null) {
            LOG.warn("Rotation strategy \"{}\" not found, not running index rotation!", config.rotationStrategyClass());
            rotationProblemNotification("Index Rotation Problem!",
                    "Index rotation strategy " + config.rotationStrategyClass() + " not found! Please fix your index rotation configuration!");
            return;
        }

        final RotationStrategy rotationStrategy = rotationStrategyProvider.get();

        if (rotationStrategy == null) {
            LOG.warn("No rotation strategy found, not running index rotation!");
            return;
        }

        rotationStrategy.rotate(indexSet);
    }

    private void rotationProblemNotification(String title, String description) {
        final Notification notification = notificationService.buildNow()
                .addNode(nodeId.toString())
                .addType(Notification.Type.GENERIC)
                .addSeverity(Notification.Severity.URGENT)
                .addDetail("title", title)
                .addDetail("description", description);
        notificationService.publishIfFirst(notification);
    }

    protected void checkAndRepair(IndexSet indexSet) {
        if (!indexSet.isUp()) {
            if (indices.exists(indexSet.getWriteIndexAlias())) {
                // Publish a notification if there is an *index* called alertmanager2_deflector
                Notification notification = notificationService.buildNow()
                        .addType(Notification.Type.DEFLECTOR_EXISTS_AS_INDEX)
                        .addSeverity(Notification.Severity.URGENT);
                final boolean published = notificationService.publishIfFirst(notification);
                if (published) {
                    LOG.warn("There is an index called [" + indexSet.getWriteIndexAlias() + "]. Cannot fix this automatically and published a notification.");
                }
            } else {
                indexSet.setUp();
            }
        } else {
            try {
                String currentTarget;
                try {
                    currentTarget = indexSet.getActiveWriteIndex();
                } catch (TooManyAliasesException e) {
                    // If we get this exception, there are multiple indices which have the deflector alias set.
                    // We try to cleanup the alias and try again. This should not happen, but might under certain
                    // circumstances.
                    indexSet.cleanupAliases(e.getIndices());
                    try {
                        currentTarget = indexSet.getActiveWriteIndex();
                    } catch (TooManyAliasesException e1) {
                        throw new IllegalStateException(e1);
                    }
                }
                String shouldBeTarget = indexSet.getNewestIndex();

                if (!shouldBeTarget.equals(currentTarget)) {
                    String msg = "Deflector is pointing to [" + currentTarget + "], not the newest one: [" + shouldBeTarget + "]. Re-pointing.";
                    LOG.warn(msg);
                    activityWriter.write(new Activity(msg, IndexRotationThread.class));

                    if (indices.waitForRecovery(shouldBeTarget) == HealthStatus.Red) {
                        LOG.error("New target index for deflector didn't get healthy within timeout. Skipping deflector update.");
                    } else {
                        indexSet.pointTo(shouldBeTarget, currentTarget);
                    }
                }
            } catch (NoTargetIndexException e) {
                LOG.warn("Deflector is not up. Not trying to point to another index.");
            }
        }

    }

    @Override
    public boolean runsForever() {
        return false;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return true;
    }

    @Override
    public boolean masterOnly() {
        return true;
    }

    @Override
    public boolean startOnThisNode() {
        return true;
    }

    @Override
    public boolean isDaemon() {
        return true;
    }

    @Override
    public int getInitialDelaySeconds() {
        return 0;
    }

    @Override
    public int getPeriodSeconds() {
        return 10;
    }

}
