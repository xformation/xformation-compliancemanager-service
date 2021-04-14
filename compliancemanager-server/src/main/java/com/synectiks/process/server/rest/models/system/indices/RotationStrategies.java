/*
 * */
package com.synectiks.process.server.rest.models.system.indices;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class RotationStrategies {
    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract Set<RotationStrategyDescription> strategies();

    @JsonCreator
    public static RotationStrategies create(@JsonProperty("total") int total,
                                            @JsonProperty("strategies") Set<RotationStrategyDescription> strategies) {
        return new AutoValue_RotationStrategies(total, strategies);
    }
}
