/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.BucketInterval;

import javax.annotation.Nullable;
import java.util.Optional;

@AutoValue
public abstract class AutoInterval implements Interval {
    public static final String type = "auto";
    private static final String FIELD_SCALING = "scaling";

    @JsonProperty
    public abstract String type();

    @JsonProperty(FIELD_SCALING)
    public abstract Optional<Double> scaling();

    @Override
    public BucketInterval toBucketInterval() {
        return com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.AutoInterval.create();
    }

    private static Builder builder() { return new AutoValue_AutoInterval.Builder().type(type); };

    public static AutoInterval create() {
        return AutoInterval.builder().build();
    }

    public static AutoInterval create(Double scaling) {
        return AutoInterval.builder().scaling(scaling).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(String type);
        public abstract Builder scaling(@Nullable Double scaling);

        public abstract AutoInterval build();
    }
}

