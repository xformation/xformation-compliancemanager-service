/*
 * */
package com.synectiks.process.server.periodical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Uninterruptibles;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.cluster.Cluster;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.ranges.IndexRange;
import com.synectiks.process.server.indexer.ranges.IndexRangeService;
import com.synectiks.process.server.indexer.ranges.LegacyMongoIndexRangeService;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.periodical.Periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link Periodical} to check if index ranges need to be recalculated and notify the administrators about it.
 *
 * @since 1.2.0
 */
public class IndexRangesMigrationPeriodical extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(IndexRangesMigrationPeriodical.class);

    private final Cluster cluster;
    private final IndexSetRegistry indexSetRegistry;
    private final IndexRangeService indexRangeService;
    private final NotificationService notificationService;
    private final LegacyMongoIndexRangeService legacyMongoIndexRangeService;
    private final ClusterConfigService clusterConfigService;

    @Inject
    public IndexRangesMigrationPeriodical(final Cluster cluster,
                                          final IndexSetRegistry indexSetRegistry,
                                          final IndexRangeService indexRangeService,
                                          final NotificationService notificationService,
                                          final LegacyMongoIndexRangeService legacyMongoIndexRangeService,
                                          final ClusterConfigService clusterConfigService) {
        this.cluster = checkNotNull(cluster);
        this.indexSetRegistry = checkNotNull(indexSetRegistry);
        this.indexRangeService = checkNotNull(indexRangeService);
        this.notificationService = checkNotNull(notificationService);
        this.legacyMongoIndexRangeService = checkNotNull(legacyMongoIndexRangeService);
        this.clusterConfigService = checkNotNull(clusterConfigService);
    }

    @Override
    public void doRun() {
        final MongoIndexRangesMigrationComplete migrationComplete = clusterConfigService.get(MongoIndexRangesMigrationComplete.class);
        if (migrationComplete != null && migrationComplete.complete) {
            LOG.debug("Migration of index ranges (pre alertmanager 1.2.2) already complete. Skipping migration process.");
            return;
        }

        while (!cluster.isConnected() || !cluster.isHealthy()) {
            Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        }

        final Set<String> indexNames = ImmutableSet.copyOf(indexSetRegistry.getManagedIndices());

        // Migrate old MongoDB index ranges
        final SortedSet<IndexRange> mongoIndexRanges = legacyMongoIndexRangeService.findAll();
        for (IndexRange indexRange : mongoIndexRanges) {
            if(indexNames.contains(indexRange.indexName())) {
                LOG.info("Migrating index range from MongoDB: {}", indexRange);
                indexRangeService.save(indexRange);
            } else {
                LOG.info("Removing stale index range from MongoDB: {}", indexRange);
            }

            legacyMongoIndexRangeService.delete(indexRange.indexName());
        }

        // Check whether all index ranges have been migrated
        final int numberOfIndices = indexNames.size();
        final SortedSet<IndexRange> allIndexRanges = indexRangeService.findAll();
        final int numberOfIndexRanges = allIndexRanges.size();
        if (numberOfIndices > numberOfIndexRanges) {
            LOG.info("There are more indices ({}) than there are index ranges ({}). Notifying administrator.",
                    numberOfIndices, numberOfIndexRanges);
            // remove all present index names so we can display the index sets that need manual fixing
            final Set<String> extraIndices = Sets.newHashSet(indexNames);
            allIndexRanges.forEach(indexRange -> extraIndices.remove(indexRange.indexName()));
            final Set<String> affectedIndexSetNames = extraIndices.stream()
                    .map(indexSetRegistry::getForIndex)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(IndexSet::getConfig)
                    .map(IndexSetConfig::title)
                    .collect(Collectors.toSet());
            final Notification notification = notificationService.buildNow()
                    .addSeverity(Notification.Severity.URGENT)
                    .addType(Notification.Type.INDEX_RANGES_RECALCULATION)
                    .addDetail("indices", numberOfIndices)
                    .addDetail("index_ranges", numberOfIndexRanges)
                    .addDetail("index_sets", affectedIndexSetNames.isEmpty() ? null : Joiner.on(", ").join(affectedIndexSetNames));
            notificationService.publishIfFirst(notification);
        }

        clusterConfigService.write(new MongoIndexRangesMigrationComplete(true));
    }

    @Override
    public boolean runsForever() {
        return true;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return false;
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
        return 0;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @JsonAutoDetect
    public static class MongoIndexRangesMigrationComplete {
        @JsonProperty
        public boolean complete;

        @JsonCreator
        public MongoIndexRangesMigrationComplete(@JsonProperty("complete") boolean complete) {
            this.complete = complete;
        }
    }
}
