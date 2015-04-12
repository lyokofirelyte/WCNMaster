/*package com.github.lyokofirelyte.Elysian.Games.Cranked;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityGame;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class Cranked implements AutoRegister<Cranked> {
	
	@Getter
	private Cranked type = this;
	
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
}*/