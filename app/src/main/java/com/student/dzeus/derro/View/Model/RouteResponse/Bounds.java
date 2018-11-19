package com.student.dzeus.derro.View.Model.RouteResponse;

public class Bounds{
	private Southwest southwest;
	private Northeast northeast;

	public void setSouthwest(Southwest southwest){
		this.southwest = southwest;
	}

	public Southwest getSouthwest(){
		return southwest;
	}

	public void setNortheast(Northeast northeast){
		this.northeast = northeast;
	}

	public Northeast getNortheast(){
		return northeast;
	}

	@Override
 	public String toString(){
		return 
			"Bounds{" + 
			"southwest = '" + southwest + '\'' + 
			",northeast = '" + northeast + '\'' + 
			"}";
		}
}
