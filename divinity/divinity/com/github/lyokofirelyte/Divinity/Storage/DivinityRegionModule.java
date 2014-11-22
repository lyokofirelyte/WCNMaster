package com.github.lyokofirelyte.Divinity.Storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.DRF;
import com.github.lyokofirelyte.Spectral.DataTypes.DRI;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRegion;

public class DivinityRegionModule extends DivinityStorageModule implements DivinityRegion {
	
	public DivinityRegionModule(String n, API i) {
		super(n, i);
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
			if (contains(f.toString())){
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
}