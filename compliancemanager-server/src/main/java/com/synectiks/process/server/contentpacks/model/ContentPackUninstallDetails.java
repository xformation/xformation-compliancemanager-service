/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class ContentPackUninstallDetails {
    public static final String FIELD_ENTITES = "entities";

    @JsonProperty(FIELD_ENTITES)
    public abstract Set<NativeEntityDescriptor> entities();

    public static ContentPackUninstallDetails create(@JsonProperty(FIELD_ENTITES) Set<NativeEntityDescriptor> entities) {
        return new AutoValue_ContentPackUninstallDetails(entities);
    }
}
