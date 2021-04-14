/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class IndexRangesResponse {
    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract List<IndexRangeSummary> ranges();

    @JsonCreator
    public static IndexRangesResponse create(@JsonProperty("total") int total,
                                             @JsonProperty("ranges") List<IndexRangeSummary> ranges) {
        return new AutoValue_IndexRangesResponse(total, ranges);
    }
}
