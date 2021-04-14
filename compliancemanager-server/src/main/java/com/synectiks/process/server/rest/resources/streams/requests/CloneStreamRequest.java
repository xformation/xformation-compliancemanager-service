/*
 * */
package com.synectiks.process.server.rest.resources.streams.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class CloneStreamRequest {
    @JsonProperty
    public abstract String title();

    @JsonProperty
    public abstract String description();

    @JsonProperty("remove_matches_from_default_stream")
    public abstract boolean removeMatchesFromDefaultStream();

    @JsonProperty("index_set_id")
    public abstract String indexSetId();

    @JsonCreator
    public static CloneStreamRequest create(@JsonProperty("title") String title,
                                            @JsonProperty("description") String description,
                                            @JsonProperty("remove_matches_from_default_stream") boolean removeMatchesFromDefaultStream,
                                            @JsonProperty("index_set_id") String indexSetId) {
        return new AutoValue_CloneStreamRequest(title, description, removeMatchesFromDefaultStream, indexSetId);
    }
}
