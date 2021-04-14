/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class InputCreated {
    @JsonProperty
    public abstract String id();

    @JsonCreator
    public static InputCreated create(@JsonProperty("id") String id) {
        return new AutoValue_InputCreated(id);
    }
}
