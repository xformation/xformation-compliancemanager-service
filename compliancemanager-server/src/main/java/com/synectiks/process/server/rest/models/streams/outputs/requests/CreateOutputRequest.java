/*
 * */
package com.synectiks.process.server.rest.models.streams.outputs.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class CreateOutputRequest {
    @JsonProperty
    public abstract String title();
    @JsonProperty
    public abstract String type();
    @JsonProperty
    public abstract Map<String, Object> configuration();
    @JsonProperty
    @Nullable
    public abstract Set<String> streams();
    @JsonProperty
    @Nullable
    public abstract String contentPack();

    @JsonCreator
    public static CreateOutputRequest create(@JsonProperty("title") String title,
                                             @JsonProperty("type") String type,
                                             @JsonProperty("configuration") Map<String, Object> configuration,
                                             @JsonProperty("streams") @Nullable Set<String> streams,
                                             @JsonProperty("content_pack") @Nullable String contentPack) {
        return new AutoValue_CreateOutputRequest(title, type, configuration, streams, contentPack);
    }

    public static CreateOutputRequest create(@JsonProperty("title") String title,
                                             @JsonProperty("type") String type,
                                             @JsonProperty("configuration") Map<String, Object> configuration,
                                             @JsonProperty("streams") @Nullable Set<String> streams) {
        return create(title, type, configuration, streams, null);
    }
}
