/*
 * */
package com.synectiks.process.common.freeenterprise;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FreeLicenseAPIClient {
    @POST("enterprise/license/v2/freetrial")
    Call<FreeLicenseAPIResponse> requestFreeLicense(@Body FreeLicenseAPIRequest request);
}
