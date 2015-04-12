package com.github.lyokofirelyte.Creator.Events.Listeners.System;

import java.util.HashMap;

import org.bukkit.event.EventHandler;

import com.github.lyokofirelyte.Creator.VTParser;
import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Events.VTSystemEvent;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.github.lyokofirelyte.Creator.Identifiers.VTData;
import com.github.lyokofirelyte.Creator.Identifiers.VTMap;

public class SystemDisable extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public SystemDisable(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/system", "SystemDisable.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onSystemDisable(VTSystemEvent e){
		
		if (getLong("ActiveCooldown") <= System.currentTimeMillis() && e.getType().equals(VTData.DISABLE)){
			if (getList("main").size() > 0){
				new VTParser(main, "SystemDisable.yml", "main", getList("main"), e.getLocation(), new HashMap<String, String>(), e.getSender()).start();
				cooldown();
			}
		}
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}