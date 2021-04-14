/*
 * */
package com.synectiks.process.server.rest.models.roles.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class RolesResponse {

    @JsonProperty
    @NotNull
    public abstract Set<RoleResponse> roles();

    @JsonProperty
    public int total() {
        return roles().size();
    }

    @JsonCreator
    public static RolesResponse create(@JsonProperty("roles") @NotNull Set<RoleResponse> roles) {
        return new AutoValue_RolesResponse(roles);
    }
}
