/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
public abstract class SidecarRegistrationConfiguration {
    @JsonProperty
    public abstract long updateInterval();

    @JsonProperty
    public abstract boolean sendStatus();

    @JsonCreator
    public static SidecarRegistrationConfiguration create(@JsonProperty("update_interval") long updateInterval,
                                                          @JsonProperty("send_status") boolean sendStatus) {
        return new AutoValue_SidecarRegistrationConfiguration(updateInterval, sendStatus);
    }
}
