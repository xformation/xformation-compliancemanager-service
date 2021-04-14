/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.BucketInterval;

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
        return com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.AutoInterval.create();
    }

    private static Builder builder() { return new AutoValue_AutoInterval.Builder().type(type); };

    public static AutoInterval create() {
        return AutoInterval.builder().build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(String type);
        public abstract Builder scaling(@Nullable Double scaling);

        public abstract AutoInterval build();
    }
}

