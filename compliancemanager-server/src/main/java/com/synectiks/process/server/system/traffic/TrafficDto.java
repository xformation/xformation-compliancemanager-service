/*
 * */
package com.synectiks.process.server.system.traffic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.joda.time.DateTime;
import org.mongojack.Id;
import org.mongojack.ObjectId;

import java.util.Collections;
import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AutoValue_TrafficDto.Builder.class)
public abstract class TrafficDto {

    @Id
    @ObjectId
    @JsonProperty
    public abstract String id();

    @JsonProperty
    public abstract DateTime bucket();

    @JsonProperty
    public abstract Map<String, Long> input();

    @JsonProperty
    public abstract Map<String, Long> output();

    @JsonProperty
    public abstract Map<String, Long> decoded();

    public static Builder builder() {
        return new AutoValue_TrafficDto.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public Builder() {
            decoded(Collections.emptyMap());
        }

        @Id
        @ObjectId
        @JsonProperty
        public abstract Builder id(String id);

        @JsonProperty
        public abstract Builder bucket(DateTime bucket);

        @JsonProperty
        public abstract Builder input(Map<String, Long> inputTraffic);

        @JsonProperty
        public abstract Builder output(Map<String, Long> outputTraffic);

        @JsonProperty
        public abstract Builder decoded(Map<String, Long> decodedTraffic);

        public abstract TrafficDto build();
    }
}
