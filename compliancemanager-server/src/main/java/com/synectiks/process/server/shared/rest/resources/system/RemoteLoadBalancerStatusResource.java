/*
 * */
package com.synectiks.process.server.shared.rest.resources.system;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RemoteLoadBalancerStatusResource {
    @PUT("system/lbstatus/override/{status}")
    Call<Void> override(@Path("status") String status);
}
