/*
 * */
package com.synectiks.process.common.plugins.views.search.views.widgets.aggregation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.mongodb.lang.Nullable;

import java.util.Optional;

@AutoValue
@JsonTypeName(HeatmapVisualizationConfigDTO.NAME)
@JsonDeserialize(builder = HeatmapVisualizationConfigDTO.Builder.class)
public abstract class HeatmapVisualizationConfigDTO implements VisualizationConfigDTO {
    public static final String NAME = "heatmap";
    private static final String FIELD_COLOR_SCALE = "color_scale";
    private static final String FIELD_REVERSE_SCALE = "reverse_scale";
    private static final String FIELD_AUTO_SCALE = "auto_scale";
    private static final String FIELD_Z_MIN = "z_min";
    private static final String FIELD_Z_MAX = "z_max";
    private static final String FIELD_DEFAULT_VALUE = "default_value";
    private static final String FIELD_USE_SMALLEST_AS_DEFAULT = "use_smallest_as_default";

    @JsonProperty(FIELD_COLOR_SCALE)
    public abstract String colorScale();

    @JsonProperty(FIELD_REVERSE_SCALE)
    public abstract boolean reverseScale();

    @JsonProperty(FIELD_AUTO_SCALE)
    public abstract boolean autoScale();

    @JsonProperty(FIELD_Z_MIN)
    public abstract Optional<String> zMin();

    @JsonProperty(FIELD_Z_MAX)
    public abstract Optional<String> zMax();

    @JsonProperty(FIELD_DEFAULT_VALUE)
    public abstract Optional<Long> defaultValue();

    @JsonProperty(FIELD_USE_SMALLEST_AS_DEFAULT)
    public abstract boolean useSmallestAsDefault();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonCreator
        public static Builder create() {
            return new AutoValue_HeatmapVisualizationConfigDTO.Builder()
                    .colorScale("Viridis")
                    .reverseScale(false)
                    .useSmallestAsDefault(false)
                    .autoScale(true);
        }

        @JsonProperty(FIELD_COLOR_SCALE)
        public abstract Builder colorScale(String colorScale);

        @JsonProperty(FIELD_REVERSE_SCALE)
        public abstract Builder reverseScale(boolean reverseScale);

        @JsonProperty(FIELD_AUTO_SCALE)
        public abstract Builder autoScale(boolean autoScale);

        @JsonProperty(FIELD_Z_MIN)
        public abstract Builder zMin(@Nullable String zMin);

        @JsonProperty(FIELD_Z_MAX)
        public abstract Builder zMax(@Nullable String zMax);

        @JsonProperty(FIELD_DEFAULT_VALUE)
        public abstract Builder defaultValue(@Nullable Long defaultValue);

        @JsonProperty(FIELD_USE_SMALLEST_AS_DEFAULT)
        public abstract Builder useSmallestAsDefault(boolean useSmallestAsDefault);

        public abstract HeatmapVisualizationConfigDTO build();
    }
}
