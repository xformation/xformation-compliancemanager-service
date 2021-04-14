/*
 * */
package com.synectiks.process.server.system.processing;

import org.joda.time.DateTime;

import com.synectiks.process.server.plugin.lifecycles.Lifecycle;

/**
 * This is used to track processing status on a single alertmanager node.
 */
public interface ProcessingStatusRecorder {
    /**
     * Update the receive time for the "ingest" measurement point. This is done right before a raw messages gets
     * written to the disk journal.
     *
     * @param newTimestamp the new timestamp to record
     */
    void updateIngestReceiveTime(DateTime newTimestamp);

    DateTime getIngestReceiveTime();

    /**
     * Update the receive time for the "post-processing" measurement point. This is done right after all message
     * processors have run.
     *
     * @param newTimestamp the new timestamp to record
     */
    void updatePostProcessingReceiveTime(DateTime newTimestamp);

    DateTime getPostProcessingReceiveTime();

    /**
     * Update receive time for the "post-indexing" measurement point. This is done right after messages
     * have been written to Elasticsearch.
     *
     * @param newTimestamp the new timestamp to record
     */
    void updatePostIndexingReceiveTime(DateTime newTimestamp);

    DateTime getPostIndexingReceiveTime();

    /**
     * Returns the node {@link Lifecycle} status for the node.
     *
     * @return the node lifecycle status
     */
    Lifecycle getNodeLifecycleStatus();

    long getJournalInfoUncommittedEntries();

    double getJournalInfoReadMessages1mRate();

    double getJournalInfoWrittenMessages1mRate();
}
