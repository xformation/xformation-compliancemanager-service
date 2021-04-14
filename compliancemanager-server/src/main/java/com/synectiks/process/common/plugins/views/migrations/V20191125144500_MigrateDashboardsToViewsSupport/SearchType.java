/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty
    Optional<String> name();
}
