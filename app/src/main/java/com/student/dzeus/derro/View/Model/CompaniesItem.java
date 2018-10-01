package com.student.dzeus.derro.View.Model;

import java.util.List;

public class CompaniesItem{
	private List<LocationsItem> locations;
	private String companyName;

	public void setLocations(List<LocationsItem> locations){
		this.locations = locations;
	}

	public List<LocationsItem> getLocations(){
		return locations;
	}

	public void setCompanyName(String companyName){
		this.companyName = companyName;
	}

	public String getCompanyName(){
		return companyName;
	}

	@Override
 	public String toString(){
		return 
			"CompaniesItem{" + 
			"locations = '" + locations + '\'' + 
			",company_name = '" + companyName + '\'' + 
			"}";
		}
}