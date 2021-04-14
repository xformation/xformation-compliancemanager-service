/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.BucketSpec;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.Time;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.Values;

@AutoValue
public abstract class Pivot {
    private static final String TYPE_TIME = "time";
    private static final String TYPE_VALUES = "values";

    static final String FIELD_FIELD_NAME = "field";
    static final String FIELD_TYPE = "type";
    static final String FIELD_CONFIG = "config";


    @JsonProperty(FIELD_FIELD_NAME)
    public abstract String field();

    @JsonProperty(FIELD_TYPE)
    public abstract String type();

    @JsonProperty(FIELD_CONFIG)
    public abstract PivotConfig config();

    public static Builder timeBuilder() {
        return new AutoValue_Pivot.Builder()
                .type(TYPE_TIME);
    }

    public static Builder valuesBuilder() {
        return new AutoValue_Pivot.Builder()
                .type(TYPE_VALUES);
    }

    public BucketSpec toBucketSpec() {
        switch (type()) {
            case TYPE_TIME:
                final TimeHistogramConfig timeConfig = (TimeHistogramConfig)config();
                return Time.create(field(), timeConfig.interval().toBucketInterval());
            case TYPE_VALUES:
                final ValueConfig valueConfig = (ValueConfig)config();
                return Values.create(field(), valueConfig.limit());
        }

        throw new RuntimeException("Invalid pivot type when creating bucket spec: " + type());
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder field(String field);

        public abstract Builder config(PivotConfig config);

        public abstract Builder type(String type);

        public abstract Pivot build();
    }
}
