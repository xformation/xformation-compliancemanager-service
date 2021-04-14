/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.sidecar.rest.models.CollectorUpload;
import com.synectiks.process.server.database.PaginatedList;

import java.util.Collection;

@AutoValue
public abstract class CollectorUploadListResponse {
    @JsonProperty("pagination")
    public abstract PaginatedList.PaginationInfo paginationInfo();

    @JsonProperty
    public abstract long total();

    @JsonProperty
    public abstract Collection<CollectorUpload> uploads();

    @JsonCreator
    public static CollectorUploadListResponse create(@JsonProperty("pagination") PaginatedList.PaginationInfo paginationInfo,
                                                     @JsonProperty("total") long total,
                                                     @JsonProperty("uploads") Collection<CollectorUpload> uploads) {
        return new AutoValue_CollectorUploadListResponse(paginationInfo, total, uploads);
    }

}
