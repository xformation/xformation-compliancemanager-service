/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class PipelineRuleEntity {
    @JsonProperty("title")
    public abstract ValueReference title();

    @JsonProperty("description")
    @Nullable
    public abstract ValueReference description();

    @JsonProperty("source")
    public abstract ValueReference source();

    @JsonCreator
    public static PipelineRuleEntity create(@JsonProperty("title") ValueReference title,
                                        @JsonProperty("description") @Nullable ValueReference description,
                                        @JsonProperty("source") ValueReference source) {
        return new AutoValue_PipelineRuleEntity(title, description, source);
    }
}
