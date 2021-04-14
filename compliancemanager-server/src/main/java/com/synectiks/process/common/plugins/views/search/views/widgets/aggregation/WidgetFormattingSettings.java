/*
 * */
package com.synectiks.process.common.plugins.views.search.views.widgets.aggregation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = WidgetFormattingSettings.Builder.class)
public abstract class WidgetFormattingSettings {
    private static final String FIELD_CHART_COLORS = "chart_colors";

    @JsonProperty(FIELD_CHART_COLORS)
    public abstract List<ChartColorMapping> chartColors();

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonProperty(FIELD_CHART_COLORS)
        public abstract Builder chartColors(List<ChartColorMapping> chartColors);

        public abstract WidgetFormattingSettings build();

        @JsonCreator
        static Builder builder() {
            return new AutoValue_WidgetFormattingSettings.Builder()
                    .chartColors(Collections.emptyList());
        }
    }
}
