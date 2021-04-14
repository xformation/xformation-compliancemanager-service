/*
 * */
package com.synectiks.process.server.indexer;

import com.fasterxml.jackson.databind.JsonNode;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.rest.resources.system.indexer.responses.IndexSetStats;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class IndexSetStatsCreator {
    private final Indices indices;

    @Inject
    public IndexSetStatsCreator(final Indices indices) {
        this.indices = indices;
    }

    public IndexSetStats getForIndexSet(final IndexSet indexSet) {
        final Set<String> closedIndices = indices.getClosedIndices(indexSet);
        final List<JsonNode> primaries = StreamSupport.stream(indices.getIndexStats(indexSet).spliterator(), false)
                .map(json -> json.get("primaries"))
                .collect(Collectors.toList());
        final long documents = primaries.stream()
                .map(json -> json.path("docs").path("count").asLong())
                .reduce(0L, Long::sum);
        final long size = primaries.stream()
                .map(json -> json.path("store").path("size_in_bytes").asLong())
                .reduce(0L, Long::sum);

        return IndexSetStats.create(primaries.size() + closedIndices.size(), documents, size);
    }
}
