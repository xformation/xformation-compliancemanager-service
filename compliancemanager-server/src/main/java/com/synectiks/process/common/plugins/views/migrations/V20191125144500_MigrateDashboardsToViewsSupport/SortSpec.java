/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface SortSpec {
    enum Direction {
        Ascending,
        Descending
    }

    String TYPE_FIELD = "type";
    String FIELD_FIELD = "field";
    String FIELD_DIRECTION = "direction";

    @JsonProperty(TYPE_FIELD)
    String type();
    @JsonProperty(FIELD_FIELD)
    String field();
    @JsonProperty(FIELD_DIRECTION)
    Direction direction();
}
