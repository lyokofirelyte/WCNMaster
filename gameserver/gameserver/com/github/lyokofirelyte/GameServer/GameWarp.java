package com.github.lyokofirelyte.GameServer;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Saveable;

public class GameWarp {
	
	@Getter @Setter @Saveable
	protected String name;
	
	@Getter @Setter @Saveable
	protected String world;
	
	@Getter @Setter @Saveable
	protected int X;
	
	@Getter @Setter @Saveable
	protected int Y;
	
	@Getter @Setter @Saveable
	protected int Z;
	
	@Getter @Setter @Saveable
	protected float yaw;
	
	@Getter @Setter @Saveable
	protected float pitch;
	
	public GameWarp(GameServer main, String name, int x, int y, int z, float yaw, float pitch, String world){
		setName(name);
		setX(x);
		setY(y);
		setZ(z);
		setWorld(world);
		setYaw(yaw);
		setPitch(pitch);
		save();
	}
	
	public GameWarp(File f){
		load(f);
	}
	
	public void teleport(Player p){
		p.teleport(new Location(Bukkit.getWorld(world), X, Y, Z, yaw, pitch));
	}
	
	@SneakyThrows
	public void load(File file){
		
		if (file.exists()){
			JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(file.getPath()));
			setName((String) obj.get("NAME"));
			setWorld((String) obj.get("WORLD"));
			setX(Integer.parseInt(obj.get("X") + ""));
			setY(Integer.parseInt(obj.get("Y") + ""));
			setZ(Integer.parseInt(obj.get("Z") + ""));
			setYaw(Float.parseFloat(obj.get("YAW") + ""));
			setPitch(Float.parseFloat(obj.get("PITCH") + ""));
		}
	}
	
	@SneakyThrows
	public void save(){
		
		File file = new File("./plugins/GameServer/warps/");
		file.mkdirs();
		
		JSONObject obj = new JSONObject();
		
		for (Field f : getClass().getDeclaredFields()){
			try {
				f.setAccessible(true);
				if (f.getAnnotation(Saveable.class) != null){
					obj.put(f.getName().toUpperCase(), f.get(this));
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		Empyreal.saveJSON("./plugins/GameServer/warps/" + getFullName() + ".json", obj);
	}
	
	public void delete(){
		new File("./plugins/GameServer/warps/" + getFullName() + ".json").delete();
	}
	
	public String getFullName(){
		return world + "," + X + "," + Y + "," + Z;
	}
}