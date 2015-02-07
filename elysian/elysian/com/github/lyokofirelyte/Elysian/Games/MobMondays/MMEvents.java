package com.github.lyokofirelyte.Elysian.Games.MobMondays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;

import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.MobMondays.MMMain.locationType;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class MMEvents implements Listener{

	MMMain root;
	Elysian main;
	
	public MMEvents(MMMain i){
		root = i;
		main = i.main;
	}
	
	@EventHandler
	public void onScoreboard(ScoreboardUpdateEvent e){
		
		if (e.getReason().contains("mobMondaysGame")){
			
			Player p = e.getPlayer();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			
			
		}

	}
			
	@EventHandler
	public void onDeath(EntityDeathEvent e){
		
		if(e.getEntity().getKiller() instanceof Player){
			Player p = (Player) e.getEntity().getKiller();
			if(root.players.contains(p.getName())){
				
				if(root.scores.containsKey(p.getName())){
					root.scores.put(p.getName(), root.scores.get(p.getName()) + 1);
				}else{
					root.scores.put(p.getName(), 1);
				}
				
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(root.players.contains(p.getName())){
			root.players.remove(p.getName());
			main.api.cancelTask("mobMondaysScore" + p.getName());

			for(PotionEffect pe : p.getActivePotionEffects()){
				p.removePotionEffect(pe.getType());
			}			
			
			if(root.selected.containsKey(p.getName())){
				root.selected.remove(p.getName());
			}
			
		}
	}
	
	@EventHandler
	public void onRespawn(final PlayerRespawnEvent e){
		final Player p = e.getPlayer();
		if(root.players.contains(p.getName())){
			root.players.remove(p.getName());
			main.api.cancelTask("mobMondaysScore" + p.getName());

			for(PotionEffect pe : p.getActivePotionEffects()){
				p.removePotionEffect(pe.getType());
			}			
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){

				@Override
				public void run() {
					p.teleport(root.getLocation(locationType.DEATH));
					
				}
				
			}, 5L);
			
		}
		
		if(root.selected.containsKey(p.getName())){
			root.selected.remove(p.getName());
		}
		
	}

	
}
