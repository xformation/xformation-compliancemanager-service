/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BarVisualizationConfig implements VisualizationConfig {
    public static final String NAME = "bar";
    private static final String FIELD_BAR_MODE = "barmode";

    public enum BarMode {
        stack,
        overlay,
        group,
        relative
    };

    @JsonProperty
    public abstract BarMode barmode();

    public static Builder builder() {
        return new AutoValue_BarVisualizationConfig.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        @JsonProperty(FIELD_BAR_MODE)
        public abstract Builder barmode(BarMode barMode);

        public abstract BarVisualizationConfig build();
    }
}
