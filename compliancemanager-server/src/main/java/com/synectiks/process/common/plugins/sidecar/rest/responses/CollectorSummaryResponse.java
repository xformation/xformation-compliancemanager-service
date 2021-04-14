/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.sidecar.rest.models.CollectorSummary;
import com.synectiks.process.server.database.PaginatedList;

import javax.annotation.Nullable;
import java.util.Collection;

@AutoValue
public abstract class CollectorSummaryResponse {
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
    public abstract Collection<CollectorSummary> collectors();

    @JsonCreator
    public static CollectorSummaryResponse create(@JsonProperty("query") @Nullable String query,
                                                  @JsonProperty("pagination") PaginatedList.PaginationInfo paginationInfo,
                                                  @JsonProperty("total") long total,
                                                  @JsonProperty("sort") String sort,
                                                  @JsonProperty("order") String order,
                                                  @JsonProperty("collectors") Collection<CollectorSummary> collectors) {
        return new AutoValue_CollectorSummaryResponse(query, paginationInfo, total, sort, order, collectors);
    }
}
