/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LineVisualizationConfig implements VisualizationConfig {
    public static final String NAME = "line";
    private static final String FIELD_INTERPOLATION = "interpolation";

    @JsonProperty
    public abstract Interpolation interpolation();

    public static Builder builder() {
        return new AutoValue_LineVisualizationConfig.Builder()
                .interpolation(Interpolation.linear);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        @JsonProperty(FIELD_INTERPOLATION)
        public abstract Builder interpolation(Interpolation interpolation);

        public abstract LineVisualizationConfig build();
    }
}
