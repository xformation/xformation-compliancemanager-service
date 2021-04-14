/*
 * */
package com.synectiks.process.common.security.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ActiveAuthServiceBackendChangedEvent {
    public static final String FIELD_ACTIVE_BACKEND = "active_backend";

    @JsonProperty(FIELD_ACTIVE_BACKEND)
    public abstract String activeBackend();

    @JsonCreator
    public static ActiveAuthServiceBackendChangedEvent create(@JsonProperty(FIELD_ACTIVE_BACKEND) String activeBackend) {
        return new AutoValue_ActiveAuthServiceBackendChangedEvent(activeBackend);
    }
}
