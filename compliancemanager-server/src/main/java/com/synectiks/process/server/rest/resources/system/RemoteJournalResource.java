/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.synectiks.process.server.rest.resources.system.responses.JournalSummaryResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RemoteJournalResource {
    @GET("system/journal")
    Call<JournalSummaryResponse> get();
}
