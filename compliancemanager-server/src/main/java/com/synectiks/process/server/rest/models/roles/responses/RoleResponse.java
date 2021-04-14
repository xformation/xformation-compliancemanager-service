/*
 * */
package com.synectiks.process.server.rest.models.roles.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class RoleResponse {

    @JsonProperty
    @NotBlank
    public abstract String name();

    @JsonProperty
    public abstract Optional<String> description();

    @JsonProperty
    @NotNull
    public abstract Set<String> permissions();

    @JsonProperty
    public abstract boolean readOnly();

    @JsonCreator
    public static RoleResponse create(@JsonProperty("name") @NotBlank String name,
                                      @JsonProperty("description") Optional<String> description,
                                      @JsonProperty("permissions") @NotNull Set<String> permissions,
                                      @JsonProperty("read_only") boolean readOnly) {
        return new AutoValue_RoleResponse(name, description, permissions, readOnly);
    }
}
