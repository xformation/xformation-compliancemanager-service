/*
 * */
package com.synectiks.process.server.rest.models.system.deflector.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class DeflectorSummary {
    @JsonProperty("is_up")
    public abstract boolean isUp();

    @JsonProperty("current_target")
    @Nullable
    public abstract String currentTarget();

    @JsonCreator
    public static DeflectorSummary create(@JsonProperty("is_up") boolean isUp,
                                          @JsonProperty("current_target") @Nullable String currentTarget) {
        return new AutoValue_DeflectorSummary(isUp, currentTarget);
    }
}
