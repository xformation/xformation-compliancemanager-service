/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Set;

@AutoValue
@JsonAutoDetect
public abstract class PipelineReverseConnections {
    @JsonProperty
    public abstract String pipelineId();

    @JsonProperty
    public abstract Set<String> streamIds();

    @JsonCreator
    public static PipelineReverseConnections create(@JsonProperty("pipeline_id") String pipelineId,
                                                    @JsonProperty("stream_ids") Set<String> streamIds) {
        return new AutoValue_PipelineReverseConnections(pipelineId, streamIds);
    }
}
