/*
 * */
package com.synectiks.process.server.rest.models.system.indices;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class RetentionStrategySummary {
    @JsonProperty
    public abstract String strategy();

    @JsonProperty
    public abstract RetentionStrategyConfig config();

    @JsonCreator
    public static RetentionStrategySummary create(@JsonProperty("strategy") @NotEmpty String strategy,
                                                  @JsonProperty("config") @Valid @NotNull RetentionStrategyConfig config) {
        return new AutoValue_RetentionStrategySummary(strategy, config);
    }
}
