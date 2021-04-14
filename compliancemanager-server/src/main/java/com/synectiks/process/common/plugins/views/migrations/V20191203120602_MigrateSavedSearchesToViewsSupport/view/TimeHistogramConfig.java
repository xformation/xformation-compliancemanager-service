/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TimeHistogramConfig {
    public static final String NAME = "time";
    static final String FIELD_INTERVAL = "interval";

    @JsonProperty(FIELD_INTERVAL)
    public abstract AutoInterval interval();

    public static TimeHistogramConfig create() {
        return new AutoValue_TimeHistogramConfig(AutoInterval.create());
    }
}
