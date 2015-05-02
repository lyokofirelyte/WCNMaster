package com.github.lyokofirelyte.Empyreal.Elysian;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.JSONMap;

public class DivinityStorageModule extends JSONMap<String, Object> {

	private static final long serialVersionUID = -4735951550321821204L;
	
	@Getter @Setter
	public Empyreal api;
	
	@Getter @Setter
	public UUID uuid;
	
	@Setter
	private String name;
	
	@Getter @Setter
	private String table;
	
	public String getName(){
		return getStr("name");
	}
	
	public DivinityStorageModule(UUID u, Empyreal i, String table){
		api = i;
		uuid = u;
		name = Bukkit.getPlayer(u) != null ? Bukkit.getPlayer(u).getName() : Bukkit.getOfflinePlayer(u).getName();
		setTable(table);
		set("uuid", u.toString());
		set("name", name);
		set("table", table);
	}

	public DivinityStorageModule(String n, Empyreal i, String table){
		api = i;
		name = n;
		if (Bukkit.getPlayer(n) != null){
			uuid = Bukkit.getPlayer(n).getUniqueId();
		}
		setTable(table);
		set("name", n);
		set("uuid", uuid != null ? uuid.toString() : n);
		set("table", table);
	}
	
	public void fill(JSONMap<String, Object> obj){
		for (String thing : obj.keySet()){
			set(thing, obj.get(thing));
		}
	}
	
	public void save(){}
}