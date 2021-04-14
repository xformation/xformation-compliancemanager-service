/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = RuleMetricsConfigDto.Builder.class)
public abstract class RuleMetricsConfigDto {
    private static final String FIELD_METRICS_ENABLED = "metrics_enabled";

    @JsonProperty(FIELD_METRICS_ENABLED)
    public abstract boolean metricsEnabled();

    public static Builder builder() {
        return Builder.create();
    }

    public static RuleMetricsConfigDto createDefault() {
        return builder().build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_RuleMetricsConfigDto.Builder().metricsEnabled(false);
        }

        @JsonProperty(FIELD_METRICS_ENABLED)
        public abstract Builder metricsEnabled(boolean metricsEnabled);

        public abstract RuleMetricsConfigDto build();
    }
}
