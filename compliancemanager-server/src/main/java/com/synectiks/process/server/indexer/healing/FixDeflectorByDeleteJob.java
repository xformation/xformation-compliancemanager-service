/*
 * */
package com.synectiks.process.server.indexer.healing;

import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.buffers.Buffers;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.shared.system.activities.Activity;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;
import com.synectiks.process.server.system.jobs.SystemJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class FixDeflectorByDeleteJob extends SystemJob {

    public interface Factory {

        FixDeflectorByDeleteJob create();
    }

    private static final Logger LOG = LoggerFactory.getLogger(FixDeflectorByDeleteJob.class);

    public static final int MAX_CONCURRENCY = 1;

    private final IndexSetRegistry indexSetRegistry;
    private final Indices indices;
    private final ServerStatus serverStatus;
    private final ActivityWriter activityWriter;
    private final Buffers bufferSynchronizer;
    private final NotificationService notificationService;

    private int progress = 0;

    @AssistedInject
    public FixDeflectorByDeleteJob(IndexSetRegistry indexSetRegistry,
                                   Indices indices,
                                   ServerStatus serverStatus,
                                   ActivityWriter activityWriter,
                                   Buffers bufferSynchronizer,
                                   NotificationService notificationService) {
        this.indexSetRegistry = indexSetRegistry;
        this.indices = indices;
        this.serverStatus = serverStatus;
        this.activityWriter = activityWriter;
        this.bufferSynchronizer = bufferSynchronizer;
        this.notificationService = notificationService;
    }

    @Override
    public void execute() {
        indexSetRegistry.forEach(this::doExecute);
    }

    public void doExecute(IndexSet indexSet) {
        if (!indexSet.getConfig().isWritable()) {
            LOG.debug("No need to fix deflector for non-writable index set <{}> ({})", indexSet.getConfig().id(),
                    indexSet.getConfig().title());
            return;
        }

        if (indexSet.isUp() || !indices.exists(indexSet.getWriteIndexAlias())) {
            LOG.error("There is no index <{}>. No need to run this job. Aborting.", indexSet.getWriteIndexAlias());
            return;
        }

        LOG.info("Attempting to fix deflector with delete strategy.");

        // Pause message processing and lock the pause.
        boolean wasProcessing = serverStatus.isProcessing();
        serverStatus.pauseMessageProcessing();
        progress = 10;

        bufferSynchronizer.waitForEmptyBuffers();
        progress = 25;

        // Delete deflector index.
        LOG.info("Deleting <{}> index.", indexSet.getWriteIndexAlias());
        indices.delete(indexSet.getWriteIndexAlias());
        progress = 70;

        // Set up deflector.
        indexSet.setUp();
        progress = 80;

        // Start message processing again.
        try {

            serverStatus.unlockProcessingPause();
            if (wasProcessing) {
                serverStatus.resumeMessageProcessing();
            }
        } catch (Exception e) {
            // lol checked exceptions
            throw new RuntimeException("Could not unlock processing pause.", e);
        }

        progress = 90;
        activityWriter.write(new Activity("Notification condition [" + Notification.Type.DEFLECTOR_EXISTS_AS_INDEX + "] " +
                "has been fixed.", this.getClass()));
        notificationService.fixed(Notification.Type.DEFLECTOR_EXISTS_AS_INDEX);

        progress = 100;
        LOG.info("Finished.");
    }

    @Override
    public void requestCancel() {
        // Cannot be canceled.
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public int maxConcurrency() {
        return MAX_CONCURRENCY;
    }

    @Override
    public boolean providesProgress() {
        return true;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Tries to fix a broken deflector alias by deleting the deflector index. Triggered by hand " +
                "after a notification.";
    }

    @Override
    public String getClassName() {
        return this.getClass().getCanonicalName();
    }

}
