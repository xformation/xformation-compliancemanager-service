/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;

import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.Period;

import javax.validation.constraints.NotNull;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class TimeBasedRotationStrategyConfig implements RotationStrategyConfig {
    private static final Period DEFAULT_DAYS = Period.days(1);

    @JsonProperty("rotation_period")
    public abstract Period rotationPeriod();

    @JsonCreator
    public static TimeBasedRotationStrategyConfig create(@JsonProperty(TYPE_FIELD) String type,
                                                         @JsonProperty("rotation_period") @NotNull Period maxTimePerIndex) {
        return new AutoValue_TimeBasedRotationStrategyConfig(type, maxTimePerIndex);
    }

    @JsonCreator
    public static TimeBasedRotationStrategyConfig create(@JsonProperty("rotation_period") @NotNull Period maxTimePerIndex) {
        return create(TimeBasedRotationStrategyConfig.class.getCanonicalName(), maxTimePerIndex);
    }

    public static TimeBasedRotationStrategyConfig createDefault() {
        return create(DEFAULT_DAYS);
    }
}
