/*
 * */
package com.synectiks.process.server.rest.models.users.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ChangeUserRequest {
    @JsonProperty
    @Nullable
    public abstract String email();

    @JsonProperty
    @Nullable
    public abstract String fullName();

    @JsonProperty
    @Nullable
    public abstract List<String> permissions();

    @JsonProperty
    @Nullable
    public abstract String timezone();

    @JsonProperty
    @Nullable
    public abstract Startpage startpage();

    @JsonProperty
    @Nullable
    public abstract Long sessionTimeoutMs();

    @JsonProperty
    @Nullable
    public abstract List<String> roles();

    @JsonCreator
    public static ChangeUserRequest create(@JsonProperty("email") @Nullable @Email String email,
                                           @JsonProperty("full_name") @Nullable String fullName,
                                           @JsonProperty("permissions") @Nullable List<String> permissions,
                                           @JsonProperty("timezone") @Nullable String timezone,
                                           @JsonProperty("startpage") @Nullable @Valid Startpage startpage,
                                           @JsonProperty("session_timeout_ms") @Nullable @Min(1) Long sessionTimeoutMs,
                                           @JsonProperty("roles") @Nullable List<String> roles) {
        return new AutoValue_ChangeUserRequest(email, fullName, permissions, timezone, startpage, sessionTimeoutMs, roles);
    }
}