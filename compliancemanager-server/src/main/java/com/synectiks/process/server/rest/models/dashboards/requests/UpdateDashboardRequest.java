/*
 * */
package com.synectiks.process.server.rest.models.dashboards.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class UpdateDashboardRequest {
    @JsonProperty
    @Nullable
    public abstract String title();

    @JsonProperty
    @Nullable
    public abstract String description();

    @JsonCreator
    public static UpdateDashboardRequest create(@JsonProperty("title") @Nullable String title,
                                                @JsonProperty("description") @Nullable String description) {
        return new AutoValue_UpdateDashboardRequest(title, description);
    }
}
