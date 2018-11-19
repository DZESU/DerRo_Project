package com.student.dzeus.derro.View.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Company {

	@SerializedName("company_id")
	private int companyId;
	@SerializedName("company_name")
	private String companyName;
	@SerializedName("location")
	private List<LocationItem> locationList;

	public void setCompanyName(String companyName){
		this.companyName = companyName;
	}

	public String getCompanyName(){
		return companyName;
	}

	public void setLocation(List<LocationItem> locationList){
		this.locationList = locationList;
	}

	public List<LocationItem> getLocation(){
		return locationList;
	}

	public int getCompany_id() {
		return companyId;
	}

	public void setCompany_id(int company_id) {
		this.companyId = company_id;
	}

	@Override
	public String toString() {
		return "Company{" +
				"companyId=" + companyId +
				", companyName='" + companyName + '\'' +
				", locationList=" + locationList +
				'}';
	}
}