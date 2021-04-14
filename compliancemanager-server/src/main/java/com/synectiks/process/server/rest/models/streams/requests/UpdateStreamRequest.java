/*
 * */
package com.synectiks.process.server.rest.models.streams.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UpdateStreamRequest {
    @JsonProperty
    @Nullable
    public abstract String title();

    @JsonProperty
    @Nullable
    public abstract String description();

    @JsonProperty("matching_type")
    @Nullable
    public abstract String matchingType();

    @JsonProperty("remove_matches_from_default_stream")
    @Nullable
    public abstract Boolean removeMatchesFromDefaultStream();

    @JsonProperty("index_set_id")
    @Nullable
    public abstract String indexSetId();

    @JsonCreator
    public static UpdateStreamRequest create(@JsonProperty("title") @Nullable String title,
                                             @JsonProperty("description") @Nullable String description,
                                             @JsonProperty("matching_type") @Nullable String matchingType,
                                             @JsonProperty("rules") @Nullable List rules,
                                             @JsonProperty("remove_matches_from_default_stream") @Nullable Boolean removeMatchesFromDefaultStream,
                                             @JsonProperty("index_set_id") @Nullable String indexSetId) {
        return new AutoValue_UpdateStreamRequest(title, description, matchingType, removeMatchesFromDefaultStream, indexSetId);
    }
}
