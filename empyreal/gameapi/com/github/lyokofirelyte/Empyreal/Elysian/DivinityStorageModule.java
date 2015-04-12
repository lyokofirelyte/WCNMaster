package com.github.lyokofirelyte.Empyreal.Elysian;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.JSONMap;

public abstract class DivinityStorageModule extends JSONMap<String, Object> {

	private static final long serialVersionUID = -4735951550321821204L;
	
	@Getter @Setter
	public Empyreal api;
	
	@Getter @Setter
	public UUID uuid;
	
	@Getter @Setter
	private String name;
	
	@Getter @Setter
	private String table;
	
	public DivinityStorageModule(UUID u, Empyreal i, String table){
		api = i;
		uuid = u;
		name = Bukkit.getPlayer(u) != null ? Bukkit.getPlayer(u).getName() : Bukkit.getOfflinePlayer(u).getName();
		setTable(table);
	}

	public DivinityStorageModule(String n, Empyreal i, String table){
		api = i;
		name = n;
		if (Bukkit.getPlayer(n) != null){
			uuid = Bukkit.getPlayer(n).getUniqueId();
		}
		setTable(table);
	}
	
	public void fill(JSONMap<String, Object> obj){
		for (Object thing : obj.keySet()){
			set(thing, obj.get(thing));
		}
	}
	
	public abstract void save();
}