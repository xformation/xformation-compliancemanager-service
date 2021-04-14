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
public abstract class RetentionStrategies {
    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract Set<RetentionStrategyDescription> strategies();

    @JsonCreator
    public static RetentionStrategies create(@JsonProperty("total") int total,
                                             @JsonProperty("strategies") Set<RetentionStrategyDescription> strategies) {
        return new AutoValue_RetentionStrategies(total, strategies);
    }
}
