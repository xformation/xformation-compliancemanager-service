/*
 * */
package com.synectiks.process.server.rest.models.search.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;
import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class TermsHistogramResult {
    @JsonProperty("time")
    public abstract long time();

    @JsonProperty("interval")
    public abstract String interval();

    @JsonProperty("size")
    public abstract long size();

    @JsonProperty("buckets")
    public abstract Map<Long, TermsResult> buckets();

    @JsonProperty("terms")
    public abstract Set<String> terms();

    @JsonProperty("built_query")
    public abstract String builtQuery();

    @JsonProperty("queried_timerange")
    public abstract TimeRange queriedTimerange();

    public static TermsHistogramResult create(long time, String interval, long size, Map<Long, TermsResult> buckets, Set<String> terms, String builtQuery, TimeRange queriedTimerange) {
        return new AutoValue_TermsHistogramResult(time, interval, size, buckets, terms, builtQuery, queriedTimerange);
    }
}
