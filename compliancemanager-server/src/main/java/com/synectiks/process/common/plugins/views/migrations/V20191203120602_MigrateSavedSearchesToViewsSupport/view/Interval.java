/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.BucketInterval;

public interface Interval {
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();

    BucketInterval toBucketInterval();
}
