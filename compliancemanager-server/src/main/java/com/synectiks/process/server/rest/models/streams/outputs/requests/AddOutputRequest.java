/*
 * */
package com.synectiks.process.server.rest.models.streams.outputs.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class AddOutputRequest {
    @JsonProperty
    public abstract Set<String> outputs();

    @JsonCreator
    public static AddOutputRequest create(@JsonProperty("outputs") @NotEmpty Set<String> outputs) {
        return new AutoValue_AddOutputRequest(outputs);
    }
}
