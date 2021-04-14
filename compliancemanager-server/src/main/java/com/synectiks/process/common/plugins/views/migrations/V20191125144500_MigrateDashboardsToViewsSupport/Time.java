/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Time implements BucketSpec {
    public static final String NAME = "time";

    @Override
    public String type() { return NAME; }

    @JsonProperty
    public abstract String field();

    @JsonProperty
    public abstract BucketInterval interval();

    public static Time create(String field, BucketInterval interval) {
        return new AutoValue_Time(field, interval);
    }
}

