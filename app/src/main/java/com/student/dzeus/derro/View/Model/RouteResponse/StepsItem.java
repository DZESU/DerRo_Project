package com.student.dzeus.derro.View.Model.RouteResponse;

import com.google.gson.annotations.SerializedName;

public class StepsItem{
	private Duration duration;
	@SerializedName("start_location")
	private StartLocation startLocation;
	private Distance distance;
	private String travelMode;
	@SerializedName("end_location")
	private EndLocation endLocation;
	@SerializedName("html_instructions")
	private String htmlInstructions;
	private Polyline polyline;

	public void setDuration(Duration duration){
		this.duration = duration;
	}

	public Duration getDuration(){
		return duration;
	}

	public void setStartLocation(StartLocation startLocation){
		this.startLocation = startLocation;
	}

	public StartLocation getStartLocation(){
		return startLocation;
	}

	public void setDistance(Distance distance){
		this.distance = distance;
	}

	public Distance getDistance(){
		return distance;
	}

	public void setTravelMode(String travelMode){
		this.travelMode = travelMode;
	}

	public String getTravelMode(){
		return travelMode;
	}

	public void setHtmlInstructions(String htmlInstructions){
		this.htmlInstructions = htmlInstructions;
	}

	public String getHtmlInstructions(){
		return htmlInstructions;
	}

	public void setEndLocation(EndLocation endLocation){
		this.endLocation = endLocation;
	}

	public EndLocation getEndLocation(){
		return endLocation;
	}

	public void setPolyline(Polyline polyline){
		this.polyline = polyline;
	}

	public Polyline getPolyline(){
		return polyline;
	}

	@Override
 	public String toString(){
		return 
			"StepsItem{" + 
			"duration = '" + duration + '\'' + 
			",start_location = '" + startLocation + '\'' + 
			",distance = '" + distance + '\'' + 
			",travel_mode = '" + travelMode + '\'' + 
			",html_instructions = '" + htmlInstructions + '\'' + 
			",end_location = '" + endLocation + '\'' + 
			",polyline = '" + polyline + '\'' + 
			"}";
		}
}
