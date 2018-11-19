package com.student.dzeus.derro.View.Model.RouteResponse;

public class OverviewPolyline{
	private String points;

	public void setPoints(String points){
		this.points = points;
	}

	public String getPoints(){
		return points;
	}

	@Override
 	public String toString(){
		return 
			"OverviewPolyline{" + 
			"points = '" + points + '\'' + 
			"}";
		}
}
