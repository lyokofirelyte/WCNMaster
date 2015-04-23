package com.github.lyokofirelyte.Empyreal.Elysian;

public enum RingsType {

	MENU("MENU"),
	SYSTEM("SYSTEM"),
	ALLIANCE("ALLIANCE"),
	SERVER("SERVER");
	
	RingsType(String type){
		this.type = type;;
	}
	
	String type;
	
	public String getType(){
		return type;
	}
}