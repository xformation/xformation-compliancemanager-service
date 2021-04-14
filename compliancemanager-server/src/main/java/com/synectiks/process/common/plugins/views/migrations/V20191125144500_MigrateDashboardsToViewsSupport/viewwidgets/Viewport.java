/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Viewport {
    @JsonProperty("center_x")
    public abstract double centerX();

    @JsonProperty("center_y")
    public abstract double centerY();

    @JsonProperty
    public abstract int zoom();

    private static Builder builder() {
        return new AutoValue_Viewport.Builder();
    }

    static Viewport empty() {
        return Viewport.builder()
                .centerX(0)
                .centerY(0)
                .zoom(32)
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("center_x")
        public abstract Builder centerX(double centerX);

        @JsonProperty("center_y")
        public abstract Builder centerY(double centerY);

        @JsonProperty
        public abstract Builder zoom(int zoom);

        public abstract Viewport build();
    }
}
