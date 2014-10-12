package com.github.lyokofirelyte.Spectral.DataTypes;

public enum ElyTask {

	/**
	 * Performs a sweep of recently logged blocks and adds them to file
	 */
	LOGGER("LOGGER"),
	
	/**
	 * Performs a sweep (20 sec) for various activities (see ElyWatch.java)
	 */
	WATCHER("WATCHER"),
	
	/**
	 * Used for the /spectate command to update the player location
	 */
	SPECTATE("SPECTATE"),
	
	/**
	 * Cleans out the list of MMO blocks that are marked as invalid
	 */
	MMO_BLOCKS("MMO_BLOCKS"),
	
	/**
	 * 20 minute interval of announcements, programmable with /announcer
	 */
	ANNOUNCER("ANNOUNCER"),
	
	/**
	 * 20 minute interval of saving data to file
	 */
	AUTO_SAVE("AUTO_SAVE"),
	
	/**
	 * Pulls data from the website chat to the server chat and updates everyone
	 */
	WEBSITE("WEBSITE");
	
	ElyTask(String type){
		taskType = type;
	}
	
	String taskType;
}