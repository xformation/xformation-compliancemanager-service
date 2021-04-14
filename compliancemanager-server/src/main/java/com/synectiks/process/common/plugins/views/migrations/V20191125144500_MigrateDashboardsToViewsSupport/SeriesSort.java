/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
public abstract class SeriesSort implements SortSpec {
    public static final String Type = "series";

    @Override
    @JsonProperty(TYPE_FIELD)
    public abstract String type();

    @Override
    @JsonProperty(FIELD_FIELD)
    public abstract String field();

    @Override
    @JsonProperty(FIELD_DIRECTION)
    public abstract Direction direction();

    public static SeriesSort create(@JsonProperty(FIELD_FIELD) String field,
                                    @JsonProperty(FIELD_DIRECTION) Direction direction) {
        return new AutoValue_SeriesSort(Type, field, direction);
    }
}
