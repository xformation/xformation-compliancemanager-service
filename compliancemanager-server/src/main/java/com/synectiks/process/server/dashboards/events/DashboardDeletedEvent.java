/*
 * */
package com.synectiks.process.server.dashboards.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class DashboardDeletedEvent {
    private static final String FIELD_DASHBOARD_ID = "dashboard_id";

    @JsonProperty(FIELD_DASHBOARD_ID)
    public abstract String dashboardId();

    @JsonCreator
    public static DashboardDeletedEvent create(@JsonProperty(FIELD_DASHBOARD_ID) String dashboardId) {
        return new AutoValue_DashboardDeletedEvent(dashboardId);
    }
}
