/*
 * */
package com.synectiks.process.server.rest.models.users.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class PermissionEditRequest {
    @JsonProperty
    public abstract List<String> permissions();

    @JsonCreator
    public static PermissionEditRequest create(@JsonProperty("permissions") @NotNull List<String> permissions) {
        return new AutoValue_PermissionEditRequest(permissions);
    }
}
