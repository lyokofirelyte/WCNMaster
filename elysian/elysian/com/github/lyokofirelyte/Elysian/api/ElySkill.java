package com.github.lyokofirelyte.Elysian.api;

public enum ElySkill {
	
	/**
	 * Hamdrax-related shenans
	 */
	CHAOS("CHAOS"),
	
	/**
	 * Attacking things with a sword
	 */
	ATTACK("ATTACK"),

	/**
	 * Breaking certain blocks with a pickaxe
	 */
	MINING("MINING"),
	
	/**
	 * Hitting shit w/ an axe
	 */
	AXES("AXES"),
	
	/**
	 * Breaking logs with an axe
	 */
	WOODCUTTING("WOODCUTTING"),
	
	/**
	 * Breaking crops
	 */
	FARMING("FARMING"),
	
	/**
	 * Breaking certain blocks with a shovel
	 */
	DIGGING("DIGGING"),
	
	/**
	 * Crafting items
	 */
	CRAFTING("CRAFTING"),
	
	/**
	 * Placing blocks
	 */
	BUILDING("BUILDING"),
	
	/**
	 * Fall damage
	 */
	ENDURANCE("ENDURANCE"),
	
	/**
	 * Taking mob damage
	 */
	RESISTANCE("RESISTANCE"),
	
	/**
	 * Killing while under the influence of the vampyre vial
	 */
	VAMPYRISM("VAMPYRISM"),
	
	/**
	 * Stick fighting
	 */
	FENCING("FENCING"),
	
	/**
	 * Bow fighting
	 */
	ARCHERY("ARCHERY"),
	
	/**
	 * Group warfare
	 */
	PATROL("PATROL"),
	
	/**
	 * Offensive magic spells
	 */
	SOLAR("SOLAR"),
	
	/**
	 * Defensive / healing / assistive spells
	 */
	LUNAR("LUNAR");
	
	ElySkill(String skill){
		this.skill = skill;
	}
	
	/**
 	 * @return A string-safe version of the enum value
	 */
	public String s(){
		return skill;
	}

	String skill;
}