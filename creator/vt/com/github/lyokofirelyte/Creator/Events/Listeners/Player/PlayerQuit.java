package com.github.lyokofirelyte.Creator.Events.Listeners.Player;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.lyokofirelyte.Creator.VTParser;
import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.github.lyokofirelyte.Creator.Identifiers.VTMap;

public class PlayerQuit extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerQuit(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerQuit.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onJoin(PlayerQuitEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					if (getBool("Cancelled")){
						e.setQuitMessage(null);
					}
					new VTParser(main, "PlayerQuit.yml", "main", getList("main"), e.getPlayer().getLocation(), new HashMap<String, String>(), e.getPlayer().getName()).start();
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