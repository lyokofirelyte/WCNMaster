package com.github.lyokofirelyte.Spectral.StorageSystems;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface DivinityRing extends DivinityStorage  {
	
	/**
	 * @return Alliance owned or system
	 */
	public boolean isAllianceOwned();
	
	/**
	 * @return The center of the ring, already split up for you
	 */
	public String[] getCenter();
	
	/**
	 * @return The string location for the destination ring
	 */
	public String getDest();
	
	/**
	 * @return The string location for the center of the ring
	 */
	public Location getCenterLoc();
	
	/**
	 * @return A boolean determining if the ring is currently in use or not
	 */
	public boolean isInOperation();
	
	/**
	 * @return The ID of the material used for the physical rings
	 */
	public int getMatId();
	
	/**
	 * @return The byte data for the ID of the material used for the physical ringss
	 */
	public byte getMatByte();
	
	/**
	 * @return A list of players that will be teleported
	 * @deprecated
	 */
	public List<Player> getPlayers();
	
	/**
	 * Adds a player to be teleported
	 * @deprecated
	 */
	public void addPlayer(Player name);
	
	/**
	 * Removes a player to be teleported
	 * @deprecated
	 */
	public void remPlayer(Player name);
	
	/**
	 * Changes the operation status of the ring
	 */
	public void setInOperation(boolean b);
}