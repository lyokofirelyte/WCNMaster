package com.github.lyokofirelyte.Elysian.Games.Booth;

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
