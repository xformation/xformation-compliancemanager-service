/*
 * */
package com.synectiks.process.server.rest.models.system.lookup;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.lookup.dto.DataAdapterDto;
import com.synectiks.process.server.plugin.lookup.LookupDataAdapterConfiguration;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.synectiks.process.server.lookup.dto.DataAdapterDto.FIELD_CUSTOM_ERROR_TTL;
import static com.synectiks.process.server.lookup.dto.DataAdapterDto.FIELD_CUSTOM_ERROR_TTL_ENABLED;
import static com.synectiks.process.server.lookup.dto.DataAdapterDto.FIELD_CUSTOM_ERROR_TTL_UNIT;

import java.util.concurrent.TimeUnit;

@AutoValue
@JsonAutoDetect
@WithBeanGetter
@JsonDeserialize(builder = AutoValue_DataAdapterApi.Builder.class)
public abstract class DataAdapterApi {

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

    @Nullable
    @JsonProperty(FIELD_CUSTOM_ERROR_TTL_ENABLED)
    public abstract Boolean customErrorTTLEnabled();

    @Nullable
    @JsonProperty(FIELD_CUSTOM_ERROR_TTL)
    public abstract Long customErrorTTL();

    @Nullable
    @JsonProperty(FIELD_CUSTOM_ERROR_TTL_UNIT)
    public abstract TimeUnit customErrorTTLUnit();

    @JsonProperty("content_pack")
    @Nullable
    public abstract String contentPack();

    @JsonProperty("config")
    @NotNull
    public abstract LookupDataAdapterConfiguration config();

    public static Builder builder() {
        return new AutoValue_DataAdapterApi.Builder();
    }

    public static DataAdapterApi fromDto(DataAdapterDto dto) {
        return builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .name(dto.name())
                .customErrorTTLEnabled(dto.customErrorTTLEnabled())
                .customErrorTTL(dto.customErrorTTL())
                .customErrorTTLUnit(dto.customErrorTTLUnit())
                .contentPack(dto.contentPack())
                .config(dto.config())
                .build();
    }

    public DataAdapterDto toDto() {
        return DataAdapterDto.builder()
                .id(id())
                .title(title())
                .description(description())
                .name(name())
                .customErrorTTLEnabled(customErrorTTLEnabled())
                .customErrorTTL(customErrorTTL())
                .customErrorTTLUnit(customErrorTTLUnit())
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

        @JsonProperty(FIELD_CUSTOM_ERROR_TTL_ENABLED)
        public abstract Builder customErrorTTLEnabled(@Nullable Boolean enabled);

        @JsonProperty(FIELD_CUSTOM_ERROR_TTL)
        public abstract Builder customErrorTTL(@Nullable Long ttl);

        @JsonProperty(FIELD_CUSTOM_ERROR_TTL_UNIT)
        public abstract Builder customErrorTTLUnit(@Nullable TimeUnit unit);

        @JsonProperty("content_pack")
        public abstract Builder contentPack(@Nullable String contentPack);

        @JsonProperty("config")
        public abstract Builder config(LookupDataAdapterConfiguration config);

        public abstract DataAdapterApi build();
    }
}
