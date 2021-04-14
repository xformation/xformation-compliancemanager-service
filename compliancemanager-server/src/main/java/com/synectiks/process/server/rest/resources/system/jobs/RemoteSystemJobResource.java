/*
 * */
package com.synectiks.process.server.rest.resources.system.jobs;

import com.synectiks.process.server.rest.models.system.SystemJobSummary;
import com.synectiks.process.server.rest.models.system.jobs.requests.TriggerRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;
import java.util.Map;

public interface RemoteSystemJobResource {
    @GET("system/jobs")
    Call<Map<String, List<SystemJobSummary>>> list();

    @GET("system/jobs/{jobId}")
    Call<SystemJobSummary> get(@Path("jobId") String jobId);

    @DELETE("system/jobs/{jobId}")
    Call<SystemJobSummary> delete(@Path("jobId") String jobId);

    @POST("system/jobs")
    Call trigger(@Body TriggerRequest tr);
}
