/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@JsonDeserialize(builder = ViewWidgetPosition.Builder.class)
@WithBeanGetter
public abstract class ViewWidgetPosition {
    @JsonProperty("col")
    abstract Position col();

    @JsonProperty("row")
    abstract Position row();

    @JsonProperty("height")
    abstract Position height();

    @JsonProperty("width")
    abstract Position width();

    public static Builder builder() { return new AutoValue_ViewWidgetPosition.Builder(); }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder col(Position col);

        public abstract Builder row(Position row);

        public abstract Builder height(Position height);

        public abstract Builder width(Position width);

        public abstract ViewWidgetPosition build();
    }
}
