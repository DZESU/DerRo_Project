package com.student.dzeus.derro.View.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseCompaniesList {
	@SerializedName("response")
	private List<Company> companyList;

	public void setResponse(List<Company> companyList){
		this.companyList = companyList;
	}

	public List<Company> getCompanyList(){
		return companyList;
	}

	@Override
 	public String toString(){
		return 
			"ResponseCompaniesList{" +
			"companyList = '" + companyList + '\'' +
			"}";
		}
}