/*
 * */
package com.synectiks.process.server.rest.resources.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.indexer.indices.stats.IndexStatistics;
import com.synectiks.process.server.rest.models.system.indexer.responses.IndexStats;

import java.util.Collection;

@JsonAutoDetect
@AutoValue
public abstract class IndexSetStats {
    private static final String FIELD_INDICES = "indices";
    private static final String FIELD_DOCUMENTS = "documents";
    private static final String FIELD_SIZE = "size";

    @JsonProperty(FIELD_INDICES)
    public abstract long indices();

    @JsonProperty(FIELD_DOCUMENTS)
    public abstract long documents();

    @JsonProperty(FIELD_SIZE)
    public abstract long size();

    @JsonCreator
    public static IndexSetStats create(@JsonProperty(FIELD_INDICES) long indices,
                                       @JsonProperty(FIELD_DOCUMENTS) long documents,
                                       @JsonProperty(FIELD_SIZE) long size) {
        return new AutoValue_IndexSetStats(indices, documents, size);
    }

    public static IndexSetStats fromIndexStatistics(Collection<IndexStatistics> indexStatistics, Collection<String> closedIndices) {
        final long totalIndicesCount = indexStatistics.size() + closedIndices.size();
        final long totalDocumentsCount = indexStatistics.stream()
                .map(IndexStatistics::allShards)
                .map(IndexStats::documents)
                .mapToLong(IndexStats.DocsStats::count)
                .sum();
        final long totalSizeInBytes = indexStatistics.stream()
                .map(IndexStatistics::allShards)
                .mapToLong(IndexStats::storeSizeBytes)
                .sum();
        return create(totalIndicesCount, totalDocumentsCount, totalSizeInBytes);
    }
}