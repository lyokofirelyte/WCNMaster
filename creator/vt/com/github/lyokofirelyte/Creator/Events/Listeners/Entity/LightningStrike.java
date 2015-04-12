package com.github.lyokofirelyte.Creator.Events.Listeners.Entity;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.LightningStrikeEvent;

import com.github.lyokofirelyte.Creator.VTParser;
import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.github.lyokofirelyte.Creator.Identifiers.VTMap;

public class LightningStrike extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public LightningStrike(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "LightningStrike.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onNote(LightningStrikeEvent e){
		
		if (getList("Worlds").contains(e.getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "LightningStrike.yml", "main", getList("main"), e.getLightning().getLocation(), new HashMap<String, String>(), "lightning_strike").start();
					cooldown();
				}
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