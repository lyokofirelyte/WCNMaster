package com.github.lyokofirelyte.Elysian.Games.Gotcha;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.Gotcha.GotchaData.GotchaGame;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Public.ParticleEffect;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class GotchaActive implements Listener {

	private Elysian main;
	private Gotcha root;
	
	public GotchaActive(Gotcha i){
		root = i;
		main = root.main;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (root.isPlayerInGame(dp)){
			root.getGameWithPlayer(dp).remPlayer(dp);
			root.getGameWithPlayer(dp).msg(e.getPlayer().getDisplayName() + " &c&ohas left the Gotcha game!");
		}
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent e){
		
		if (e.getPlayer() instanceof Player){
			if (root.isPlayerInGame(main.api.getDivPlayer((Player) e.getPlayer()))){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		
		if (e.getEntity() instanceof Player){
			if (root.getGameWithPlayer(main.api.getDivPlayer((Player)e.getEntity())) != null){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onHit(ProjectileHitEvent e){
		
		if (e.getEntity() instanceof SmallFireball){
			
			SmallFireball ball = (SmallFireball) e.getEntity();
			DivinityPlayer shooter = null;
			DivinityPlayer victim = null;
			GotchaGame game = null;
			
			if (ball.getShooter() instanceof Player){
				
				shooter = main.api.getDivPlayer((Player) ball.getShooter());
				game = root.getGameWithPlayer(shooter);

				if (game != null){
					for (Entity v : ball.getNearbyEntities(2D, 2D, 2D)){
						if (v instanceof Player){
							victim = main.api.getDivPlayer((Player) v);
							main.api.cancelTask(root.tasks.get(ball));
							ParticleEffect.ANGRY_VILLAGER.display(1, 1, 1, 0, 2000, v.getLocation(), 100);
							v.getWorld().playSound(v.getLocation(), Sound.EXPLODE, 15F, 15F);
							game.addPoint(shooter);
							main.api.event(new DivinityTeleportEvent(Bukkit.getPlayer(victim.uuid()), game.getRandomSpawnPoint()));
							game.msg("&6&oKill-Feed&f: " + shooter.getStr(DPI.DISPLAY_NAME) + " &7&o" + GotchaWords.generate() + "'d &7" + victim.getStr(DPI.DISPLAY_NAME));
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onLaunch(PlayerInteractEvent e){
		
		if (e.getAction() == Action.RIGHT_CLICK_AIR && e.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_HOE)){
			
			Player p = e.getPlayer();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			
			if (dp.getBool(DPI.IN_GAME) && root.getGameWithPlayer(dp) != null && root.getGameWithPlayer(dp).isCooldownDone(dp)){
				root.getGameWithPlayer(dp).resetCooldown(dp);
				Location eyeLocation = p.getLocation();
				eyeLocation.setY(eyeLocation.getY() + 1.5);
				Location frontLocation = eyeLocation.add(eyeLocation.getDirection());
				
				SmallFireball fireball = (SmallFireball) p.getWorld().spawnEntity(frontLocation, EntityType.SMALL_FIREBALL);
				fireball.setShooter(p);
				fireball.setVelocity(p.getLocation().getDirection().multiply(2.2));
				
				String taskName = "gotchaLaser" + p.getName() + new Random().nextInt(1000);
				
				root.tasks.put(fireball, taskName);
				main.api.repeat(this, "gotchaLaser", 0L, 1L, taskName, fireball);
			}
		}
	}
	
	public void gotchaLaser(SmallFireball ball){
		if (!ball.isDead()){
			ParticleEffect.DRIP_WATER.display(0, 0, 0, 1, 100, ball.getLocation(), 30);
			ParticleEffect.RED_DUST.display(0, 0, 0, 1, 50, ball.getLocation(), 30);
		} else {
			main.api.cancelTask(root.tasks.get(ball));
			root.tasks.remove(ball);
		}
	}
	
	@EventHandler
	public void onScoreboard(ScoreboardUpdateEvent e){
		
		if (e.getReason().contains("gameGotcha")){
			
			Player p = e.getPlayer();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			GotchaGame game = root.getGameWithPlayer(dp);
			Map<String, Integer> topScores = new THashMap<String, Integer>();
			List<Integer> sortedScores = new ArrayList<Integer>();
			boolean form = false;
			
			if (game != null){
					
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
				Objective o = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
					
				if (o == null || !o.getName().equals("gameGotcha")){
					if (o != null){
						o.unregister();
					}
					o = board.registerNewObjective("gameGotcha", "dummy");
					o.setDisplaySlot(DisplaySlot.SIDEBAR);
					form = true;
				}
				
				o.setDisplayName("§6Gotcha! " + game.getTimeLeft());
	
				for (DivinityPlayer player : game.getPlayers()){
					topScores.put(player.getStr(DPI.DISPLAY_NAME), game.getScore(player));
					sortedScores.add(game.getScore(player));
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
				
				if (sortedScores.get(0) > 30 && sortedScores.get(0) > sortedScores.get(1) + 3){
					game.stop();
				}
			}
		}
	}
}