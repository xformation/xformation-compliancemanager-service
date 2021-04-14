/*
 * */
package com.synectiks.process.server.dashboards.widgets;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
@JsonDeserialize(builder = WidgetPosition.Builder.class)
public abstract class WidgetPosition {

    @JsonProperty("id")
    public abstract String id();

    @JsonProperty("width")
    public abstract int width();

    @JsonProperty("height")
    public abstract int height();

    @JsonProperty("col")
    public abstract int col();

    @JsonProperty("row")
    public abstract int row();

    public static Builder builder() {
        return new AutoValue_WidgetPosition.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract WidgetPosition build();

        public abstract Builder id(String id);

        public abstract Builder width(int width);

        public abstract Builder height(int height);

        public abstract Builder col(int col);

        public abstract Builder row(int row);
    }
}
