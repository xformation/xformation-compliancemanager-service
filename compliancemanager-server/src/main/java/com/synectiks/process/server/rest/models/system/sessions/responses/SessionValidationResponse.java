/*
 * */
package com.synectiks.process.server.rest.models.system.sessions.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class SessionValidationResponse {
    @JsonProperty("is_valid")
    public abstract boolean isValid();

    @JsonProperty("session_id")
    @Nullable
    public abstract String sessionId();

    @JsonProperty("username")
    @Nullable
    public abstract String username();

    @JsonCreator
    public static SessionValidationResponse create(
            @JsonProperty("is_valid") boolean isValid,
            @JsonProperty("session_id") @Nullable String newSessionId,
            @JsonProperty("username") @Nullable String username) {
        return new AutoValue_SessionValidationResponse(isValid, newSessionId, username);
    }

    public static SessionValidationResponse valid() {
        return new AutoValue_SessionValidationResponse(true, null, null);
    }

    public static SessionValidationResponse validWithNewSession(String newSessionId, String username) {
        return new AutoValue_SessionValidationResponse(true, newSessionId, username);
    }

    public static SessionValidationResponse invalid() {
        return new AutoValue_SessionValidationResponse(false, null, null);
    }
}
