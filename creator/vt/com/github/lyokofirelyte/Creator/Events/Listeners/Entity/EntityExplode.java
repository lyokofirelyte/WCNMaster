package com.github.lyokofirelyte.Creator.Events.Listeners.Entity;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.lyokofirelyte.Creator.VTParser;
import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.github.lyokofirelyte.Creator.Identifiers.VTMap;

public class EntityExplode extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public EntityExplode(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "EntityExplode.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onExplode(EntityExplodeEvent e){
		
		if (getList("Worlds").contains(e.getEntity().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "EntityExplode.yml", "main", getList("main"), e.getEntity().getLocation(), getCustoms(e), e.getEntity().getType().name().toLowerCase()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(EntityExplodeEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		String type = e.getEntity().getType().name();
		int x = 0;
		
		map.put("<entitytype>", type.substring(0, 1) + type.substring(1).toLowerCase());
		map.put("<yeild>", e.getYield() + "");
		map.put("<explodedblock:amount>", e.blockList().size() + "");
		
		for (Block b : e.blockList()){
			Location l = b.getLocation();
			map.put("<explodedblock:" + x + ":type>", b.getType().name().toLowerCase());
			map.put("<explodedblock:" + x + ":byte>", b.getData() + "");
			map.put("<explodedblock:" + x + ":location>", l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
		}

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}