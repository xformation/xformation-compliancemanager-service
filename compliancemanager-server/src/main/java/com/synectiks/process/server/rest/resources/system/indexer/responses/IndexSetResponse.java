/*
 * */
package com.synectiks.process.server.rest.resources.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;
import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class IndexSetResponse {
    @JsonProperty("total")
    public abstract int total();

    @JsonProperty("index_sets")
    public abstract List<IndexSetSummary> indexSets();

    @JsonProperty("stats")
    public abstract Map<String, IndexSetStats> stats();

    @JsonCreator
    public static IndexSetResponse create(@JsonProperty("total") int total,
                                          @JsonProperty("index_sets") List<IndexSetSummary> ranges,
                                          @JsonProperty("stats") Map<String, IndexSetStats> stats) {
        return new AutoValue_IndexSetResponse(total, ranges, stats);
    }
}