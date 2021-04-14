/*
 * */
package com.synectiks.process.server.storage.versionprobe;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RootRoute {
    @GET("/")
    Call<RootResponse> root();
}
