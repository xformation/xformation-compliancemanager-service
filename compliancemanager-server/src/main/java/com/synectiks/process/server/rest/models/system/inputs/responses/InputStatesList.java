/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class InputStatesList {
    @JsonProperty("states")
    public abstract Set<InputStateSummary> states();

    @JsonCreator
    public static InputStatesList create(@JsonProperty("states") Set<InputStateSummary> states) {
        return new AutoValue_InputStatesList(states);
    }
}
