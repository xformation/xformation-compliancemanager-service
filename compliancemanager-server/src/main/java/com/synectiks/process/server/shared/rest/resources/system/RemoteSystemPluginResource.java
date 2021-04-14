/*
 * */
package com.synectiks.process.server.shared.rest.resources.system;

import com.synectiks.process.server.rest.models.system.plugins.responses.PluginList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RemoteSystemPluginResource {
    @GET("system/plugins")
    Call<PluginList> list();
}
