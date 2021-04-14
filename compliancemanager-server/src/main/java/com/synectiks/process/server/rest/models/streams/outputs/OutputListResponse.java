/*
 * */
package com.synectiks.process.server.rest.models.streams.outputs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.rest.models.system.outputs.responses.OutputSummary;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Collection;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class OutputListResponse {
    @JsonProperty
    public abstract long total();

    @JsonProperty
    public abstract Collection<OutputSummary> outputs();

    @JsonCreator
    public static OutputListResponse create(@JsonProperty("total") long total, @JsonProperty("outputs") Collection<OutputSummary> outputs) {
        return new AutoValue_OutputListResponse(total, outputs);
    }

    public static OutputListResponse create(Collection<OutputSummary> outputs) {
        return new AutoValue_OutputListResponse(outputs.size(), outputs);
    }
}
