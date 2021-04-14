/*
 * */
package com.synectiks.process.server.indexer.counts;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.IndexSetRegistry;

import java.util.Arrays;
import java.util.List;

@Singleton
public class Counts {
    private final IndexSetRegistry indexSetRegistry;
    private final CountsAdapter countsAdapter;

    @Inject
    public Counts(IndexSetRegistry indexSetRegistry, CountsAdapter countsAdapter) {
        this.indexSetRegistry = indexSetRegistry;
        this.countsAdapter = countsAdapter;
    }

    public long total() {
        return totalCount(indexSetRegistry.getManagedIndices());
    }

    public long total(final IndexSet indexSet) {
        return totalCount(indexSet.getManagedIndices());
    }

    private long totalCount(final String[] indexNames) {
        // Return 0 if there are no indices in the given index set. If we run the query with an empty index list,
        // Elasticsearch will count all documents in all indices and thus return a wrong count.
        if (indexNames.length == 0) {
            return 0L;
        }

        final List<String> indices = Arrays.asList(indexNames);
        return countsAdapter.totalCount(indices);
    }
}
