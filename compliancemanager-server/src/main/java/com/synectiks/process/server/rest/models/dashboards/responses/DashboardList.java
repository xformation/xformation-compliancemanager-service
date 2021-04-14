/*
 * */
package com.synectiks.process.server.rest.models.dashboards.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;
import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class DashboardList {
    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract List<Map<String, Object>> dashboards();

    public static DashboardList create(int total, List<Map<String, Object>> dashboards) {
        return new AutoValue_DashboardList(total, dashboards);
    }
}
