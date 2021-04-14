/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class InputCreateRequest {
    @JsonProperty
    public abstract String title();

    @JsonProperty
    public abstract String type();

    @JsonProperty
    public abstract boolean global();

    @JsonProperty
    public abstract Map<String, Object> configuration();

    @JsonProperty
    @Nullable
    public abstract String node();

    @JsonCreator
    public static InputCreateRequest create(@JsonProperty("title") String title,
                                            @JsonProperty("type") String type,
                                            @JsonProperty("global") boolean global,
                                            @JsonProperty("configuration") Map<String, Object> configuration,
                                            @JsonProperty("node") String node) {
        return new AutoValue_InputCreateRequest(title, type, global, configuration, node);
    }
}