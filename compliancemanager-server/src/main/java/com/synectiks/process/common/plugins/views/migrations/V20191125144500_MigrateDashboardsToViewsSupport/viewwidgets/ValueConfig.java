/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ValueConfig implements PivotConfig {
    public static final String NAME = "values";
    static final String FIELD_LIMIT = "limit";

    @JsonProperty(FIELD_LIMIT)
    public abstract int limit();

    static Builder builder() {
        return new AutoValue_ValueConfig.Builder().limit(15);
    }

    public static ValueConfig ofLimit(int limit) {
        return ValueConfig.builder().limit(limit).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty(FIELD_LIMIT)
        public abstract Builder limit(int limit);

        public abstract ValueConfig build();
    }
}
