/*
 * */
package com.synectiks.process.server.rest.models.system.metrics.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Collection;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class MetricsSummaryResponse {
    @JsonProperty
    public abstract int total();
    @JsonProperty
    public abstract Collection metrics();

    @JsonCreator
    public static MetricsSummaryResponse create(@JsonProperty("total") int total, @JsonProperty("metrics") Collection metrics) {
        return new AutoValue_MetricsSummaryResponse(total, metrics);
    }

    public static MetricsSummaryResponse create(Collection metrics) {
        return new AutoValue_MetricsSummaryResponse(metrics.size(), metrics);
    }
}
