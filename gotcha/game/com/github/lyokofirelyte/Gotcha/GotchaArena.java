package com.github.lyokofirelyte.Gotcha;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.JSONMap;
import com.github.lyokofirelyte.Empyreal.Saveable;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class GotchaArena {
	
	@Getter @Setter @Saveable
	private String name;
	
	@Getter @Setter @Saveable
	private String lobby;
	
	@Getter @Setter @Saveable
	private List<String> spawnPoints = new ArrayList<String>();
	
	@Getter
	private String folderPath = "./plugins/Gotcha/arenas/";
	
	public GotchaArena(String name){
		setName(name);
	}
	
	public Location toLocation(String val){
		
		String[] valSplit = val.split(" ");
		
		if (valSplit.length ==  4){
			return new Location(Bukkit.getWorld(valSplit[0]), Double.parseDouble(valSplit[1]), Double.parseDouble(valSplit[2]), Double.parseDouble(valSplit[3]));
		}
		
		return new Location(Bukkit.getWorld(valSplit[0]), Double.parseDouble(valSplit[1]), Double.parseDouble(valSplit[2]), Double.parseDouble(valSplit[3]), Float.parseFloat(valSplit[4]), Float.parseFloat(valSplit[5]));
	}
	
	public void delete(){
		new File(folderPath + getName() + ".json").delete();
	}
	
	@SneakyThrows
	public void load(){
		
		JSONMap<Object, Object> map = Empyreal.loadJSON(folderPath + getName() + ".json");
		
		for (Field f : getClass().getDeclaredFields()){
			if (f.getAnnotation(Saveable.class) != null){
				f.set(this, map.get(f.getName().toUpperCase()));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	public void save(){
		
		File file = new File(folderPath);
		file.mkdirs();
		
		JSONObject obj = new JSONObject();
		
		for (Field f : getClass().getDeclaredFields()){
			if (f.getAnnotation(Saveable.class) != null){
				obj.put(f.getName().toUpperCase(), f.get(this));
			}
		}
		
		Empyreal.saveJSON("./plugins/Gotcha/areans/" + getName() + ".json", obj);
	}
}