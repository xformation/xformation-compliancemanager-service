/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
@JsonDeserialize(builder = WidgetPosition.Builder.class)
public abstract class WidgetPosition {

    @JsonProperty("width")
    public abstract int width();

    @JsonProperty("height")
    public abstract int height();

    @JsonProperty("col")
    public abstract int col();

    @JsonProperty("row")
    public abstract int row();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract WidgetPosition build();

        @JsonProperty("width")
        public abstract Builder width(int width);

        @JsonProperty("height")
        public abstract Builder height(int height);

        @JsonProperty("col")
        public abstract Builder col(int col);

        @JsonProperty("row")
        public abstract Builder row(int row);

        @JsonCreator
        public static Builder create() {
            return new AutoValue_WidgetPosition.Builder();
        }
    }
}
