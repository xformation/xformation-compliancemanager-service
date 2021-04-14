/*
 * */
package com.synectiks.process.server.shared.rest.resources.system;

import com.synectiks.process.server.rest.models.system.responses.SystemJVMResponse;
import com.synectiks.process.server.rest.models.system.responses.SystemOverviewResponse;
import com.synectiks.process.server.rest.models.system.responses.SystemProcessBufferDumpResponse;
import com.synectiks.process.server.rest.models.system.responses.SystemThreadDumpResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RemoteSystemResource {
    @GET("system")
    Call<SystemOverviewResponse> system();

    @GET("system/jvm")
    Call<SystemJVMResponse> jvm();

    @GET("system/threaddump")
    Call<SystemThreadDumpResponse> threadDump();

    @GET("system/processbufferdump")
    Call<SystemProcessBufferDumpResponse> processBufferDump();
}
