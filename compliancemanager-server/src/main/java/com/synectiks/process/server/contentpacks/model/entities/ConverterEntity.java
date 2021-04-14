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

import java.util.Map;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class ConverterEntity {
    @JsonProperty("type")
    public abstract ValueReference type();

    @JsonProperty("configuration")
    public abstract ReferenceMap configuration();

    @JsonCreator
    public static ConverterEntity create(@JsonProperty("type") ValueReference type,
                                         @JsonProperty("configuration") ReferenceMap configuration) {
        return new AutoValue_ConverterEntity(type, configuration);
    }
}
