/*
 * */
package com.synectiks.process.server.rest.models.system.lookup;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.lookup.dto.CacheDto;
import com.synectiks.process.server.plugin.lookup.LookupCacheConfiguration;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AutoValue
@JsonAutoDetect
@WithBeanGetter
@JsonDeserialize(builder = AutoValue_CacheApi.Builder.class)
public abstract class CacheApi {

    @Nullable
    @JsonProperty("id")
    public abstract String id();

    @JsonProperty("title")
    @NotEmpty
    public abstract String title();

    @JsonProperty("description")
    public abstract String description();

    @JsonProperty("name")
    @NotEmpty
    public abstract String name();

    @JsonProperty("content_pack")
    @Nullable
    public abstract String contentPack();

    @JsonProperty
    @NotNull
    public abstract LookupCacheConfiguration config();

    public static Builder builder() {
        return new AutoValue_CacheApi.Builder();
    }

    public static CacheApi fromDto(CacheDto dto) {
        return builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .name(dto.name())
                .contentPack(dto.contentPack())
                .config(dto.config())
                .build();
    }

    public CacheDto toDto() {
        return CacheDto.builder()
                .id(id())
                .title(title())
                .description(description())
                .name(name())
                .contentPack(contentPack())
                .config(config())
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("id")
        public abstract Builder id(@Nullable String id);

        @JsonProperty("title")
        public abstract Builder title(String title);

        @JsonProperty("description")
        public abstract Builder description(String description);

        @JsonProperty("name")
        public abstract Builder name(String name);

        @JsonProperty("content_pack")
        public abstract Builder contentPack(@Nullable String contentPack);

        @JsonProperty("config")
        public abstract Builder config(@Valid LookupCacheConfiguration config);

        public abstract CacheApi build();
    }
}
