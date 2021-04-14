/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class SidecarCollectorConfigurationEntity {
    @JsonProperty("collector_id")
    public abstract ValueReference collectorId();

    @JsonProperty("title")
    public abstract ValueReference title();

    @JsonProperty("color")
    public abstract ValueReference color();

    @JsonProperty("template")
    public abstract ValueReference template();

    @JsonCreator
    public static SidecarCollectorConfigurationEntity create(@JsonProperty("collector_id") ValueReference collectorId,
                                                      @JsonProperty("title") ValueReference title,
                                                      @JsonProperty("color") ValueReference color,
                                                      @JsonProperty("template") ValueReference template) {
        return new AutoValue_SidecarCollectorConfigurationEntity(collectorId, title, color, template);
    }
}