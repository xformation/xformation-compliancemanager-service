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
import javax.validation.constraints.NotNull;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class StreamRuleEntity {
    @JsonProperty("type")
    @NotNull
    public abstract ValueReference type();

    @JsonProperty("field")
    @NotBlank
    public abstract ValueReference field();

    @JsonProperty("value")
    @NotNull
    public abstract ValueReference value();

    @JsonProperty("inverted")
    public abstract ValueReference inverted();

    @JsonProperty("description")
    public abstract ValueReference description();

    @JsonCreator
    public static StreamRuleEntity create(
            @JsonProperty("type") @NotNull ValueReference type,
            @JsonProperty("field") @NotBlank ValueReference field,
            @JsonProperty("value") @NotNull ValueReference value,
            @JsonProperty("inverted") ValueReference inverted,
            @JsonProperty("description") ValueReference description) {
        return new AutoValue_StreamRuleEntity(type, field, value, inverted, description);
    }
}
