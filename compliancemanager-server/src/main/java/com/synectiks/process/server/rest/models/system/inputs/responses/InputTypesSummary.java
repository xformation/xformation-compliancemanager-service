/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

/**
 * Created by dennis on 12/12/14.
 */
@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class InputTypesSummary {
    @JsonProperty
    public abstract Map<String, String> types();

    @JsonCreator
    public static InputTypesSummary create(@JsonProperty("types") Map<String, String> types) {
        return new AutoValue_InputTypesSummary(types);
    }
}
