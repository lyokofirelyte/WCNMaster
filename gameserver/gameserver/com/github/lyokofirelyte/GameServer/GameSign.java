package com.github.lyokofirelyte.GameServer;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
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
	
	public GameSign(GameServer main, String serverName, String state, int x, int y, int z, String world, final Sign sign){
		setServerName(serverName);
		setState(state);
		setX(x);
		setY(y);
		setZ(z);
		setWorld(world);
		
		APIScheduler.DELAY.start(main.getApi(), x + y + z + "", 10L, new Runnable(){
			public void run(){
				sign.setLine(0, Utils.AS("&b" + getServerName()));
				sign.setLine(1, Utils.AS("&f0 Players"));
				sign.setLine(2, Utils.AS("&e&oPre-Lobby"));
				sign.setLine(3, Utils.AS("&a[ JOIN ]"));
				sign.update();
			}
		});
		
		save();
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
			setX(Integer.parseInt(obj.get("X") + ""));
			setY(Integer.parseInt(obj.get("Y") + ""));
			setZ(Integer.parseInt(obj.get("Z") + ""));
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