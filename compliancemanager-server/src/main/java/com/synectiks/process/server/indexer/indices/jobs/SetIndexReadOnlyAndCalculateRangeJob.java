/*
 * */
package com.synectiks.process.server.indexer.indices.jobs;

import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.SetIndexReadOnlyJob;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypePoller;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypesService;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.indexer.ranges.CreateNewSingleIndexRangeJob;
import com.synectiks.process.server.system.jobs.SystemJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SetIndexReadOnlyAndCalculateRangeJob extends SystemJob {
    private static final Logger LOG = LoggerFactory.getLogger(SetIndexReadOnlyAndCalculateRangeJob.class);

    public interface Factory {
        SetIndexReadOnlyAndCalculateRangeJob create(String indexName);
    }

    private final SetIndexReadOnlyJob.Factory setIndexReadOnlyJobFactory;
    private final CreateNewSingleIndexRangeJob.Factory createNewSingleIndexRangeJobFactory;
    private final IndexSetRegistry indexSetRegistry;
    private final Indices indices;
    private final IndexFieldTypesService indexFieldTypesService;
    private final IndexFieldTypePoller indexFieldTypePoller;
    private final String indexName;

    @Inject
    public SetIndexReadOnlyAndCalculateRangeJob(SetIndexReadOnlyJob.Factory setIndexReadOnlyJobFactory,
                                                CreateNewSingleIndexRangeJob.Factory createNewSingleIndexRangeJobFactory,
                                                IndexSetRegistry indexSetRegistry,
                                                Indices indices,
                                                IndexFieldTypesService indexFieldTypesService,
                                                IndexFieldTypePoller indexFieldTypePoller,
                                                @Assisted String indexName) {
        this.setIndexReadOnlyJobFactory = setIndexReadOnlyJobFactory;
        this.createNewSingleIndexRangeJobFactory = createNewSingleIndexRangeJobFactory;
        this.indexSetRegistry = indexSetRegistry;
        this.indices = indices;
        this.indexFieldTypesService = indexFieldTypesService;
        this.indexFieldTypePoller = indexFieldTypePoller;
        this.indexName = indexName;
    }

    @Override
    public void execute() {
        if (indices.isClosed(indexName)) {
            LOG.debug("Not running job for closed index <{}>", indexName);
            return;
        }
        final SystemJob setIndexReadOnlyJob = setIndexReadOnlyJobFactory.create(indexName);
        setIndexReadOnlyJob.execute();
        final SystemJob createNewSingleIndexRangeJob = createNewSingleIndexRangeJobFactory.create(indexSetRegistry.getAll(), indexName);
        createNewSingleIndexRangeJob.execute();

        // Update field type information again to make sure we got the latest state
        indexSetRegistry.getForIndex(indexName)
                .ifPresent(indexSet -> {
                    indexFieldTypePoller.pollIndex(indexName, indexSet.getConfig().id())
                            .ifPresent(indexFieldTypesService::upsert);
                });
    }

    @Override
    public void requestCancel() {}

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public int maxConcurrency() {
        return 1000;
    }

    @Override
    public boolean providesProgress() {
        return false;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Makes index " + indexName + " read only and calculates and adds its index range afterwards.";
    }

    @Override
    public String getClassName() {
        return this.getClass().getCanonicalName();
    }
}
