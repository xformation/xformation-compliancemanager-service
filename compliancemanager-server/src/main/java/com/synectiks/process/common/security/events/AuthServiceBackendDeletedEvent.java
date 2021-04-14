/*
 * */
package com.synectiks.process.common.security.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AuthServiceBackendDeletedEvent {
    private static final String FIELD_AUTH_SERVICE_ID = "auth_service_id";

    @JsonProperty(FIELD_AUTH_SERVICE_ID)
    public abstract String authServiceId();

    @JsonCreator
    public static AuthServiceBackendDeletedEvent create(@JsonProperty(FIELD_AUTH_SERVICE_ID) String authServiceId) {
        return new AutoValue_AuthServiceBackendDeletedEvent(authServiceId);
    }
}
