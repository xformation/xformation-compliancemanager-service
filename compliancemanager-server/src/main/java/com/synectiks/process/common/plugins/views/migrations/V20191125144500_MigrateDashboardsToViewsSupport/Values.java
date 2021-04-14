/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Values implements BucketSpec {
    public static final String NAME = "values";

    @JsonProperty
    public String type() { return NAME; }

    @JsonProperty
    public abstract String field();

    @JsonProperty
    public abstract int limit();

    public static Values create(String field, int limit) {
        return new AutoValue_Values(field, limit);
    }
}

