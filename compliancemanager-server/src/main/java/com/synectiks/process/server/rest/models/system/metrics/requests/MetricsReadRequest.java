/*
 * */
package com.synectiks.process.server.rest.models.system.metrics.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class MetricsReadRequest {
    @JsonProperty
    public abstract List<String> metrics();

    @JsonCreator
    public static MetricsReadRequest create(@JsonProperty("metrics") List<String> metrics) {
        return new AutoValue_MetricsReadRequest(metrics);
    }

}
