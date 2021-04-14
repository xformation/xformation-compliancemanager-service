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
public abstract class SidecarCollectorEntity {
    @JsonProperty("name")
    public abstract ValueReference name();

    @JsonProperty("service_type")
    public abstract ValueReference serviceType();

    @JsonProperty("node_operating_system")
    public abstract ValueReference nodeOperatingSystem();

    @JsonProperty("executable_path")
    public abstract ValueReference executablePath();

    @JsonProperty("execute_parameters")
    public abstract ValueReference executeParameters();

    @JsonProperty("validation_parameters")
    public abstract ValueReference validationParameters();

    @JsonProperty("default_template")
    public abstract ValueReference defaultTemplate();

    @JsonCreator
    public static SidecarCollectorEntity create(@JsonProperty("name") ValueReference name,
                                         @JsonProperty("service_type") ValueReference serviceType,
                                         @JsonProperty("node_operating_system") ValueReference nodeOperatingSystem,
                                         @JsonProperty("executable_path") ValueReference executablePath,
                                         @JsonProperty("execute_parameters") ValueReference executeParameters,
                                         @JsonProperty("validation_parameters") ValueReference validationParameters,
                                         @JsonProperty("default_template") ValueReference defaultTemplate) {
        return new AutoValue_SidecarCollectorEntity(name,
                serviceType,
                nodeOperatingSystem,
                executablePath,
                executeParameters,
                validationParameters,
                defaultTemplate);
    }
}
