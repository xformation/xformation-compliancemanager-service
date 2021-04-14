/*
 * */
package com.synectiks.process.server.rest.resources.system.processing;

import com.synectiks.process.server.rest.models.system.processing.ProcessingStatusSummary;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RemoteSystemProcessingStatusResource {
    @GET("system/processing/status")
    Call<ProcessingStatusSummary> getStatus();

    @GET("system/processing/status/persisted")
    Call<ProcessingStatusSummary> getPersistedStatus();
}
