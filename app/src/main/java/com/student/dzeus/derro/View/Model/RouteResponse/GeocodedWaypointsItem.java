package com.student.dzeus.derro.View.Model.RouteResponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeocodedWaypointsItem{
	private List<String> types;
	@SerializedName("geocoded_waypoints")
	private String geocoderStatus;
	@SerializedName("place_id")
	private String placeId;

	public void setTypes(List<String> types){
		this.types = types;
	}

	public List<String> getTypes(){
		return types;
	}

	public void setGeocoderStatus(String geocoderStatus){
		this.geocoderStatus = geocoderStatus;
	}

	public String getGeocoderStatus(){
		return geocoderStatus;
	}

	public void setPlaceId(String placeId){
		this.placeId = placeId;
	}

	public String getPlaceId(){
		return placeId;
	}

	@Override
 	public String toString(){
		return 
			"GeocodedWaypointsItem{" + 
			"types = '" + types + '\'' + 
			",geocoder_status = '" + geocoderStatus + '\'' + 
			",place_id = '" + placeId + '\'' + 
			"}";
		}
}