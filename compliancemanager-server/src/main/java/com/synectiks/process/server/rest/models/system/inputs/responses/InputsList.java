/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class InputsList {
    @JsonProperty
    public abstract Set<InputSummary> inputs();
    @JsonProperty
    public abstract int total();

    @JsonCreator
    public static InputsList create(@JsonProperty("inputs") Set<InputSummary> inputs) {
        return new AutoValue_InputsList(inputs, inputs.size());
    }
}
