/*
 * */
package com.synectiks.process.server.rest.models.system.lookup;

import com.google.auto.value.AutoValue;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

import javax.annotation.Nullable;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
@JsonDeserialize(builder = AutoValue_ErrorStatesRequest.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ErrorStatesRequest {

    @Nullable
    @JsonProperty("tables")
    public abstract Set<String > tables();

    @Nullable
    @JsonProperty("data_adapters")
    public abstract Set<String > dataAdapters();

    @Nullable
    @JsonProperty("caches")
    public abstract Set<String > caches();

    public static Builder builder() {
        return new AutoValue_ErrorStatesRequest.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("tables")
        public abstract Builder tables(@Nullable Set<String> tables);

        @JsonProperty("data_adapters")
        public abstract Builder dataAdapters(@Nullable Set<String> dataAdapters);

        @JsonProperty("caches")
        public abstract Builder caches(@Nullable Set<String> caches);

        public abstract ErrorStatesRequest build();
    }
}
