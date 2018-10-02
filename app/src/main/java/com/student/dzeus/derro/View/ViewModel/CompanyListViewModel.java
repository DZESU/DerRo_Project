package com.student.dzeus.derro.View.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.student.dzeus.derro.View.Model.Company;
import com.student.dzeus.derro.View.Repositry.LocationRepositry;

import java.util.List;

public class CompanyListViewModel extends ViewModel {

    private static final String TAG = "pory";
    LocationRepositry repositry;
    LiveData<List<Company>> listCompany;

    public CompanyListViewModel() {

        repositry = new LocationRepositry();

    }

    public LiveData<List<Company>> getListCompany() {
        if (listCompany== null)
            LoadListCompany();
        return listCompany;
    }

    public void LoadListCompany() {
        this.listCompany = repositry.getAllLocation();
    }
}
