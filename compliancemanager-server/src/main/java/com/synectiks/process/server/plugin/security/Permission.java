/*
 * */
package com.synectiks.process.server.plugin.security;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotBlank;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class Permission {
    @JsonProperty("permission")
    public abstract String permission();

    @JsonProperty("description")
    public abstract String description();

    @JsonCreator
    public static Permission create(@JsonProperty("permission") @NotBlank String permission,
                                    @JsonProperty("description") String description) {
        return new AutoValue_Permission(permission, description);
    }
}
