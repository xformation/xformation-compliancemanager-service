/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotEmpty;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class CreateStaticFieldRequest {
    @JsonProperty
    public abstract String key();

    @JsonProperty
    public abstract String value();

    @JsonCreator
    public static CreateStaticFieldRequest create(@JsonProperty("key") @NotEmpty String key,
                                                  @JsonProperty("value") @NotEmpty String value) {
        return new AutoValue_CreateStaticFieldRequest(key, value);
    }
}
