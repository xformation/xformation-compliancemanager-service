/*
 * */
package com.synectiks.process.server.rest.models.search.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class HistogramResult {
    @JsonProperty
    public abstract String interval();

    @JsonProperty
    public abstract Map results();

    @JsonProperty
    public abstract long time();

    @JsonProperty
    public abstract String builtQuery();

    @JsonProperty
    public abstract TimeRange queriedTimerange();

    public static HistogramResult create(String interval, Map results, long time, String builtQuery,
                                         TimeRange queriedTimerange) {
        return new AutoValue_HistogramResult(interval, results, time, builtQuery, queriedTimerange);
    }


}
