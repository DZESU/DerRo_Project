package com.student.dzeus.derro.View.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.student.dzeus.derro.View.Model.Company;
import com.student.dzeus.derro.View.Repositry.LocationRepositry;

import java.util.ArrayList;
import java.util.List;

public class CompanyListViewModel extends ViewModel {

    private static final String TAG = "pory";
    LocationRepositry repositry;
    LiveData<List<Company>> listCompany;

    List<LatLng> latLngList;

    public CompanyListViewModel() {

        repositry = new LocationRepositry();
        latLngList = new ArrayList<>();

    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    public void setLatLngList(List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }

    public LiveData<List<Company>> getListCompany() {
        if (listCompany == null || listCompany.getValue() == null)
            LoadListCompany();
        return listCompany;
    }

    public void LoadListCompany() {
        this.listCompany = repositry.getAllLocation();
    }

    public Company fillterCompany(int companyId) {
        if (hasCompany()) {
            Company fillteredCompany;
            for (int i = 0; i < listCompany.getValue().size(); i++) {
                if (listCompany.getValue().get(i).getCompany_id() == companyId) {
                    fillteredCompany = listCompany.getValue().get(i);
                    return fillteredCompany;
                }
            }
        }
        return null;
    }

    public Company fillterCompany(String companyName) {
        if (hasCompany()) {
            Company fillteredCompany;
            for (int i = 0; i < listCompany.getValue().size(); i++) {
                if (listCompany.getValue().get(i).getCompanyName() == companyName) {
                    fillteredCompany = listCompany.getValue().get(i);
                    return fillteredCompany;
                }
            }
        }
        return null;
    }

    public boolean hasCompany() {
        return listCompany.getValue() != null;
    }
}
