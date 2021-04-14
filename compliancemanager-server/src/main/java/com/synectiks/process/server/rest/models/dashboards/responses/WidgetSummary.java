/*
 * */
package com.synectiks.process.server.rest.models.dashboards.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class WidgetSummary {
    @JsonProperty
    public abstract String id();

    @JsonProperty
    public abstract String description();

    @JsonProperty
    public abstract String type();

    @JsonProperty("cache_time")
    public abstract int cacheTime();

    @JsonProperty("creator_user_id")
    public abstract String creatorUserId();

    @JsonProperty
    public abstract Map<String, Object> config();

    @JsonCreator
    public static WidgetSummary create(@JsonProperty("id") String id,
                                       @JsonProperty("description") String description,
                                       @JsonProperty("type") String type,
                                       @JsonProperty("cache_time") int cacheTime,
                                       @JsonProperty("creator_user_id") String creatorUserId,
                                       @JsonProperty("config") Map<String, Object> config) {
        return new AutoValue_WidgetSummary(id, description, type, cacheTime, creatorUserId, config);
    }
}
