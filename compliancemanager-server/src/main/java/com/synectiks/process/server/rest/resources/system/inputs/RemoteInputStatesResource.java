/*
 * */
package com.synectiks.process.server.rest.resources.system.inputs;

import com.synectiks.process.server.rest.models.system.inputs.responses.InputCreated;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputDeleted;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputStatesList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RemoteInputStatesResource {
    @GET("system/inputstates")
    Call<InputStatesList> list();

    @PUT("system/inputstates/{inputId}")
    Call<InputCreated> start(@Path("inputId") String inputId);

    @DELETE("system/inputstates/{inputId}")
    Call<InputDeleted> stop(@Path("inputId") String inputId);
}
