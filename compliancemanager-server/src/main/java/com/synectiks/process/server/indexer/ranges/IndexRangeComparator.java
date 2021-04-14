/*
 * */
package com.synectiks.process.server.indexer.ranges;

import com.google.common.collect.ComparisonChain;

import java.util.Comparator;

public class IndexRangeComparator implements Comparator<IndexRange> {
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(IndexRange o1, IndexRange o2) {
        return ComparisonChain.start()
                .compare(o1.end(), o2.end())
                .compare(o1.begin(), o2.begin())
                .compare(o1.indexName(), o2.indexName())
                .result();
    }
}
