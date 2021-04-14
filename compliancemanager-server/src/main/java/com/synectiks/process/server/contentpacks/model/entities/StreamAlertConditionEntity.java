/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

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
public abstract class StreamAlertConditionEntity {
    @JsonProperty("type")
    @NotBlank
    public abstract String type();

    @JsonProperty("title")
    @NotBlank
    public abstract ValueReference title();

    @JsonProperty("parameters")
    @NotNull
    public abstract ReferenceMap parameters();

    @JsonCreator
    public static StreamAlertConditionEntity create(
            @JsonProperty("type") @NotBlank String type,
            @JsonProperty("title") @NotBlank ValueReference title,
            @JsonProperty("parameters") @NotNull ReferenceMap parameters) {
        return new AutoValue_StreamAlertConditionEntity(type, title, parameters);
    }
}