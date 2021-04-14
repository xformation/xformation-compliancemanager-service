/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RuleMetricsConfigChangedEvent {
    @JsonProperty("enabled")
    public abstract boolean enabled();

    @JsonCreator
    public static RuleMetricsConfigChangedEvent create(@JsonProperty("enabled") boolean enabled) {
        return new AutoValue_RuleMetricsConfigChangedEvent(enabled);
    }
}
