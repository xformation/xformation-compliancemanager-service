/*
 * */
package com.synectiks.process.server.streams.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class StreamsChangedEvent {
    private static final String FIELD_STREAM_IDS = "stream_ids";

    @JsonProperty(FIELD_STREAM_IDS)
    public abstract ImmutableSet<String> streamIds();

    @JsonCreator
    public static StreamsChangedEvent create(@JsonProperty(FIELD_STREAM_IDS) ImmutableSet<String> streamIds) {
        return new AutoValue_StreamsChangedEvent(streamIds);
    }

    public static StreamsChangedEvent create(String streamId) {
        return create(ImmutableSet.of(streamId));
    }
}