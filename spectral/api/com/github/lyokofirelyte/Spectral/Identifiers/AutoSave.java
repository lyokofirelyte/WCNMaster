package com.github.lyokofirelyte.Spectral.Identifiers;

/**
 * An interface representing an object that wishes to be auto-saved
 */
public interface AutoSave {
	
	/**
	 * Called when the system is starts an auto-save
	 */
	public void save();
	
	/**
	 * Called when the system boots up and is not usually used
	 */
	public void load();
}