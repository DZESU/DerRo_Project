package com.student.dzeus.derro.View.Model;

import java.util.List;

public class ResponseLocation{
	private ResponseLocation responseLocation;
	private List<CompaniesItem> companies;

	public void setResponseLocation(ResponseLocation responseLocation){
		this.responseLocation = responseLocation;
	}

	public ResponseLocation getResponseLocation(){
		return responseLocation;
	}

	public void setCompanies(List<CompaniesItem> companies){
		this.companies = companies;
	}

	public List<CompaniesItem> getCompanies(){
		return companies;
	}

	@Override
 	public String toString(){
		return 
			"ResponseLocation{" + 
			"responseLocation = '" + responseLocation + '\'' + 
			",companies = '" + companies + '\'' + 
			"}";
		}
}
