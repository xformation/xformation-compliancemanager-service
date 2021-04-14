/*
 * */
package com.synectiks.process.server.rest.models.roles.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.rest.models.users.responses.UserSummary;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class RoleMembershipResponse {

    @JsonProperty
    @NotBlank
    public abstract String role();

    @JsonProperty
    @NotNull
    public abstract Collection<UserSummary> users();

    @JsonCreator
    public static RoleMembershipResponse create(@JsonProperty("role") @NotBlank String roleName, @JsonProperty("users") @NotNull Collection<UserSummary> users) {
        return new AutoValue_RoleMembershipResponse(roleName, users);
    }
}
