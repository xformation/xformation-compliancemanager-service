/*
 * */
package com.synectiks.process.server.rest.resources.streams.outputs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;

import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class AvailableOutputSummary {
    @JsonProperty
    public abstract String name();

    @JsonProperty
    public abstract String type();

    @JsonProperty("human_name")
    public abstract String humanName();

    @JsonProperty("link_to_docs")
    public abstract String linkToDocs();

    @JsonProperty
    public abstract ConfigurationRequest requestedConfiguration();

    public static AvailableOutputSummary create(String name, String type, String humanName, String linkToDocs, ConfigurationRequest requestedConfiguration) {
        return new AutoValue_AvailableOutputSummary(name, type, humanName, linkToDocs, requestedConfiguration);
    }
}
