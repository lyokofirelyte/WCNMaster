/*package com.github.lyokofirelyte.MM;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.JSONMap;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class MMMain extends JavaPlugin implements GameModule{
	
	@Getter @Setter
	private String jarName = "APIUsageExample-1.0.jar";
	
	@Getter @Setter
	private String packageName = "MMMain";
	
	@Getter @Setter 
	private Empyreal api;
	
	@Getter @Setter
	private JSONMap<Object, Object> save;
	
	MMCommands mmcmd;
	MMEvents mmevents;
	
	public List<String> currentPlayers = new ArrayList<String>();	
	public List<String> startingPlayers = new ArrayList<String>();	
	public Map<String, String> selected = new THashMap<String, String>();	
	public Map<String, Integer> scores = new THashMap<String, Integer>();	
	public Map<String, String> description = new THashMap<String, String>();	
	public enum locationType {DEATH, SPAWN, RANDOMMOB}; 
	
	@Getter @Setter
	int secondsLeft = 105;
	
	@Getter @Setter
	int timer;
	
	@Getter @Setter
	int round = 1;
	
	@Getter @Setter
	String current = "";
	String split = "%SPLIT%";
	
	@Getter @Setter
	public boolean gameStarted = false;
	public boolean allowedToJoin = false;
	
	
	public void onEnable(){
		
		setApi((Empyreal) Bukkit.getPluginManager().getPlugin("Empyreal"));
		getApi().registerModule(this);
				
		mmcmd = new MMCommands(this);
		mmevents = new MMEvents(this);

		description.put("mage", "Avada Kedavra");
		description.put("melee", "Move quick; stab fast");
		description.put("ranger", "No-scope your enemies");
		description.put("pyro", "Let your inner crazy out");
		description.put("barbarian", "Be a savage beast");
		description.put("healer", "Kill them with kindness");
		
		setSave(Empyreal.loadJSON("./plugins/MM/arenas.json"));
	}

	public void onDisable(){
		getApi().unregisterModule(this);
	}
	
	
	@Override
	public void onRegister(){
	
		// This will fire after the API registers the plugin. If this stuff happens, everything's OK so far.
		// do more stuff to set up your plugin if needed
		
		// Now, if your plugin is ready to accept people, tell the API to allow people to click on the sign to join.
		// The person who booted up the server will be moved over automatically after this.
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "server_boot_complete");
		
		
		// When the game is stared and you don't want anyone else to be able to join
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "game_in_progress");
		
	}
	
	@Override
	public void closing(){
		
	}
	
	@Override
	public void onPlayerJoin(Player p){
		GamePlayer<MMPlayer> player = new MMPlayer(p);
		
		getApi().registerPlayer(player);
		
		if(p.isOp()){
			player.getPerms().add("gameserver.staff");
			p.setOp(false);
		}
		
		p.getActivePotionEffects().clear();
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.teleport(getLocation(locationType.DEATH));
		currentPlayers.add(p.getName());
		startingPlayers.add(p.getName());
		Utils.bc(p.getName() + " has joined &aMobMondays!");
		p.setGameMode(GameMode.SURVIVAL);
	}
	
	@Override
	public void onPlayerQuit(Player p){
		getApi().unregisterPlayer(getApi().getGamePlayer(p.getUniqueId()));
	}
	
	@Override
	public void onPlayerChat(GamePlayer<?> gp, String message){
		Utils.bc("&7" + gp.getPlayer().getDisplayName() + "&f: " + message);
	}
	
	@Override
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}

	
	public String locationToString(Location loc){
		return loc.getWorld().getName() + split + loc.getX() + split + loc.getY() + split + loc.getZ();
	}
	
	public void spawnRandomMobs(){
		
		List<EntityType> mobs = new ArrayList<EntityType>(Arrays.asList(EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.ZOMBIE, EntityType.MAGMA_CUBE, EntityType.WITCH, EntityType.SILVERFISH, EntityType.SPIDER, EntityType.SLIME, EntityType.SKELETON));
		List<EntityType> bosses = new ArrayList<EntityType>(Arrays.asList(EntityType.GIANT, EntityType.ENDERMAN, EntityType.BLAZE));
		for(int i = 0; i < getSave().getList("arenas." + current + ".monsterspawn").size(); i++){
			Location loc = getLocation(locationType.RANDOMMOB);
			boolean spawnedWitch = false;
			for(int j = 0; j < startingPlayers.size() * 2 + round; j++){
				EntityType random = mobs.get(new Random().nextInt(mobs.size()));

				if(random.equals(EntityType.SKELETON)){
					Skeleton s = (Skeleton) loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
					s.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					s.getEquipment().setItemInHand(new ItemStack(Material.BOW));
				}else if(random.equals(EntityType.WITCH)){
					if(!spawnedWitch){
						spawnedWitch = true;
						Entity ent = loc.getWorld().spawnCreature(loc, random);
						LivingEntity livingent = (LivingEntity) ent;
						livingent.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					}
				}else{
					Entity ent = loc.getWorld().spawnCreature(loc, random);
					LivingEntity livingent = (LivingEntity) ent;
					livingent.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
				}
			
			}
			
			if(currentPlayers.size() <= 2){
				loc.getWorld().spawnCreature(loc, bosses.get(new Random().nextInt(bosses.size())));
			}
			
		}
		

	}
	
	public Location getLocation(locationType type){
		Location loc = null;
		String[] location = null;
		
		switch(type){
		
		case DEATH:
			
			location = getSave().getStr("arenas." + current + ".death").split(split);
			loc = new Location(Bukkit.getWorld(location[0]), Float.parseFloat(location[1]), Float.parseFloat(location[2]), Float.parseFloat(location[3]));
			
			break;
			
		case SPAWN:
			
			
			location = getSave().getStr("arenas." + current + ".spawn").split(split);
			loc = new Location(Bukkit.getWorld(location[0]), Float.parseFloat(location[1]), Float.parseFloat(location[2]), Float.parseFloat(location[3]));
			
			break;
			
		case RANDOMMOB:
			
			List<String> locations = getSave().getList("arenas." + current + ".monsterspawn");
			location = locations.get(new Random().nextInt(locations.size())).split(split);
			loc = new Location(Bukkit.getWorld(location[0]), Float.parseFloat(location[1]), Float.parseFloat(location[2]), Float.parseFloat(location[3]));
			
			break;
			
		}
		
		return loc;
	}
	
	
	public void start(String arena){
			setCurrent(arena);
			int timer = 60;

			Utils.bc("The game is starting in 60 seconds!");
			
			for(String s : currentPlayers){
				scores.put(s, 0);
			}
			
			
			/*APIScheduler.REPEAT.start(getApi(), "COUNTDOWN_TIMER", 20L, new Runnable(){
				public void run(){
					
					for(Player p : Bukkit.getOnlinePlayers()){
						p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(Utils.AS("&aMM! " + getApi().getTimeLeft(timer)));
					}
					
					APIScheduler.REPEAT.stop("COUNTDOWN_TIMER");

				}
			});
			
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){ public void run(){
				
				
				boolean canStart = true;
				StringBuilder notSelected = new StringBuilder();
				
				for(Player player : Bukkit.getOnlinePlayers()){
					if(getApi().getGamePlayer(player.getUniqueId(), MMPlayer.class).getType().getKit() == ""){
						canStart = false;
						notSelected.append(player.getName() + ", ");
					}
				}
				if(!canStart){
					Utils.bc("Not everyone has selected a kit yet!");
					Utils.bc("Player that need to select a kit: ");
					Utils.bc(notSelected.toString());
					//TODO: Restart the timer
				}else{
					actuallyStart();
				}
				
				
			}}, 5 * 20L);
	}
	
	public void updateBoardAll(){
		List<Integer> scores = new ArrayList<Integer>();
		Map<GamePlayer<GotchaPlayer>, Integer> mappedScores = new HashMap<>();
		List<String> finals = new ArrayList<String>();
		
		for (Player p : Bukkit.getOnlinePlayers()){
			GamePlayer<GotchaPlayer> player = getApi().getGamePlayer(p.getUniqueId());
			scores.add(player.getType().getScore());
			mappedScores.put(player, player.getType().getScore());
			p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(Utils.AS("&aMM! " + getTimeLeft()));
		}
		
		for (Player p : Bukkit.getOnlinePlayers()){
			for (GamePlayer<GotchaPlayer> gg : mappedScores.keySet()){
				p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(gg.getName()).setScore(gg.getType().getScore());	
			}
		}
		
	}
	
	
	public void actuallyStart(){
		
		for(String s : currentPlayers){
			Bukkit.getPlayer(s).teleport(getLocation(locationType.SPAWN));
		}
		setRound(1);
		Utils.bc("The game has started!");

		APIScheduler.REPEAT.start(getApi(), "MM_TIMER", 20L, new Runnable(){
			public void run(){
			
				if(getSecondsLeft() == 0){
					setSecondsLeft(105);
					setRound(getRound() + 1);
					
					Utils.bc("Round " + round + " has started!");
					
					if(getRound() == 4){
						for(GamePlayer<?> players : getApi().getPlayers().values()){
							MMPlayer mmp = players.getType();
							
						}
					}
				}else if(getSecondsLeft() <= 5){
					Utils.bc(getSecondsLeft() + " seconds left untill round " + (getRound() + 1) + " starts!");
				}
				
				
				if(currentPlayers.size() != 1 ){
					if(getSecondsLeft() == 105){
						spawnRandomMobs();
					}
				}else{
					Utils.bc("We have a winner!&6 " + currentPlayers.get(0) + " &bwon MobMondays!");
					startingPlayers.clear();
					scores.clear();
					currentPlayers.clear();
					
					APIScheduler.DELAY.start(getApi(), "CLOSE_SERVER", 100L, new Runnable(){
						public void run(){
							getApi().sendAllToServer("GameServer");
							APIScheduler.REPEAT.stop("MM_TIMER");
							shutdown();
						}
					});
				}
				

				
				
				secondsLeft = secondsLeft - 1;
				
			}
		});
//		
//		timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){ public void run(){
//			
//			
//			
//		}}, 0L, 20L);
//		
	}
	
	public String getDescription(String name){
		if(description.containsKey(name)){
			return description.get(name);
		}else{
			return name;
		}
	}

	public boolean isInventoryEmpty(Player p){
		for(ItemStack i : p.getInventory().getContents()){
			if(i != null && !i.getType().equals(Material.AIR)){
				return false;
			}
		}
		for(ItemStack i : p.getInventory().getArmorContents()){
			if(i != null && !i.getType().equals(Material.AIR)){
				return false;
			}
		}
		return true;
	}

}*/