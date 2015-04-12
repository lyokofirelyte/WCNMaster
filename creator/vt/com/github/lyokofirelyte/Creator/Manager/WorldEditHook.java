package com.github.lyokofirelyte.Creator.Manager;

import org.bukkit.Bukkit;

import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class WorldEditHook implements AR {

	private VariableTriggers main;
	private WorldEditPlugin we;
	private boolean hooked;
	
	public WorldEditHook(VariableTriggers i){
		main = i;
	}
	
	public boolean hookSetup(){
		
		try {
			hooked = ((we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit")) != null);
		} catch (Exception e){
			hooked = false;
		}
		
		return hooked;
	}
	
	public WorldEditPlugin getWe(){
		return we;
	}
}