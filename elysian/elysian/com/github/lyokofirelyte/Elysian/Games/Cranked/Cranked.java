package com.github.lyokofirelyte.Elysian.Games.Cranked;

import java.util.ArrayList;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;
import com.github.lyokofirelyte.Spectral.Identifiers.DivGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;

public class Cranked implements AutoSave, AutoRegister, DivGame {
	
	protected Elysian main;
	public CrankedCommand command;
	public CrankedActive active;
	public boolean isStarted = false;
	public ArrayList<String> players = new ArrayList<String>();
	public THashMap<String, Integer> kills = new THashMap<String, Integer>();
	public String currentGame;
	
	public Cranked(Elysian i){
		main = i;
		command = new CrankedCommand(this);
		active = new CrankedActive(this);
	}

	public DivinityGame toDivGame() {
		return main.api.getDivGame("cranked", "cranked");
	}

	@Override
	public void save(){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(){
		// TODO Auto-generated method stub
		
	}

	public boolean isPlaying(Player p){
		if(players.contains(p.getName())){
			return true;
		}
		return false;
	}
	
	public Location getRandomLocation(){
		for(String s : this.toDivGame().getStringList("Arenas." + currentGame + ".locations")){
			
		}
		return null;
	}
	
	public Object[] registerSubClasses(){
		return new Object[]{
			command,
			active
		};
	}
}
