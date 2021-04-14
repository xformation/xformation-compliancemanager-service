/*
 * */
package com.synectiks.process.server.rest.resources.streams.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.streams.StreamDTO;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Collection;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class StreamPageListResponse {
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
    public abstract Collection<StreamDTO> streams();

    @JsonCreator
    public static StreamPageListResponse create(
            @JsonProperty("query") @Nullable String query,
            @JsonProperty("pagination") PaginatedList.PaginationInfo paginationInfo,
            @JsonProperty("total") long total,
            @JsonProperty("sort") @Nullable String sort,
            @JsonProperty("order") @Nullable String order,
            @JsonProperty("streams") Collection<StreamDTO> streams) {
        return new AutoValue_StreamPageListResponse(query, paginationInfo, total, sort, order, streams);
    }
}
