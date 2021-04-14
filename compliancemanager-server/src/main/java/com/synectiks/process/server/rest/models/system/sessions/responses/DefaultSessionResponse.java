/*
 * */
package com.synectiks.process.server.rest.models.system.sessions.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Date;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class DefaultSessionResponse {
    @JsonProperty("valid_until")
    public abstract Date validUntil();

    @JsonProperty("session_id")
    public abstract String sessionId();

    @JsonProperty("username")
    public abstract String username();

    @JsonProperty("user_id")
    public abstract String userId();

    @JsonCreator
    public static DefaultSessionResponse create(@JsonProperty("valid_until") Date validUntil,
                                                @JsonProperty("session_id") String sessionId,
                                                @JsonProperty("username") String username,
                                                @JsonProperty("user_id") String userId) {
        return new AutoValue_DefaultSessionResponse(validUntil, sessionId, username, userId);
    }
}
