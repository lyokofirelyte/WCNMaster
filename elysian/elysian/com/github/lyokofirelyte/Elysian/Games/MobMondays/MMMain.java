package com.github.lyokofirelyte.Elysian.Games.MobMondays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;
import com.github.lyokofirelyte.Spectral.Identifiers.DivGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class MMMain implements AutoSave, AutoRegister, DivGame{
	public Elysian main;
	MMCommands mmcmd;
	MMEvents mmevents;
	
	public List<String> classes = new ArrayList<String>(Arrays.asList("mage", "melee", "ranger", "pyro", "barbarian", "healer"));
	public List<String> players = new ArrayList<String>();
	public Map<String, String> selected = new HashMap<String, String>();
	public enum locationType {DEATH, SPAWN, RANDOMMOB}; 
	
	int secondsLeft = 180;
	int timer;
	int round = 1;
	
	String current = "";
	String split = "%SPLIT%";
	
	public boolean active = false;
	public boolean allowedToJoin = false;
	
	public MMMain(Elysian i){
		main = i;
		mmcmd = new MMCommands(this);
		mmevents = new MMEvents(this);
	}
	
	public String locationToString(Location loc){
		return loc.getWorld().getName() + split + loc.getX() + split + loc.getY() + split + loc.getZ();
	}
	
	public void spawnRandomMobs(){
		
		List<EntityType> mobs = new ArrayList<EntityType>(Arrays.asList(EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.ZOMBIE, EntityType.MAGMA_CUBE, EntityType.WITCH, EntityType.SILVERFISH, EntityType.SPIDER, EntityType.SLIME, EntityType.SKELETON));
		List<EntityType> bosses = new ArrayList<EntityType>(Arrays.asList(EntityType.GIANT, EntityType.ENDERMAN, EntityType.BLAZE));
		for(int i = 0; i < toDivGame().getStringList("arenas." + current + ".monsterspawn").size(); i++){
			Location loc = getLocation(locationType.RANDOMMOB);
			
			for(int j = 0; j < players.size() * 3 + round + 4; j++){
				EntityType random = mobs.get(new Random().nextInt(mobs.size()));

				if(random.equals(EntityType.SKELETON)){
					Skeleton s = (Skeleton) loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
					s.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					s.getEquipment().setItemInHand(new ItemStack(Material.BOW));
				}else{
					Entity ent = loc.getWorld().spawnCreature(loc, random);
					LivingEntity livingent = (LivingEntity) ent;
					livingent.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
				}
			
			}
			
			if(players.size() <= 2){
				loc.getWorld().spawnCreature(loc, bosses.get(new Random().nextInt(bosses.size())));
			}
			
		}
		

	}
	
	public Location getLocation(locationType type){
		Location loc = null;
		String[] location = null;
		
		switch(type){
		
		case DEATH:
			
			location = toDivGame().getString("arenas." + current + ".death").split(split);
			loc = new Location(Bukkit.getWorld(location[0]), Float.parseFloat(location[1]), Float.parseFloat(location[2]), Float.parseFloat(location[3]));
			
			break;
			
		case SPAWN:
			
			
			location = toDivGame().getString("arenas." + current + ".spawn").split(split);
			loc = new Location(Bukkit.getWorld(location[0]), Float.parseFloat(location[1]), Float.parseFloat(location[2]), Float.parseFloat(location[3]));
			
			break;
			
		case RANDOMMOB:
			
			List<String> locations = toDivGame().getStringList("arenas." + current + ".monsterspawn");
			location = locations.get(new Random().nextInt(locations.size())).split(split);
			loc = new Location(Bukkit.getWorld(location[0]), Float.parseFloat(location[1]), Float.parseFloat(location[2]), Float.parseFloat(location[3]));
			
			break;
			
		}
		
		return loc;
	}
	
	
	public void start(String arena){
			current = arena;
			
			msg("The game is starting in 10 seconds!");
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){
				
				actuallyStart();
				
			}}, 200L);
	}
	
	public void actuallyStart(){
		
		for(String s : players){
			Bukkit.getPlayer(s).teleport(getLocation(locationType.SPAWN));
		}
		
		msg("The game has started!");
		
		
		timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){ public void run(){
			
			if(secondsLeft == 0){
				secondsLeft = 180;
				round = round + 1;
				main.divinity.api.divUtils.bc("Round " + round + " has started!");
				
				if(round == 4){
					for(String s : players){
						Player temp = Bukkit.getPlayer(s);
						temp.getInventory().addItem(new ItemStack(Material.POTION, 8, (short)8193));
						switch(selected.get(s)){
						case "mage":
							temp.getInventory().addItem(new ItemStack(Material.COOKED_CHICKEN, 16));
							break;
							
						case "pyro":
							temp.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 16));
							break;
							
						case "barbarian":
							temp.getInventory().addItem(new ItemStack(Material.COOKED_CHICKEN, 16));
							break;
							
						case "melee":
							temp.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
							break;
							
						case "healer":
							temp.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 4));
							break;
							
						case "ranger":
							temp.getInventory().addItem(new ItemStack(Material.COOKED_MUTTON, 16));
							break;

						}
						
					}
				}
			}
			
			
			System.out.println(players.size());
			if(players.size() != 1 ){
				if(secondsLeft == 180){
					spawnRandomMobs();
				}
			}else{
				main.divinity.api.divUtils.bc("We have a winner!&6 " + players.get(0) + " &bwon MobMondays!");
				main.divinity.api.divUtils.bc("Please leave the arena by going to spawn or some other place :)");
				Bukkit.getScheduler().cancelTask(timer);
				active = false;
				for(String s : players){
					Bukkit.getPlayer(s).getActivePotionEffects().clear();
				}
			}
			

			
			
			secondsLeft = secondsLeft - 1;
			
		}}, 0L, 20L);
//		Bukkit.getScheduler().cancelTask(timer);
		
	}
	
	public void msg(String message){
		for(String s : players){
			if(main.api.doesPartialPlayerExist(s)){
				DivinityPlayer dp = main.api.getDivPlayer(s);
				dp.s(message);
			}
		}
	}
	
	@Override
	public Object[] registerSubClasses() {
		return new Object[]{mmcmd, mmevents};
	}

	@Override
	public DivinityGame toDivGame() {
		return main.api.getDivGame("mobmondays", "mobmondays");
	}

	@Override
	public void save() {}

	@Override
	public void load() {}

}
