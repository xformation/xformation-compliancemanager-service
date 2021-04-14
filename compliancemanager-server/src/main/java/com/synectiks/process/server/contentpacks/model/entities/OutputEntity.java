/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMap;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class OutputEntity {
    @JsonProperty("title")
    @NotBlank
    public abstract ValueReference title();

    @JsonProperty("type")
    @NotBlank
    public abstract ValueReference type();

    @JsonProperty("configuration")
    @NotNull
    public abstract ReferenceMap configuration();

    @JsonCreator
    public static OutputEntity create(
            @JsonProperty("title") @NotBlank ValueReference title,
            @JsonProperty("type") @NotBlank ValueReference type,
            @JsonProperty("configuration") @NotNull ReferenceMap configuration) {
        return new AutoValue_OutputEntity(title, type, configuration);
    }
}
