/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.SortSpec;

public interface SortConfig {
    String FIELD_TYPE = "type";
    String FIELD_FIELD = "field";
    String FIELD_DIRECTION = "direction";

    SortSpec toSortSpec();

    enum Direction {
        Ascending,
        Descending
    }

    @JsonProperty(FIELD_TYPE)
    String type();

    @JsonProperty(FIELD_FIELD)
    String field();

    @JsonProperty(FIELD_DIRECTION)
    Direction direction();

    default SortSpec.Direction toDirection() {
        switch (direction()) {
            case Ascending:
                return SortSpec.Direction.Ascending;
            case Descending:
                return SortSpec.Direction.Descending;
        }

        throw new RuntimeException("Unable to transform unknown direction: " + direction());
    }
}
