/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class InputDeleted {

    @JsonProperty
    public abstract String id();

    @JsonCreator
    public static InputDeleted create(@JsonProperty("id") String id) {
        return new AutoValue_InputDeleted(id);
    }
}
