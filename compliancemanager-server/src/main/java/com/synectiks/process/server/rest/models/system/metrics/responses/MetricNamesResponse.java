/*
 * */
package com.synectiks.process.server.rest.models.system.metrics.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class MetricNamesResponse {
    @JsonProperty
    public abstract Set<String> names();

    @JsonCreator
    public static MetricNamesResponse create(@JsonProperty("names") Set<String> names) {
        return new AutoValue_MetricNamesResponse(names);
    }
}
