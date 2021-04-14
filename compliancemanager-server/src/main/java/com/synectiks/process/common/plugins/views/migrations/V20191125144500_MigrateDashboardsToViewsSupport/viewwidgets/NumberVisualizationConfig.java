/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NumberVisualizationConfig implements VisualizationConfig {
    public static final String NAME = "numeric";
    private static final String FIELD_TREND = "trend";
    private static final String FIELD_TREND_PREFERENCE = "trend_preference";

    public enum TrendPreference {
        LOWER,
        NEUTRAL,
        HIGHER;
    }

    @JsonProperty
    public abstract boolean trend();

    @JsonProperty
    public abstract TrendPreference trendPreference();

    public static Builder builder() {
        return new AutoValue_NumberVisualizationConfig.Builder()
                .trend(false)
                .trendPreference(TrendPreference.NEUTRAL);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder trend(boolean trend);

        public abstract Builder trendPreference(TrendPreference trendPreference);

        public abstract NumberVisualizationConfig build();

    }
}
