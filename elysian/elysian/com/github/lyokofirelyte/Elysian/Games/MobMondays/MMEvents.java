package com.github.lyokofirelyte.Elysian.Games.MobMondays;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
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

			if (root.allowedToJoin == true){
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
				Objective o = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

				if (o == null || !o.getName().equals("mobMondaysGame")){
					if (o != null){
						o.unregister();
					}
					o = board.registerNewObjective("mobMondaysGame", "dummy");
					o.setDisplaySlot(DisplaySlot.SIDEBAR);
					form = true;
				
				}
				
				o.setDisplayName("§6MM Kills:");
				
				for (String s : root.startingPlayers){
					DivinityPlayer player = main.api.getDivPlayer(s);
//					String strippedName = player.getStr(DPI.DISPLAY_NAME).replace(player.getStr(DPI.ALLIANCE_COLOR_1), "").replace(player.getStr(DPI.ALLIANCE_COLOR_2), "");
					topScores.put(player.getStr(DPI.DISPLAY_NAME), root.scores.get(s));
					sortedScores.add(root.scores.get(s));
				}
				
				Collections.sort(sortedScores);
				Collections.reverse(sortedScores);
				
				for (int score : sortedScores){
					for (String name : topScores.keySet()){
						if (topScores.get(name) == score){
//							System.out.println(name);
//							System.out.println(ChatColor.stripColor(name));
							if(!root.currentPlayers.contains(ChatColor.stripColor(name))){
								Score s = o.getScore(main.AS("&4" + ChatColor.stripColor(name)));
								s.setScore(score);
//								System.out.println(name + " is dead");
							}else{
								Score s = o.getScore(main.AS(name));
								s.setScore(score);
//								System.out.println(name + " is alive");

							}
						}
					}
				}
				
				for(ItemStack i : p.getInventory().getArmorContents()){
					i.setDurability((short)(i.getType().getMaxDurability() - i.getType().getMaxDurability()));
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
			if(root.currentPlayers.contains(p.getName())){
				
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
		if(root.currentPlayers.contains(p.getName())){
			root.currentPlayers.remove(p.getName());

			main.api.cancelTask("mobMondaysScore" + p.getName());

			for(PotionEffect pe : p.getActivePotionEffects()){
				p.removePotionEffect(pe.getType());
			}			
			
			if(root.selected.containsKey(p.getName())){
				root.selected.remove(p.getName());
			}
			DivinityPlayer dp = main.api.getDivPlayer(p);
			dp.set(DPI.IN_GAME, false);
			p.getActivePotionEffects().clear();
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
			main.api.cancelTask("mobMondaysScore" + p.getName());
			root.msg(dp.name() + " &b has left MobMondays!");

		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		Player p = e.getPlayer();
		final Player p2 = e.getPlayer();
		if(root.currentPlayers.contains(p.getName())){
			root.currentPlayers.remove(p.getName());
			main.api.cancelTask("mobMondaysScore" + p.getName());

			for(PotionEffect pe : p.getActivePotionEffects()){
				p.removePotionEffect(pe.getType());
			}			
			p.getInventory().clear();

			Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){

				@Override
				public void run() {
					p2.teleport(root.getLocation(locationType.DEATH));
					
				}
				
			}, 5L);
			
		
			if(root.selected.containsKey(p.getName())){
				root.selected.remove(p.getName());
			}
			DivinityPlayer dp = main.api.getDivPlayer(p);
			dp.set(DPI.IN_GAME, false);
			p.getActivePotionEffects().clear();
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
			main.api.cancelTask("mobMondaysScore" + p.getName());
			root.msg(dp.name() + " &b has died during MobMondays!");
		}

	}

	
	@EventHandler
	public void onDamge(EntityDamageByEntityEvent e){
		if(((e.getCause() == DamageCause.ENTITY_EXPLOSION && !(e.getDamager() instanceof Creeper))|| (e.getCause() == DamageCause.ENTITY_ATTACK) && e.getEntity() instanceof Player && e.getDamager() instanceof Player)){
			Player p = (Player) e.getEntity();
			if(root.currentPlayers.contains(p.getName())){
				e.setCancelled(true);
			}
			
		}else if(e.getDamager() instanceof Arrow){
			Arrow ar = (Arrow) e.getDamager();
			if(ar.getShooter() instanceof Player){
				if(root.currentPlayers.contains(((Player)ar.getShooter()).getName()) && e.getEntity() instanceof Player){
					e.setCancelled(true);
				}
			}
		}
	}
	
}
