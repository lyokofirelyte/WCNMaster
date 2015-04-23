package com.github.lyokofirelyte.Empyreal.Elysian;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;

public class DivinityPlayer extends DivinityStorageModule implements GamePlayer<DivinityPlayer> {

	public DivinityPlayer(UUID n, Empyreal api) {
		super(n, api, "users");
		UUID = n;
	}
	
	@Getter
	private UUID UUID;
	
	@Getter // don't do it!
	private Player player;
	// java.lang.nullfuckthisshitexception
	
	@Override
	public List<String> getPerms(){
		return getList(DPI.PERMS);
	}
	
	@Getter
	private DivinityPlayer type = this;
	
	@Getter @Setter
	private String toServer = "none";
	
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
	
	@Override
	public void save(){
		api.getInstance(EmpyrealSQL.class).getType().saveMapToDatabase("users", this);
	}
	
	public void transfer(){
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("../wa/Divinity/users/" + uuid.toString() + ".yml"));
		
		for (String key : yaml.getKeys(false)){
			set(key, yaml.get(key));
		}
		
		save();
	}
}