/*
 * */
package com.synectiks.process.common.plugins.views.search;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.ranges.IndexRange;
import com.synectiks.process.server.plugin.streams.Stream;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IndexRangeContainsOneOfStreams implements Predicate<IndexRange> {
    private final Set<IndexSet> validIndexSets;
    private final Set<String> validStreamIds;

    public IndexRangeContainsOneOfStreams(Set<Stream> validStreams) {
        this.validStreamIds = validStreams.stream().map(Stream::getId).collect(Collectors.toSet());
        this.validIndexSets = validStreams.stream().map(Stream::getIndexSet).collect(Collectors.toSet());
    }

    IndexRangeContainsOneOfStreams(Stream... validStreams) {
        this(ImmutableSet.copyOf(validStreams));
    }

    @Override
    public boolean test(IndexRange indexRange) {
        if (validIndexSets.isEmpty() && validStreamIds.isEmpty()) {
            return false;
        }
        // If index range is incomplete, check the prefix against the valid index sets.
        if (indexRange.streamIds() == null) {
            return validIndexSets.stream().anyMatch(indexSet -> indexSet.isManagedIndex(indexRange.indexName()));
        }
        // Otherwise check if the index range contains any of the valid stream ids.
        return !Collections.disjoint(indexRange.streamIds(), validStreamIds);
    }
}
