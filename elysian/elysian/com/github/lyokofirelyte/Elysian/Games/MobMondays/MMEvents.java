package com.github.lyokofirelyte.Elysian.Games.MobMondays;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.MobMondays.MMMain.locationType;

public class MMEvents implements Listener{

	MMMain root;
	Elysian main;
	
	public MMEvents(MMMain i){
		root = i;
		main = i.main;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(root.players.contains(e.getPlayer().getName())){
			root.players.remove(e.getPlayer().getName());
			e.getPlayer().getActivePotionEffects().clear();
			
			if(root.selected.containsKey(e.getPlayer().getName())){
				root.selected.remove(e.getPlayer().getName());
			}
			
		}
	}
	
	@EventHandler
	public void onRespawn(final PlayerRespawnEvent e){
		if(root.players.contains(e.getPlayer().getName())){
			root.players.remove(e.getPlayer().getName());
			e.getPlayer().getActivePotionEffects().clear();
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){

				@Override
				public void run() {
					e.getPlayer().teleport(root.getLocation(locationType.DEATH));
					
				}
				
			}, 5L);
			
		}
		
		if(root.selected.containsKey(e.getPlayer().getName())){
			root.selected.remove(e.getPlayer().getName());
		}
		
	}

	
}
