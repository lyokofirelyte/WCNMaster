package com.github.lyokofirelyte.Spectral.DataTypes;


public enum DRS {
	
	IS_SERVER_RING("IS_SERVER_RING"),
	IS_ALLIANCE_OWNED("IS_ALLIANCE_OWNED"),
	ALLIANCE("ALLIANCE"),
	FUEL("FUEL"),

	/**
	 * The center of the ring location (world x y z)
	 * @dataType String
	 */
	CENTER("CENTER"),
	
	/**
	 * The destination ring
	 * @dataType String
	 */
	DEST("DEST"),
	
	/**
	 * The ID of the material, ex: Air is 0.
	 * @dataType int
	 */
	MAT_ID("MAT_ID"),
	
	/**
	 * The byte ID of the material, ex: colored wool
	 * @dataType byte
	 */
	BYTE_ID("BYTE_ID");
	
	DRS(String s){
		this.s = s;
	}
	
	public String s;
	
	/**
 	 * @return A string-safe version of the enum value
	 */
	public String s(){
		return s;
	}	
}