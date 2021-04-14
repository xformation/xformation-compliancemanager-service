/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;


import com.fasterxml.jackson.annotation.JsonProperty;

public interface BucketSpec {
    String TYPE_FIELD = "type";

    @JsonProperty
    String type();
}
