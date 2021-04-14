/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AreaVisualizationConfig implements VisualizationConfig {
    public static final String NAME = "area";
    private static final String FIELD_INTERPOLATION = "interpolation";

    @JsonProperty
    public abstract Interpolation interpolation();

    public static Builder builder() {
        return new AutoValue_AreaVisualizationConfig.Builder()
                .interpolation(Interpolation.linear);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        @JsonProperty(FIELD_INTERPOLATION)
        public abstract Builder interpolation(Interpolation interpolation);

        public abstract AreaVisualizationConfig build();
    }
}
