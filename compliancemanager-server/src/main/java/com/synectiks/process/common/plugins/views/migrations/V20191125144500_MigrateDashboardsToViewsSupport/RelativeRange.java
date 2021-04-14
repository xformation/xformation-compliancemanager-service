/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonTypeName(RelativeRange.RELATIVE)
public abstract class RelativeRange extends TimeRange {

    static final String RELATIVE = "relative";

    @JsonProperty
    @Override
    public abstract String type();

    @JsonProperty
    public abstract int range();

    @JsonCreator
    static RelativeRange create(@JsonProperty("type") String type, @JsonProperty("range") int range) {
        return builder().type(type).range(range).build();
    }

    public static RelativeRange create(int range) {
        return create(RELATIVE, range);
    }

    static Builder builder() {
        return new AutoValue_RelativeRange.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        abstract RelativeRange build();

        abstract Builder type(String type);

        abstract Builder range(int range);
    }

}
