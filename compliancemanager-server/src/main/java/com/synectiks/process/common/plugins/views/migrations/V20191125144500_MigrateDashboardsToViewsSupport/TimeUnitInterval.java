/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TimeUnitInterval implements BucketInterval {
    public static final String type = "timeunit";

    @JsonProperty
    public String type() { return type; };

    @JsonProperty
    public abstract String timeunit();

    public static TimeUnitInterval create(String timeunit) {
        return new AutoValue_TimeUnitInterval(timeunit);
    }
}
