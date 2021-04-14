/*
 * */
package com.synectiks.process.server.rest.models.search.responses;

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
public abstract class TermsStatsResult {
    @JsonProperty
    public abstract long time();

    @JsonProperty
    public abstract List<Map<String, Object>> terms();

    @JsonProperty
    public abstract String builtQuery();

    @JsonCreator
    public static TermsStatsResult create(@JsonProperty("time") long time, @JsonProperty("terms") List<Map<String, Object>> terms, @JsonProperty("built_query") String builtQuery) {
        return new AutoValue_TermsStatsResult(time, terms, builtQuery);
    }
}
