/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ReaderPermissionResponse {
    @JsonProperty
    public abstract List<String> permissions();

    @JsonCreator
    public static ReaderPermissionResponse create(@JsonProperty("permissions") List<String> permissions) {
        return new AutoValue_ReaderPermissionResponse(permissions);
    }
}
