/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.BucketInterval;

public interface Interval {
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();

    BucketInterval toBucketInterval();
}
