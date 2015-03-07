package com.github.lyokofirelyte.GameServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Saveable;
import com.github.lyokofirelyte.Empyreal.Utils;

public class GameSign {

	@Getter @Setter @Saveable
	protected String serverName;
	
	@Getter @Setter @Saveable
	protected String state;
	
	@Getter @Setter @Saveable
	protected String world;
	
	@Getter @Setter @Saveable
	protected int players = 0;
	
	@Getter @Setter @Saveable
	protected int maxPlayers = 0;
	
	@Getter @Setter @Saveable
	protected int X;
	
	@Getter @Setter @Saveable
	protected int Y;
	
	@Getter @Setter @Saveable
	protected int Z;
	
	public GameSign(Sign sign){
		setServerName(ChatColor.stripColor(sign.getLine(0)));
		setState(ChatColor.stripColor(sign.getLine(2)));
		setX(sign.getX());
		setY(sign.getY());
		setZ(sign.getZ());
		setWorld(sign.getWorld().getName());
	}
	
	public GameSign(File f){
		load(f);
	}
	
	@SneakyThrows
	public void load(File file){
		
		if (file.exists()){
			JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(file.getPath()));
			setServerName((String) obj.get("SERVERNAME"));
			setState((String) obj.get("STATE"));
			setWorld((String) obj.get("WORLD"));
			setX((int) obj.get("X"));
			setY((int) obj.get("Y"));
			setZ((int) obj.get("Z"));
		}
	}
	
	@SneakyThrows
	public void save(){
		
		File file = new File("./plugins/GameServer/signs/");
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

		Empyreal.saveJSON("./plugins/GameServer/signs/" + getFullName() + ".json", obj);
	}
	
	public void updateLine(int line, String msg){
		Sign sign = (Sign) new Location(Bukkit.getWorld(world), X, Y, Z).getBlock().getState();
		sign.setLine(line, Utils.AS(msg));
		sign.update();
	}
	
	public void delete(){
		new File("./plugins/GameServer/signs/" + getFullName() + ".json").delete();
	}
	
	public String getFullName(){
		return world + "," + X + "," + Y + "," + Z;
	}
}