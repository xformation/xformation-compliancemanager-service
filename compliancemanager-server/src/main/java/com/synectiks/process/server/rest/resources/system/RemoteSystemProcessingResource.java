/*
 * */
package com.synectiks.process.server.rest.resources.system;

import retrofit2.Call;
import retrofit2.http.PUT;

public interface RemoteSystemProcessingResource {
    @PUT("system/processing/pause")
    Call<Void> pause();

    @PUT("system/processing/resume")
    Call<Void> resume();
}
