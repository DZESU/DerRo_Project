package com.student.dzeus.derro.View.Model.RouteResponse;

import java.util.List;

public class RouteResponse{
	private List<RoutesItem> routes;
	private List<GeocodedWaypointsItem> geocodedWaypoints;
	private String status;

	public void setRoutes(List<RoutesItem> routes){
		this.routes = routes;
	}

	public List<RoutesItem> getRoutes(){
		return routes;
	}

	public void setGeocodedWaypoints(List<GeocodedWaypointsItem> geocodedWaypoints){
		this.geocodedWaypoints = geocodedWaypoints;
	}

	public List<GeocodedWaypointsItem> getGeocodedWaypoints(){
		return geocodedWaypoints;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"RouteResponse{" + 
			"routes = '" + routes + '\'' + 
			",geocoded_waypoints = '" + geocodedWaypoints + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}