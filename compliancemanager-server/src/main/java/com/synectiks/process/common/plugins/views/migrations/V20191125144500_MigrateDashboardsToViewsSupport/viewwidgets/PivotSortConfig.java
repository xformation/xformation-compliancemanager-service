/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.PivotSort;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.SortSpec;

@AutoValue
public abstract class PivotSortConfig implements SortConfig {
    public static final String Type = "pivot";

    @Override
    @JsonProperty(FIELD_TYPE)
    public abstract String type();

    @Override
    @JsonProperty(FIELD_FIELD)
    public abstract String field();

    @Override
    @JsonProperty(FIELD_DIRECTION)
    public abstract Direction direction();

    @Override
    public SortSpec toSortSpec() {
        return PivotSort.create(field(),toDirection());
    }

    @JsonCreator
    public static PivotSortConfig create(@JsonProperty(FIELD_FIELD) String field,
                                         @JsonProperty(FIELD_DIRECTION) Direction direction) {
        return new AutoValue_PivotSortConfig(PivotSortConfig.Type, field, direction);
    }
}
