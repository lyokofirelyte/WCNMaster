package com.github.lyokofirelyte.Empyreal;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public interface GamePlayer<T> {

	public T getType();
	
	public UUID getUUID();
	
	public String getName();
	
	public List<String> getPerms();
	
	public Player getPlayer();
	
	public void s(String msg);
}