package com.student.dzeus.derro.View.Model;

import com.google.gson.annotations.SerializedName;

public class LocationItem {

    @SerializedName("_longitude")
    private double longitude;
    @SerializedName("_name")
    private String name;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("create_at")
    private String createdAt;
    @SerializedName("_id")
    private int id;
    @SerializedName("_company_id")
    private int companyId;
    @SerializedName("_latitude")
    private double latitude;
    @SerializedName("_rate")
    private float rate;

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return
                "LocationItem{" +
                        "_longitude = '" + longitude + '\'' +
                        ",_name = '" + name + '\'' +
                        ",updated_at = '" + updatedAt + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",_id = '" + id + '\'' +
                        ",_company_id = '" + companyId + '\'' +
                        ",_latitude = '" + latitude + '\'' +
                        ",_rate = '" + rate + '\'' +
                        "}";
    }
}
