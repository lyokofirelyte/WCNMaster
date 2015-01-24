package com.github.lyokofirelyte.Elysian.Games.MobMondays;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
	public void onDeath(PlayerDeathEvent e){
		System.out.println(e.getEntity().getName());
		System.out.println(((Player)e.getEntity()).getName());
		if(root.players.contains(e.getEntity().getName())){
			root.players.remove(e.getEntity().getName());
			e.getEntity().getActivePotionEffects().clear();
			e.getEntity().teleport(root.getLocation(locationType.DEATH));
		}
		
		if(root.selected.containsKey(e.getEntity().getName())){
			root.selected.remove(e.getEntity().getName());
		}
		
	}

	
	
}
