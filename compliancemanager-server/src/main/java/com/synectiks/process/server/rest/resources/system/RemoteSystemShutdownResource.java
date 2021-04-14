/*
 * */
package com.synectiks.process.server.rest.resources.system;

import retrofit2.Call;
import retrofit2.http.POST;

public interface RemoteSystemShutdownResource {
    @POST("system/shutdown/shutdown")
    Call<Void> shutdown();
}
