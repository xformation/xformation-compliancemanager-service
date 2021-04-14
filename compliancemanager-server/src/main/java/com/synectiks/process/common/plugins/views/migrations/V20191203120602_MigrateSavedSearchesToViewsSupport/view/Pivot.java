/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.Time;

@AutoValue
public abstract class Pivot {
    private static final String TYPE_TIME = "time";

    static final String FIELD_FIELD_NAME = "field";
    static final String FIELD_TYPE = "type";
    static final String FIELD_CONFIG = "config";


    @JsonProperty(FIELD_FIELD_NAME)
    public abstract String field();

    @JsonProperty(FIELD_TYPE)
    public String type() {
        return TYPE_TIME;
    }

    @JsonProperty(FIELD_CONFIG)
    public abstract TimeHistogramConfig config();

    static Builder timeBuilder() {
        return new AutoValue_Pivot.Builder()
                .config(TimeHistogramConfig.create());
    }

    Time toBucketSpec() {
        return Time.create(field(), config().interval().toBucketInterval());
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder field(String field);

        public abstract Builder config(TimeHistogramConfig config);

        public abstract Pivot build();
    }
}
