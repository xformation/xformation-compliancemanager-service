/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.Reference;
import com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMap;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class InputEntity {
    @JsonProperty("title")
    @NotBlank
    public abstract ValueReference title();

    @JsonProperty("configuration")
    @NotNull
    public abstract ReferenceMap configuration();

    @JsonProperty("static_fields")
    @NotNull
    public abstract Map<String, ValueReference> staticFields();

    @JsonProperty("type")
    @NotBlank
    public abstract ValueReference type();

    @JsonProperty("global")
    public abstract ValueReference global();

    @JsonProperty("extractors")
    @NotNull
    public abstract List<ExtractorEntity> extractors();

    @JsonCreator
    public static InputEntity create(
            @JsonProperty("title") @NotBlank ValueReference title,
            @JsonProperty("configuration") @NotNull ReferenceMap configuration,
            @JsonProperty("static_fields") @NotNull Map<String, ValueReference> staticFields,
            @JsonProperty("type") @NotBlank ValueReference type,
            @JsonProperty("global") ValueReference global,
            @JsonProperty("extractors") @NotNull List<ExtractorEntity> extractors) {
        return new AutoValue_InputEntity(title, configuration, staticFields, type, global, extractors);
    }
}
