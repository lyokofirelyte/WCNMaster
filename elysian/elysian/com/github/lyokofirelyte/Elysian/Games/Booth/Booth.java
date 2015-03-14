/*package com.github.lyokofirelyte.Elysian.Games.Booth;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;
import com.github.lyokofirelyte.Spectral.Identifiers.DivGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class Booth implements AutoSave, AutoRegister, DivGame{

	public Elysian main;
	private BoothCommand cmd;
	
	public Booth(Elysian i){
		main = i;
		cmd = new BoothCommand(this);
	}

	
	public Location locFromConfig(String path){
		String[] l = toDivGame().getString(path).split(" ");
		return new Location(Bukkit.getWorld(l[0]), Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[3]), Float.parseFloat(l[4]), Float.parseFloat(l[5]));
	}
	
	public boolean isActive(Player p){
		if(!toDivGame().getBoolean("active")){
			DivinityPlayer dp = main.api.getDivPlayer(p);
			dp.s("There is no build-challenge at the moment!");
			return false;
		}else{
			return true;
		}
	}
	
	
	public Object[] registerSubClasses() {
		return new Object[]{cmd};
	}

	public DivinityGame toDivGame(){
		return main.api.getDivGame("booth", "booth");
	}

	@Override
	public void save() {}

	@Override
	public void load() {}
	
}
*/
package com.github.lyokofirelyte.Elysian.Games.Booth;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;
import com.github.lyokofirelyte.Spectral.Identifiers.DivGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class Booth implements AutoSave, AutoRegister, DivGame{

	public Elysian main;
	private BoothCommand cmd;
	private BoothEvent event;
	DivinityGame dg;
	
	public Booth(Elysian i){
		main = i;
		cmd = new BoothCommand(this);
		event = new BoothEvent(this);
		dg = toDivGame();
	}

	
	public Location locFromConfig(String path){
		String[] l = toDivGame().getString(path).split(" ");
		return new Location(Bukkit.getWorld(l[0]), Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[3]), Float.parseFloat(l[4]), Float.parseFloat(l[5]));
	}
	
	public boolean isActive(Player p){
		if(!toDivGame().getBoolean("active")){
			DivinityPlayer dp = main.api.getDivPlayer(p);
			dp.s("There is no build-challenge at the moment!");
			return false;
		}else{
			return true;
		}
	}
	
	public boolean isInBooth(Location l, String arena){
		for(String arenaName : dg.getConfigurationSection("Booths.").getKeys(false)){
			System.out.println("Arena: " + arenaName);
			for(String booth : dg.getConfigurationSection("Booths." + arenaName).getKeys(false)){
				System.out.println("Booth: " + booth);
				String[] loc1 = dg.getString("Booths." + arenaName + "." + booth + "MAX_BLOCK").split(" ");
				String[] loc2 = dg.getString("Booths." + arenaName + "." + booth + "MIN_BLOCK").split(" ");
				Location max = new Location(Bukkit.getWorld(loc1[0]), Integer.parseInt(loc1[1]), Integer.parseInt(loc1[2]), Integer.parseInt(loc1[3]));
				Location min = new Location(Bukkit.getWorld(loc2[0]), Integer.parseInt(loc2[1]), Integer.parseInt(loc2[2]), Integer.parseInt(loc2[3]));
				if(isBetween(min, max, l)){
					return true;
				}
			}
		}
		
		return false;
	}
		
	public String getArena(Location l){
		for(String arenaName : dg.getConfigurationSection("Booths.").getKeys(false)){
			System.out.println("Arena: " + arenaName);
			for(String booth : dg.getConfigurationSection("Booths." + arenaName).getKeys(false)){
				System.out.println("Booth: " + booth);
				String[] loc1 = dg.getString("Booths." + arenaName + "." + booth + "MAX_BLOCK").split(" ");
				String[] loc2 = dg.getString("Booths." + arenaName + "." + booth + "MIN_BLOCK").split(" ");
				Location max = new Location(Bukkit.getWorld(loc1[0]), Integer.parseInt(loc1[1]), Integer.parseInt(loc1[2]), Integer.parseInt(loc1[3]));
				Location min = new Location(Bukkit.getWorld(loc2[0]), Integer.parseInt(loc2[1]), Integer.parseInt(loc2[2]), Integer.parseInt(loc2[3]));
				if(isBetween(min, max, l)){
					return arenaName;
				}
			}
		}
		return null;
	}
	
	public String get(Player p){
		
		
		
		return null;
	}
	
	public boolean isBetween(int first, int second, int check){
		return first < second ? check >= first && check <= second : check <= first && check >= second;
	}
	
	public boolean isBetween(Location first, Location second, Location check){
		if(isBetween(first.getBlockX(), second.getBlockX(), check.getBlockX())){
			if(isBetween(first.getBlockY(), second.getBlockY(), check.getBlockY())){
				if(isBetween(first.getBlockZ(), second.getBlockZ(), check.getBlockZ())){
					return true;
				}
			}
		}
		return false;
	}
	
	//-700
	//700
	//-600
	//600

	
	public String getCurrentArena(){
		return toDivGame().getString("Booths.CURRENT_ACTIVE");
	}
	
	public Object[] registerSubClasses() {
		return new Object[]{cmd, event};
	}

	public DivinityGame toDivGame(){
		return main.api.getDivGame("booth", "booth");
	}

	@Override
	public void save() {}

	@Override
	public void load() {}
	
}