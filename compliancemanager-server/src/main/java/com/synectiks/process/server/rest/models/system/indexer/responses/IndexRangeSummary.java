/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class IndexRangeSummary {
    @JsonProperty("index_name")
    public abstract String indexName();

    @JsonProperty("begin")
    public abstract DateTime begin();

    @JsonProperty("end")
    public abstract DateTime end();

    @Nullable @JsonProperty("calculated_at")
    public abstract DateTime calculatedAt();

    @JsonProperty("took_ms")
    public abstract int calculationTookMs();

    @JsonCreator
    public static IndexRangeSummary create(@JsonProperty("index_name") String indexName,
                                           @JsonProperty("begin") DateTime begin,
                                           @JsonProperty("end") DateTime end,
                                           @Nullable @JsonProperty("calculated_at") DateTime calculatedAt,
                                           @JsonProperty("took_ms") int calculationTookMs) {
        return new AutoValue_IndexRangeSummary(indexName, begin, end, calculatedAt, calculationTookMs);
    }
}
