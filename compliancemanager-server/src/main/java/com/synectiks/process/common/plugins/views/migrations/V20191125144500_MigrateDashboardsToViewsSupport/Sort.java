/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
abstract class Sort {
    public enum SortOrder {
        ASC,
        DESC
    }

    @JsonProperty
    abstract String field();

    @JsonProperty
    abstract SortOrder order();

    @JsonCreator
    static Sort create(String field, SortOrder order) {
        return new AutoValue_Sort(field, order);
    }

}
