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
public abstract class InputUpdated {
    @JsonProperty
    public abstract String id();

    @JsonCreator
    public static InputUpdated create(@JsonProperty("id") String inputId) {
        return new AutoValue_InputUpdated(inputId);
    }
}