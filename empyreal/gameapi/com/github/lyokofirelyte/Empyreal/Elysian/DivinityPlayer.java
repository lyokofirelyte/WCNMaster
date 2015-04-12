package com.github.lyokofirelyte.Empyreal.Elysian;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.api.ElySkill;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Listener.SocketMessageListener.Handler;
import com.github.lyokofirelyte.Empyreal.Listener.SocketObject;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;

public class DivinityPlayer extends DivinityStorageModule {

	public DivinityPlayer(UUID n, Empyreal api) {
		super(n, api, "users");
	}
	
	private List<String> activeEffects = new ArrayList<String>();
		
	public boolean isOnline(){
		return Bukkit.getPlayer(uuid) != null;
	}
		
	public int getLevel(ElySkill skill){
		return Integer.parseInt(getStr(skill).split(" ")[0].replace("none", "0"));
	}
		
	public int getXP(ElySkill skill){
		return Integer.parseInt(getStr(skill).split(" ")[1].replace("none", "0"));
	}
		
	public int getNeededXP(ElySkill skill){
		return Integer.parseInt(getStr(skill).split(" ")[2].replace("none", "0").split("\\.")[0]);
	}
		
	public boolean hasLevel(ElySkill skill, int level){
		return Integer.parseInt(getStr(skill).split(" ")[0].replace("none", "0")) >= level;
	}
	
	public void lockEffect(String name, ParticleEffect eff, int offsetX, int offsetY, int offsetZ, int speed, int amount, int range, long cycleDelay){
		if (!activeEffects.contains(name)){
			activeEffects.add(name);
			api.repeat(this, "playEffect", 0L, cycleDelay, "playerEffects" + name, eff, offsetX, offsetY, offsetZ, speed, amount, range, Bukkit.getPlayer(uuid));
		}
	}
	
	public void playEffect(ParticleEffect eff, int offsetX, int offsetY, int offsetZ, int speed, int amount, int range, Player p){
		try {
			Location l = p.getLocation();
			eff.display(offsetX, offsetY, offsetZ, speed, amount, new Location(l.getWorld(), l.getX(),  l.getY()+1, l.getZ()), range);
		} catch (Exception e){}
	}
	
	public void remEffect(String name){
		if (activeEffects.contains(name)){
			activeEffects.remove(name);
			api.cancelTask("playerEffects" + name);
		}
	}
	
	public void clearEffects(){
		for (String effect : activeEffects){
			api.cancelTask("playerEffects" + effect);
		}
		activeEffects = new ArrayList<String>();
	}
		
	public void s(String message){
		if (isOnline()){
			DivinityUtilsModule.s(Bukkit.getPlayer(uuid), message);
		}
	}
		
	public void err(String message){
		if (isOnline()){
			DivinityUtilsModule.s(Bukkit.getPlayer(uuid), "&c&o" + message);
		}
	}
	
	public void save(){
		if (!api.getServerName().equals("GameServer")){
			SocketObject obj = new SocketObject(this, DivinityStorageModule.class, Handler.SAVE_OBJECT_TO_SQL, api.getServerName());
			api.sendObjectToSocket("GameServer", obj);
		} else {
			api.getInstance(EmpyrealSQL.class).getType().saveMapToDatabase("users", this);
		}
	}
	
	public void transfer(){
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("../wa/Divinity/users/" + uuid.toString() + ".yml"));
		
		for (String key : yaml.getKeys(false)){
			set(key, yaml.get(key));
		}
		
		save();
	}
}