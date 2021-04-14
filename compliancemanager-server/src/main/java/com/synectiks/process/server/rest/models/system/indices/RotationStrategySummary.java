/*
 * */
package com.synectiks.process.server.rest.models.system.indices;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class RotationStrategySummary {
    @JsonProperty
    public abstract String strategy();

    @JsonProperty
    public abstract RotationStrategyConfig config();

    @JsonCreator
    public static RotationStrategySummary create(@JsonProperty("strategy") @NotEmpty String strategy,
                                                 @JsonProperty("config") @Valid @NotNull RotationStrategyConfig config) {
        return new AutoValue_RotationStrategySummary(strategy, config);
    }
}
