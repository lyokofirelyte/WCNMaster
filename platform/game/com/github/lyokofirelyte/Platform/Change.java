package com.github.lyokofirelyte.Platform;

public enum Change {

	ROW("row"),
	COLUMN("column"),
	GRID("grid");
	
	String changeType;
	
	Change(String ct){
		changeType = ct;
	}
}