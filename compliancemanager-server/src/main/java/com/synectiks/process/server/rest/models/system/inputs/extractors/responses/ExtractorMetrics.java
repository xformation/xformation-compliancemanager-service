/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.extractors.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.rest.models.metrics.responses.TimerRateMetricsResponse;

import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class ExtractorMetrics {
    @JsonProperty
    public abstract TimerRateMetricsResponse total();

    @JsonProperty
    public abstract TimerRateMetricsResponse condition();

    @JsonProperty
    public abstract TimerRateMetricsResponse execution();

    @JsonProperty
    public abstract TimerRateMetricsResponse converters();

    @JsonProperty
    public abstract long conditionHits();

    @JsonProperty
    public abstract long conditionMisses();

    public static ExtractorMetrics create(TimerRateMetricsResponse total,
                                          TimerRateMetricsResponse condition,
                                          TimerRateMetricsResponse execution,
                                          TimerRateMetricsResponse converters,
                                          long conditionHits,
                                          long conditionMisses) {
        return new AutoValue_ExtractorMetrics(total, condition, execution, converters, conditionHits, conditionMisses);
    }
}
