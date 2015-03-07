package com.github.lyokofirelyte.Empyreal;

import org.bukkit.entity.Player;

public interface GameModule {
	
	public String getPackageName();
	
	public String getJarName();
	
	public void onRegister();
	
	public void closing();
	
	public void shutdown();
	
	public void onPlayerJoin(Player p);
	
	public void onPlayerQuit(Player p);
}