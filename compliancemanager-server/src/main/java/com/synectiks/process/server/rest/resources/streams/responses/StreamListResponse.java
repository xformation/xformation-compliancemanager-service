/*
 * */
package com.synectiks.process.server.rest.resources.streams.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Collection;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class StreamListResponse {
    @JsonProperty
    public abstract long total();

    @JsonProperty
    public abstract Collection<StreamResponse> streams();

    @JsonCreator
    public static StreamListResponse create(@JsonProperty("total") long total, @JsonProperty("streams") Collection<StreamResponse> streams) {
        return new AutoValue_StreamListResponse(total, streams);
    }
}
