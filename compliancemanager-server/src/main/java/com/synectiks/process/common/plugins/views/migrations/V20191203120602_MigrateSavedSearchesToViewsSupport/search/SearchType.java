/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.TimeRange;

import java.util.Optional;
import java.util.Set;

public interface SearchType {
    @JsonProperty
    String id();

    @JsonProperty
    Optional<TimeRange> timerange();

    @JsonProperty
    Optional<ElasticsearchQueryString> query();

    @JsonProperty
    Set<String> streams();
}
