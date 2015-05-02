package com.github.lyokofirelyte.Empyreal.Elysian;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Database.DRF;
import com.github.lyokofirelyte.Empyreal.Database.DRI;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;

public class DivinityRegion extends DivinityStorageModule {
	
	public DivinityRegion(String n, Empyreal i) {
		super(n, i, "regions");
	}
	
	public void quickSet(boolean enable, DRF... flags){
		for (DRF flag : flags){
			set(flag, enable);
		}
	}

	public int getPriority(){
		return getInt(DRI.PRIORITY);
	}
	
	public int getLength(){
		return getInt(DRI.LENGTH);
	}
	
	public int getWidth(){
		return getInt(DRI.WIDTH);
	}
	
	public int getHeight(){
		return getInt(DRI.HEIGHT);
	}
	
	public int getArea(){
		return getInt(DRI.AREA);
	}
	
	public String getMaxBlock(){
		return getStr(DRI.MAX_BLOCK);
	}
	
	public String getMinBlock(){
		return getStr(DRI.MIN_BLOCK);
	}
	
	public boolean isDisabled(){
		return getBool(DRI.DISABLED);
	}
	
	public boolean getFlag(DRF flag){
		return getBool(flag);
	}
	
	public Map<DRF, Boolean> getFlags(){
		Map<DRF, Boolean> flagMap = new HashMap<>();
		for (DRF f : DRF.values()){
			if (containsKey(f.toString())){
				flagMap.put(f, getBool(f));
			}
		}
		return flagMap;
	}
	
	public boolean canBuild(org.bukkit.entity.Player p){
		
		DivinityPlayer dp = api.getDivPlayer(p);
		
		for (String perm : (List<String>)getList(DRI.PERMS)){
			if (dp.getList(DPI.PERMS).contains(perm)){
				return true;
			}
		}
		
		return getBool(DRI.DISABLED) ? true : false;
	}

	public World world() {
		return Bukkit.getWorld(getStr(DRI.WORLD));
	}

	public String getWorld() {
		return getStr(DRI.WORLD);
	}

	public List<String> getPerms() {
		return getList(DRI.PERMS);
	}
	
	public void transfer(){
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("../wa/Divinity/regions/" + getName() + ".yml"));
		
		for (String key : yaml.getKeys(false)){
			set(key, yaml.get(key));
		}
		
		save();
	}
}