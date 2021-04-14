/*
 * */
package com.synectiks.process.server.shared.rest.resources.system;

import com.synectiks.process.server.rest.models.system.metrics.requests.MetricsReadRequest;
import com.synectiks.process.server.rest.models.system.metrics.responses.MetricNamesResponse;
import com.synectiks.process.server.rest.models.system.metrics.responses.MetricsSummaryResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface RemoteMetricsResource {
    @GET("system/metrics/names")
    Call<MetricNamesResponse> metricNames();

    @POST("system/metrics/multiple")
    Call<MetricsSummaryResponse> multipleMetrics(@Body @Valid @NotNull MetricsReadRequest request);

    @GET("system/metrics/namespace/{namespace}")
    Call<MetricsSummaryResponse> byNamespace(@Path("namespace") String namespace);
}
