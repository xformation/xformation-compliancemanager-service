/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMap;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AutoValue
@WithBeanGetter
public abstract class StreamAlarmCallbackEntity {
    @JsonProperty("type")
    @NotBlank
    public abstract String type();

    @JsonProperty("title")
    @NotBlank
    public abstract ValueReference title();

    @JsonProperty("stream_id")
    public abstract String streamId();

    @JsonProperty("configuration")
    @NotNull
    public abstract ReferenceMap configuration();
    @JsonCreator
    public static StreamAlarmCallbackEntity create(@JsonProperty("type") @NotBlank String type,
                                                   @JsonProperty("title") @NotBlank ValueReference title,
                                                   @JsonProperty("stream_id") @NotBlank String streamId,
                                                   @JsonProperty("configuration") @NotNull ReferenceMap configuration) {
        return new AutoValue_StreamAlarmCallbackEntity(type, title, streamId, configuration);
    }
}