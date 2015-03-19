package com.github.lyokofirelyte.Platform.Data;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Platform.Platform;
import com.github.lyokofirelyte.Platform.Rounds.PlatformRound;
import com.github.lyokofirelyte.Platform.Rounds.Round;

public class PlatformData implements AutoRegister<PlatformData> {

	public Platform main;
	
	@Getter
	private PlatformData type = this;
	
	public PlatformData(Platform i){
		main = i;
	}
	
	public Map<Integer, PlatformRound> rounds = new HashMap<Integer, PlatformRound>();
	public Map<String, Long> delays = new HashMap<String, Long>();
	public Map<String, Integer> ids = new HashMap<String, Integer>();
	public boolean arenaSet = true;
	
	public Map<Integer, PlatformRound> getRounds(){
		return rounds;
	}
	
	public PlatformRound getRound(Round a){
		if (!a.toString().equals("0")){
			rounds.get(0).start();
		}
		return rounds.get(Integer.parseInt(a.toString()));
	}
	
	public Long getDelay(String a){
		return delays.get(a);
	}
	
	public Integer getId(String a){
		return ids.get(a);
	}
	
	public boolean isArenaSet(){
		return arenaSet;
	}
	
	public void setDelay(String a, long b){
		delays.put(a, b);
	}
	
	public void setId(String a, int b){
		ids.put(a, b);
	}
}