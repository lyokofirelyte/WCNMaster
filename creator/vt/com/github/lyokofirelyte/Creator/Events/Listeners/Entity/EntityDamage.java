package com.github.lyokofirelyte.Creator.Events.Listeners.Entity;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.lyokofirelyte.Creator.VTParser;
import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.github.lyokofirelyte.Creator.Identifiers.VTMap;

public class EntityDamage extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public EntityDamage(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "EntityDamage.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onDamage(EntityDamageByEntityEvent e){
		
		if (getList("Worlds").contains(e.getEntity().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "EntityDamage.yml", "main", getList("main"), e.getEntity().getLocation(), getCustoms(e), e.getEntity().getType().name().toLowerCase()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(EntityDamageByEntityEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		String type = e.getEntity().getType().name();
		
		map.put("<whodied>", type.substring(0, 1) + type.substring(1).toLowerCase());
		map.put("<damagedbyplayer>", (e.getDamager() instanceof Player) + "");
		map.put("<killername>", e.getDamager() instanceof Player ? ((Player) e.getDamager()).getName() : type.substring(0, 1) + type.substring(1).toLowerCase());
		map.put("<damageamount>", e.getDamage() + "");
		map.put("<damagecause>", e.getCause().name().toLowerCase());

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}