/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.sidecar.rest.models.ConfigurationSummary;
import com.synectiks.process.server.database.PaginatedList;

import javax.annotation.Nullable;
import java.util.Collection;

@AutoValue
public abstract class ConfigurationListResponse {
    @Nullable
    @JsonProperty
    public abstract String query();

    @JsonProperty("pagination")
    public abstract PaginatedList.PaginationInfo paginationInfo();

    @JsonProperty
    public abstract long total();

    @Nullable
    @JsonProperty
    public abstract String sort();

    @Nullable
    @JsonProperty
    public abstract String order();

    @JsonProperty
    public abstract Collection<ConfigurationSummary> configurations();

    @JsonCreator
    public static ConfigurationListResponse create(@JsonProperty("query") String query,
                                                   @JsonProperty("pagination") PaginatedList.PaginationInfo paginationInfo,
                                                   @JsonProperty("total") long total,
                                                   @JsonProperty("sort") String sort,
                                                   @JsonProperty("order") String order,
                                                   @JsonProperty("configurations") Collection<ConfigurationSummary> configurations) {
        return new AutoValue_ConfigurationListResponse(query, paginationInfo, total, sort, order, configurations);
    }
}
