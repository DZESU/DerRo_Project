package com.student.dzeus.derro.Base.Network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static final String WEB_BASE_URL = "http://localhost/DerRo/php/";
    private static final String Emulator_BASE_URL = "http://10.0.2.2:8000/api/";
    private static RetrofitClient mInstance;
    private static Retrofit retrofit;
    private static OkHttpClient.Builder mHttpClient =null;
    private static Retrofit.Builder mRetrofitBuilder = null;
    OkHttpClient okHttpClient;
    private static RetrofitClient instance = null;

    private RetrofitClient() {
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiInterface getApi() {
        return new Retrofit.Builder()
                .baseUrl(Emulator_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }

}
