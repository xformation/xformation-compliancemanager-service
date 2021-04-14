/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotBlank;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class GrokPatternEntity {
    @JsonProperty("name")
    public abstract String name();

    @JsonProperty("pattern")
    public abstract String pattern();

    @JsonCreator
    public static GrokPatternEntity create(@JsonProperty("name") @NotBlank String name,
                                           @JsonProperty("pattern") @NotBlank String pattern) {
        return new AutoValue_GrokPatternEntity(name, pattern);
    }
}
