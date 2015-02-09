package com.github.lyokofirelyte.Elysian.Games.MobMondays;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.MobMondays.MMMain.locationType;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
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
			Map<String, Integer> topScores = new THashMap<String, Integer>();
			List<Integer> sortedScores = new ArrayList<Integer>();
			boolean form = false;
			System.out.println("updati3ng");

			if (root.active == true){
					System.out.println("updating");
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
				Objective o = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

				if (o == null || !o.getName().equals("mobMondaysGame")){
					if (o != null){
						o.unregister();
						System.out.println("unregistering");
					}
					o = board.registerNewObjective("mobMondaysGame", "dummy");
					o.setDisplaySlot(DisplaySlot.SIDEBAR);
					form = true;
				
				}
				
				o.setDisplayName("§6MM Kills:");
				
				for (String s : root.players){
					DivinityPlayer player = main.api.getDivPlayer(s);
					topScores.put(player.getStr(DPI.DISPLAY_NAME), root.scores.get(s));
					sortedScores.add(root.scores.get(s));
				}
				
				Collections.sort(sortedScores);
				Collections.reverse(sortedScores);
				
				for (int score : sortedScores){
					for (String name : topScores.keySet()){
						if (topScores.get(name) == score){
							Score s = o.getScore(main.AS(name));
							s.setScore(score);
						}
					}
				}
				
				if (form){
					p.setScoreboard(board);
				}
			
			}
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
			DivinityPlayer dp = main.api.getDivPlayer(p.getName());
			dp.set(DPI.IN_GAME, false);

			
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
