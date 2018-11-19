package com.student.dzeus.derro.View.Repositry;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.gson.Gson;
import com.student.dzeus.derro.Base.Network.RetrofitClient;
import com.student.dzeus.derro.View.Model.Company;
import com.student.dzeus.derro.View.Model.ResponseCompaniesList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepositry {

    private static final String TAG="pory";
    RetrofitClient retrofitClient = RetrofitClient.getInstance();

    public LocationRepositry(){

    }

    public LiveData<List<Company>> getAllLocation(){
        Call<String> result = retrofitClient.getApi().getAPINoParam();
        final MutableLiveData<List<Company>> companyLocationList = new MutableLiveData<>();
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: "+response.body());
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    ResponseCompaniesList responseCompaniesList =
                            gson.fromJson(response.body()
                                    ,ResponseCompaniesList.class);
                    companyLocationList.setValue(responseCompaniesList.getCompanyList());
                Log.d(TAG, "onResponse: "+companyLocationList.getValue().toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.toString());
            }
        });


        return companyLocationList;
    }

}
