/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@JsonDeserialize(builder = ViewWidgetPosition.Builder.class)
@WithBeanGetter
public abstract class ViewWidgetPosition {
    @JsonProperty("col")
    abstract int col();

    @JsonProperty("row")
    abstract int row();

    @JsonProperty("height")
    abstract int height();

    @JsonProperty("width")
    abstract int width();

    public static Builder builder() { return new AutoValue_ViewWidgetPosition.Builder(); }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder col(int col);

        public abstract Builder row(int row);

        public abstract Builder height(int height);

        public abstract Builder width(int width);

        public abstract ViewWidgetPosition build();
    }
}
