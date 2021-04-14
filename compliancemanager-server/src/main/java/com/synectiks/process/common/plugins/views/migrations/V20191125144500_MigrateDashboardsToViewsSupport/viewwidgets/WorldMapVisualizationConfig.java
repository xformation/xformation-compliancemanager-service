/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.validation.Valid;

@AutoValue
public abstract class WorldMapVisualizationConfig implements VisualizationConfig {
    public static final String NAME = "map";

    @JsonProperty
    public abstract Viewport viewport();

    private static Builder builder() {
        return new AutoValue_WorldMapVisualizationConfig.Builder();
    }

    public static WorldMapVisualizationConfig create() {
        return WorldMapVisualizationConfig.builder()
                .viewport(Viewport.empty())
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        @JsonProperty("viewport")
        public abstract Builder viewport(@Valid Viewport viewport);

        public abstract WorldMapVisualizationConfig build();
    }
}
