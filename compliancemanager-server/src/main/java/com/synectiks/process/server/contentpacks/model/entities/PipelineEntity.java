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
import java.util.Collections;
import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class PipelineEntity {
    @JsonProperty("title")
    public abstract ValueReference title();

    @JsonProperty("description")
    @Nullable
    public abstract ValueReference description();

    @JsonProperty("source")
    public abstract ValueReference source();

    @JsonProperty("connected_streams")
    public abstract Set<ValueReference> connectedStreams();

    @JsonCreator
    public static PipelineEntity create(@JsonProperty("title") ValueReference title,
                                        @JsonProperty("description") @Nullable ValueReference description,
                                        @JsonProperty("source") ValueReference source,
                                        @JsonProperty("connected_streams") Set<ValueReference> connectedStreams) {
        return new AutoValue_PipelineEntity(title, description, source, connectedStreams == null ? Collections.emptySet() : connectedStreams);
    }
}
