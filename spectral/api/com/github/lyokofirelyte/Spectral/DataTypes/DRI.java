package com.github.lyokofirelyte.Spectral.DataTypes;

public enum DRI {
	
	/**
	 * A list of permissions that allow bypassing of flags on the region
	 * @dataType String List
	 */
	PERMS("PERMS"),
	
	/**
	 * The priority level of the region - higher priority regions will be used in the event that two overlap
	 * @dataType int
	 */
	PRIORITY("PRIORITY"),
	
	/**
	 * The total amount of blocks that make of the length of the region
	 * @dataType int
	 */
	LENGTH("LENGTH"),
	
	/**
	 * The total amount of blocks that make of the height of the region
	 * @dataType int
	 */
	HEIGHT("HEIGHT"),
	
	/**
	 * The total amount of blocks that make of the width of the region
	 * @dataType int
	 */
	WIDTH("WIDTH"),
	
	/**
	 * The total amount of blocks that make of the area of the region
	 * @dataType int
	 */
	AREA("AREA"),
	
	/**
	 * The top-left block of the region
	 * @dataType String
	 */
	MAX_BLOCK("MAX_BLOCK"),
	
	/**
	 * The bottom-right block of the region
	 * @dataType String
	 */
	MIN_BLOCK("MIN_BLOCK"),
	
	/**
	 * A boolean determining if the flags should be ignored or not
	 * @dataType boolean
	 */
	DISABLED("DISABLED"),
	
	/**
	 * The name of the world that the region is located in
	 * @dataType String
	 */
	WORLD("WORLD");

	DRI(String info){
		this.info = info;
	}
	
	public String info;
	
	/**
 	 * @return A string-safe version of the enum value
	 */
	public String s(){
		return info;
	}
}