/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TimeHistogramConfig implements PivotConfig {
    public static final String NAME = "time";
    static final String FIELD_INTERVAL = "interval";

    @JsonProperty(FIELD_INTERVAL)
    public abstract Interval interval();

    public static Builder builder() {
        return new AutoValue_TimeHistogramConfig.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty(FIELD_INTERVAL)
        public abstract Builder interval(Interval interval);

        public abstract TimeHistogramConfig build();
    }
}
