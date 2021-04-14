/*
 * */
package com.synectiks.process.server.rest.models.users.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ChangePasswordRequest {
    @JsonProperty
    @Nullable
    public abstract String oldPassword();

    @JsonProperty
    public abstract String password();

    @JsonCreator
    public static ChangePasswordRequest create(@JsonProperty("old_password") @Nullable String oldPassword,
                                               @JsonProperty("password") @NotEmpty String password) {
        return new AutoValue_ChangePasswordRequest(oldPassword, password);
    }
}
