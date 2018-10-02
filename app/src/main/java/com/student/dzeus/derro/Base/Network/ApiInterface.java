package com.student.dzeus.derro.Base.Network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("api")
    Call<String> getAPI(@Body String param);

    @POST("der_ro_api")
    Call<String> getAPINoParam();

}
