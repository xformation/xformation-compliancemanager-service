/*
 * */
package com.synectiks.process.server.shared.rest.resources.system;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RemoteDeflectorResource {
    @POST("system/deflector/cycle")
    Call<Void> cycle();

    @POST("system/deflector/{indexSetId}/cycle")
    Call<Void> cycleIndexSet(@Path("indexSetId") String indexSetId);
}
