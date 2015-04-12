package com.github.lyokofirelyte.Creator.Events.Listeners.Player;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;

import com.github.lyokofirelyte.Creator.VTParser;
import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.github.lyokofirelyte.Creator.Identifiers.VTMap;

public class PlayerHoldItem extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerHoldItem(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerHoldItem.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onHold(PlayerItemHeldEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					if (getBool("Cancelled")){
						e.setCancelled(true);
					}
					new VTParser(main, "PlayerHoldItem.yml", "main", getList("main"), e.getPlayer().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(PlayerItemHeldEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("<oldslot>", e.getPreviousSlot() + "");
		map.put("<newSlot>", e.getNewSlot() + "");

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}