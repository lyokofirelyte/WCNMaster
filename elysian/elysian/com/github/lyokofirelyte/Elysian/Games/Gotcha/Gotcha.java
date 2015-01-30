package com.github.lyokofirelyte.Elysian.Games.Gotcha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;

import gnu.trove.map.hash.THashMap;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.Gotcha.GotchaData.GotchaGame;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;
import com.github.lyokofirelyte.Spectral.Identifiers.DivGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class Gotcha extends THashMap<String, GotchaGame> implements AutoSave, AutoRegister, DivGame {
	
	protected Elysian main;
	public GotchaActive active;
	public GotchaCommand command;
	public Map<SmallFireball, String> tasks = new THashMap<SmallFireball, String>();

	public Gotcha(Elysian i){
		main = i;
		active = new GotchaActive(this);
		command = new GotchaCommand(this);
	}
	
	public boolean isPlayerInGame(DivinityPlayer dp){
		
		if (size() > 0){
			for (GotchaGame g : values()){
				if (g.getPlayers().contains(dp)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public GotchaGame getGameWithPlayer(DivinityPlayer dp){
		
		for (GotchaGame g : values()){
			if (g.getPlayers().contains(dp)){
				return g;
			}
		}
		
		return null;
	}
	
	public GotchaGame getGame(final String name){
	
		if (!containsKey(name)){
			
			put(name, new GotchaGame(){
				
				private String gameName = name;
				
				private List<DivinityPlayer> players = new ArrayList<DivinityPlayer>();
				private List<Location> spawnPoints = new ArrayList<Location>();
				private Map<DivinityPlayer, Integer> scores = new THashMap<DivinityPlayer, Integer>();
				private Map<DivinityPlayer, Long> cooldowns = new THashMap<DivinityPlayer, Long>();
				
				private boolean inProgress = false;
				private boolean addQueue = false;
				public int secondsLeft = 0;
				
				public String name(){
					return gameName;
				}
				
				public String getTimeLeft(){
					String time = Math.round(secondsLeft/60) + ":" + (secondsLeft % 60);
					return time.split(":")[1].length() == 1 ? time.replace(":", ":0") : time;
				}
				
				public boolean isInProgress(){
					return inProgress;
				}
				
				public int getScore(DivinityPlayer dp){
					return scores.containsKey(dp) ? scores.get(dp) : 0;
				}
				
				public boolean isCooldownDone(DivinityPlayer dp){
					return cooldowns.containsKey(dp) ? cooldowns.get(dp) <= System.currentTimeMillis() : true;
				}
				
				public Map<DivinityPlayer, Integer> getScores(){
					return scores;
				}
				
				public List<DivinityPlayer> getPlayers(){
					return players;
				}
				
				public List<Location> getAllSpawnPoints(){
					return spawnPoints;
				}
				
				public Location getRandomSpawnPoint(){
					return spawnPoints.get(new Random().nextInt(spawnPoints.size()));
				}
				
				public void resetCooldown(DivinityPlayer dp){
					
					final Player p = Bukkit.getPlayer(dp.uuid());
					cooldowns.put(dp, System.currentTimeMillis() + 2000L);
					
					if (p.getItemInHand().getType().equals(Material.DIAMOND_HOE)){
						
						final ItemMeta im = p.getItemInHand().getItemMeta();
						im.setDisplayName(main.AS("&c&oCooldown..."));
						p.getItemInHand().setItemMeta(im);
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){
							if (p.getItemInHand().getType().equals(Material.DIAMOND_HOE)){
								im.setDisplayName(main.AS("&a&oReady!"));
								p.getItemInHand().setItemMeta(im);
							}
						}}, 40L);
					}
				}
				
				public void setInProgress(boolean a){
					inProgress = a;
				}
				
				public void msg(String msg){
					for (DivinityPlayer dp : players){
						dp.s(msg);
					}
				}
				
				public void addPlayer(DivinityPlayer dp){
					
					players.add(dp);
					dp.set(DPI.IN_GAME, true);
					
					if (players.size() >= 3){
						start();
					}
					
					main.api.event(new ScoreboardUpdateEvent(Bukkit.getPlayer(dp.uuid()), "filler"));
				}
				
				public void remPlayer(DivinityPlayer dp){
					
					if (players.contains(dp)){
						main.api.cancelTask("gotchaScore" + dp.name());
						players.remove(dp);
						dp.set(DPI.IN_GAME, false);
					}
					
					if (players.size() <= 0){
						remove(name);
					}
				}
				
				public void tpAll(boolean in){
					
					if (in){
						
						for (int i = 0; i < players.size(); i++){
							if (Bukkit.getPlayer(players.get(i).uuid()) != null){
								main.api.event(new DivinityTeleportEvent(Bukkit.getPlayer(players.get(i).uuid()), (spawnPoints.size() > i ? spawnPoints.get(i) : getRandomSpawnPoint())));
							}
						}
						
					} else {
						
						for (DivinityPlayer dp : players){
							if (Bukkit.getPlayer(dp.uuid()) != null){
								main.api.event(new DivinityTeleportEvent(Bukkit.getPlayer(dp.uuid()), getLobby()));
							}
						}
					}
				}
				
				public Location getLobby(){
					String[] l = toDivGame().getString("Arenas." + name + ".lobby").split(" ");
					return new Location(Bukkit.getWorld(l[0]), Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[3]), Float.parseFloat(l[4]), Float.parseFloat(l[5]));
				}
				
				public void addPoint(DivinityPlayer dp){
					scores.put(dp, (scores.containsKey(dp) ? scores.get(dp) : 0) + 1);
				}
				
				public void start(){
					
					if (!addQueue){
						addQueue = true;
						secondsLeft = 600;
						msg("The game is starting in 30 seconds!");
						
						for (String loc : toDivGame().getStringList("Arenas." + name + ".Spawns")){
							String[] l = loc.split(" ");
							spawnPoints.add(new Location(Bukkit.getWorld(l[0]), Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[3]), Float.parseFloat(l[4]), Float.parseFloat(l[5])));
						}
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){
							
							actuallyStart();
							
						}}, 600L);
					}
				}
				
				public void stop(){
					
					tpAll(false);
					
					String winner = "";
					List<Integer> scorez = new ArrayList<Integer>();
					
					for (int i : scores.values()){
						scorez.add(i);
					}
					
					Collections.sort(scorez);
					Collections.reverse(scorez);
					
					for (DivinityPlayer user : scores.keySet()){
						if (scores.get(user) == scorez.get(0)){
							winner = user.getStr(DPI.DISPLAY_NAME);
						}
					}
					
					for (DivinityPlayer dp : players){
						dp.set(DPI.IN_GAME, false);
						Bukkit.getPlayer(dp.uuid()).getInventory().clear();
						Bukkit.getPlayer(dp.uuid()).setWalkSpeed(0.2F);
						Bukkit.getPlayer(dp.uuid()).getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
						main.api.cancelTask("gotchaScore" + dp.name());
					}
					
					DivinityUtilsModule.bc(winner + " &bhas won a game of Gotcha!");	
					main.api.cancelTask("gotchaCounter" + name);
					remove(name);
				}
				
				public void actuallyStart(){
					
					if (players.size() > 0){
						
						tpAll(true);
						setInProgress(true);
						
						ItemStack i = new ItemStack(Material.DIAMOND_HOE);
						ItemMeta im = i.getItemMeta();
						im.setDisplayName(main.AS("&a&oReady!"));
						im.setLore(Arrays.asList(main.AS("&d&oGotcha! Laser Weapon")));
						i.setItemMeta(im);
						
						for (DivinityPlayer dp : players){
							dp.set(DPI.IN_GAME, true);
							Bukkit.getPlayer(dp.uuid()).setWalkSpeed(0.4F);
							Bukkit.getPlayer(dp.uuid()).setGameMode(GameMode.CREATIVE);
							Bukkit.getPlayer(dp.uuid()).setFlying(false);
							Bukkit.getPlayer(dp.uuid()).setAllowFlight(false);
							Bukkit.getPlayer(dp.uuid()).getInventory().addItem(i);
							main.api.repeat(main.api, "event", 20L, 0L, "gotchaScore" + dp.name(), new ScoreboardUpdateEvent(Bukkit.getPlayer(dp.uuid()), "gameGotcha"));
						}
						
						main.divinity.api.activeTasks.put("gotchaCounter" + name, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){ public void run(){
							
							adjustTime();
							
						}}, 0L, 20L));
						
					} else {
						inProgress = false;
						remove(name);
					}
				}
				
				public void adjustTime(){
					secondsLeft -= 1;
					if (secondsLeft <= 0){
						main.api.cancelTask("gotchaCounter" + name);
						stop();
					}
				}
			});
			
		}
		return get(name);
	}

	@Override
	public void save(){}

	@Override
	public void load(){}

	public DivinityGame toDivGame(){
		return main.api.getDivGame("gotcha", "gotcha");
	}
	
	public Object[] registerSubClasses(){
		return new Object[]{
			command,
			active
		};
	}
}