/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Set;

@JsonAutoDetect
@AutoValue
public abstract class PipelineConnectionsChangedEvent {
    @JsonProperty("stream_id")
    public abstract String streamId();

    @JsonProperty("pipeline_ids")
    public abstract Set<String> pipelineIds();

    @JsonCreator
    public static PipelineConnectionsChangedEvent create(@JsonProperty("stream_id") String streamId,
                                                         @JsonProperty("pipeline_ids") Set<String> pipelineIds) {
        return new AutoValue_PipelineConnectionsChangedEvent(streamId, pipelineIds);
    }
}
