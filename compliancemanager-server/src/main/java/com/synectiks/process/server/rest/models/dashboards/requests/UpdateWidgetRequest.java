/*
 * */
package com.synectiks.process.server.rest.models.dashboards.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class UpdateWidgetRequest {
    @Nullable
    @JsonProperty
    public abstract String description();

    @JsonProperty("cache_time")
    public abstract int cacheTime();

    @JsonCreator
    public static UpdateWidgetRequest create(@JsonProperty("description") @Nullable String description,
                                             @JsonProperty("cache_time") @Min(0) int cacheTime) {
        return new AutoValue_UpdateWidgetRequest(description, cacheTime);
    }
}
