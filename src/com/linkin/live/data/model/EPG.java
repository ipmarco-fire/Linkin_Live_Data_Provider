package com.linkin.live.data.model;

public class EPG {
	String startTime;
	String name;
	
	public String getShortStartTime(){
	    if(startTime!=null && startTime.length() > 10){
	        return startTime.substring(10);
	    }
	    return "";
	}
	
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
