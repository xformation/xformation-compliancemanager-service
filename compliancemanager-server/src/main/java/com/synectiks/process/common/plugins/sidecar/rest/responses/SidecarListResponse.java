/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.sidecar.rest.models.SidecarSummary;
import com.synectiks.process.server.database.PaginatedList;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

@AutoValue
public abstract class SidecarListResponse {
    @Nullable
    @JsonProperty
    public abstract String query();

    @JsonProperty("pagination")
    public abstract PaginatedList.PaginationInfo paginationInfo();

    @JsonProperty
    public abstract long total();

    @JsonProperty
    public abstract Boolean onlyActive();

    @Nullable
    @JsonProperty
    public abstract String sort();

    @Nullable
    @JsonProperty
    public abstract String order();

    @JsonProperty
    public abstract Collection<SidecarSummary> sidecars();

    @Nullable
    @JsonProperty
    public abstract Map<String, String> filters();

    @JsonCreator
    public static SidecarListResponse create(@JsonProperty("query") @Nullable String query,
                                             @JsonProperty("pagination") PaginatedList.PaginationInfo paginationInfo,
                                             @JsonProperty("total") long total,
                                             @JsonProperty("only_active") Boolean onlyActive,
                                             @JsonProperty("sort") @Nullable String sort,
                                             @JsonProperty("order") @Nullable String order,
                                             @JsonProperty("sidecars") Collection<SidecarSummary> sidecars,
                                             @JsonProperty("filters") @Nullable Map<String, String> filters) {
        return new AutoValue_SidecarListResponse(query, paginationInfo, total, onlyActive, sort, order, sidecars, filters);
    }

    public static SidecarListResponse create(@JsonProperty("query") @Nullable String query,
                                             @JsonProperty("pagination") PaginatedList.PaginationInfo paginationInfo,
                                             @JsonProperty("total") long total,
                                             @JsonProperty("only_active") Boolean onlyActive,
                                             @JsonProperty("sort") @Nullable String sort,
                                             @JsonProperty("order") @Nullable String order,
                                             @JsonProperty("sidecars") Collection<SidecarSummary> sidecars) {
        return create(query, paginationInfo, total, onlyActive, sort, order, sidecars, null);
    }
}
