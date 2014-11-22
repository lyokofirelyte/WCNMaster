package com.github.lyokofirelyte.Spectral.DataTypes;

public enum DPS {

	/**
	 * Represents the center location of the patrol entrance
	 * @dataType Location
	 */
	CENTER("CENTER"),
	
	/**
	 * Represents all of the involved players
	 * @dataType List<Player>
	 */
	PLAYERS("PLAYERS"),

	/**
	 * The ender crystal that marks the entrance of the patrol
	 * @dataType LivingEntity
	 */
	ENTRANCE_CRYSTAL("ENTRANCE_CRYSTAL");
	
	DPS(String name){
		this.name = name;
	}
	
	String name;
	
	/**
	 * @return A string-safe version of the enum name
	 */
	public String s(){
		return name;
	}
}