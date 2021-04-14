/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.Optional;

@AutoValue
public abstract class SeriesSpec {
    @JsonProperty
    public abstract String type();

    @JsonProperty
    @Nullable
    public abstract String id();

    @JsonProperty
    public abstract Optional<String> field();

    public String literal() {
        return type() + "(" + field().orElse("") + ")";
    }

    public static SeriesSpec create(String type, String id, String field) {
        return new AutoValue_SeriesSpec(type, id, Optional.ofNullable(field));
    }
}
