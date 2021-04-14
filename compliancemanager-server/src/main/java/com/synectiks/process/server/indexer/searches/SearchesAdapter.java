/*
 * */
package com.synectiks.process.server.indexer.searches;

import com.synectiks.process.server.indexer.ranges.IndexRange;
import com.synectiks.process.server.indexer.results.CountResult;
import com.synectiks.process.server.indexer.results.FieldStatsResult;
import com.synectiks.process.server.indexer.results.ScrollResult;
import com.synectiks.process.server.indexer.results.SearchResult;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import java.util.List;
import java.util.Set;

public interface SearchesAdapter {
    CountResult count(Set<String> affectedIndices, String query, TimeRange range, String filter);

    ScrollResult scroll(Set<String> indexWildcards, Sorting sorting, String filter, String query, TimeRange range, int limit, int offset, List<String> fields);
    ScrollResult scroll(Set<String> indexWildcards, Sorting sorting, String filter, String query, int batchSize);
    ScrollResult scroll(ScrollCommand scrollCommand);

    SearchResult search(Set<String> indices, Set<IndexRange> indexRanges, SearchesConfig config);

    FieldStatsResult fieldStats(String query, String filter, TimeRange range, Set<String> indices, String field, boolean includeCardinality, boolean includeStats, boolean includeCount);
}
