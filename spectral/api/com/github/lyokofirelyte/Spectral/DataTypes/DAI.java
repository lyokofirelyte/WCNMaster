package com.github.lyokofirelyte.Spectral.DataTypes;

public enum DAI {
	
	/**
	 * The actual name of the alliance.
	 * @dataType String
	 */
	NAME("NAME"),
	
	/**
	 * The tier (starting at 0) of the alliance.
	 * @dataType int
	 */
	TIER("TIER"),
	
	/**
	 * The total funds of the alliance. This never goes down.
	 * @dataType int
	 */
	BALANCE("BALANCE"),
	
	/**
	 * A list of UUIDs representing players in the alliance.
	 * @dataType String List (each one is a UUID.toString())
	 */
	MEMBERS("MEMBERS"),
	
	/**
	 * The first half of the alliance color
	 * @dataType String
	 * @outputExample &6
	 */
	COLOR_1("COLOR_1"),
	
	/**
	 * The second half of the alliance color
	 * @dataType String
	 * @outputExample &d
	 */
	COLOR_2("COLOR_2"),
	
	/**
	 * The UUID of the leader of the alliance
	 * @dataType String (UUID.toString());
	 */
	LEADER("LEADER"),
	
	/**
	 * The description of the alliance that leaders can set
	 * @dataType String
	 */
	DESC("DESC"),
	
	/**
	 * The coords of the center (x y z)
	 * @dataType String
	 * @outputExample 10 50 0
	 */
	CENTER("CENTER");

	DAI(String info){
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