package com.github.lyokofirelyte.Spectral.StorageSystems;

import java.util.List;
import java.util.Map;

import org.bukkit.World;

import com.github.lyokofirelyte.Spectral.DataTypes.DRF;
import com.github.lyokofirelyte.Spectral.DataTypes.DRI;

public interface DivinityRegion extends DivinityStorage { 
	
	/**
	 * Quickly enables or disables a list of flags.
	 */
	public void quickSet(boolean enable, DRF... flags);
	
	/**
	 * @return The priority of the region. If two regions overlap, the one with the higher priority is chosen. <br />
	 * In the event that they are both the same, it will just pick one randomly.
	 */
	public int getPriority();
	
	/**
	 * @return The length of the region
	 */
	public int getLength();
	
	/**
	 * @return The width of the region
	 */
	public int getWidth();
	
	/**
	 * @return The height of the region
	 */
	public int getHeight();
	
	/**
	 * @return The total area of the region
	 */
	public int getArea();
	
	/**
	 * @return The top left block of the region
	 */
	public String getMaxBlock();
	
	/**
	 * @return The bottom left block of the region
	 */
	public String getMinBlock();
	
	/**
	 * @return The disable status of the region. If true, all flags will be considered void
	 */
	public boolean isDisabled();
	
	/**
	 * @return The status of the given flag - and false if it does not contain a flag
	 */
	public boolean getFlag(DRF flag);
	
	/**
	 * @return The total list of flags and their respective booleans
	 */
	public Map<DRF, Boolean> getFlags();

	/**
	 * @return A boolean determining if a player can build in this region
	 */
	public boolean canBuild(org.bukkit.entity.Player p);

	/**
	 * @return The world that the region is set in
	 */
	public World world(); 
	
	/**
	 * @return The world that the region is set in, but as a String
	 */
	public String getWorld(); 
	
	/**
	 * @return The list of allowed permissions to override flags
	 */
	public List<String> getPerms(); 
}