package com.student.dzeus.derro.View.Model;

public class LocationsItem{
	private String longitude;
	private String name;
	private String id;
	private String companyId;
	private String latitude;
	private String rate;

	public void setLongitude(String longitude){
		this.longitude = longitude;
	}

	public String getLongitude(){
		return longitude;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setCompanyId(String companyId){
		this.companyId = companyId;
	}

	public String getCompanyId(){
		return companyId;
	}

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}

	public String getLatitude(){
		return latitude;
	}

	public void setRate(String rate){
		this.rate = rate;
	}

	public String getRate(){
		return rate;
	}

	@Override
 	public String toString(){
		return 
			"LocationsItem{" + 
			"_longitude = '" + longitude + '\'' + 
			",_name = '" + name + '\'' + 
			",_id = '" + id + '\'' + 
			",_company_id = '" + companyId + '\'' + 
			",_latitude = '" + latitude + '\'' + 
			",_rate = '" + rate + '\'' + 
			"}";
		}
}
