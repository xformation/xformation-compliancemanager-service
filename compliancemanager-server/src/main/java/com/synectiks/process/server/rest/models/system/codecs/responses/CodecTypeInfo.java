/*
 * */
package com.synectiks.process.server.rest.models.system.codecs.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class CodecTypeInfo {
    @JsonProperty
    public abstract String type();

    @JsonProperty
    public abstract String name();

    @JsonProperty
    public abstract Map<String, Map<String, Object>> requestedConfiguration();

    @JsonCreator
    public static CodecTypeInfo create(@JsonProperty("type") String type,
                                       @JsonProperty("name") String name,
                                       @JsonProperty("requested_configuration") Map<String, Map<String, Object>> requestedConfiguration) {
        return new AutoValue_CodecTypeInfo(type, name, requestedConfiguration);
    }

    public static CodecTypeInfo fromConfigurationRequest(String type, String name, ConfigurationRequest configurationRequest) {
        return create(type, name, configurationRequest.asList());
    }
}
