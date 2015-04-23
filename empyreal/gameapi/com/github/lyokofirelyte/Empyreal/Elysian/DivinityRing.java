package com.github.lyokofirelyte.Empyreal.Elysian;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.DRS;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;

public class DivinityRing extends DivinityStorageModule {
	
	public DivinityRing(String n, Empyreal api) {
		super(n, api, "rings");
	}

	private List<org.bukkit.entity.Player> players;
	private boolean inOperation = false;
	
	public String[] getCenter(){
		return getStr(DRS.CENTER).split(" ");
	}
	
	public String getDest(){
		return getStr(DRS.DEST);
	}
	
	public Location getCenterLoc(){
		return new Location(Bukkit.getWorld(getCenter()[0]), Double.parseDouble(getCenter()[1]), Double.parseDouble(getCenter()[2]), Double.parseDouble(getCenter()[3]), Float.parseFloat(getCenter()[4]), Float.parseFloat(getCenter()[5]));
	}
	
	public boolean isInOperation(){
		return inOperation;
	}
	
	public boolean isAllianceOwned(){
		return getBool(DRS.IS_ALLIANCE_OWNED);
	}

	public int getMatId(){
		return getInt(DRS.MAT_ID);
	}
	
	public byte getMatByte(){
		return getByte(DRS.BYTE_ID);
	}
	
	public List<org.bukkit.entity.Player> getPlayers(){
		return players;
	}
	
	public void addPlayer(org.bukkit.entity.Player name){
		if (!players.contains(name)){
			players.add(name);
		}
	}
	
	public void remPlayer(org.bukkit.entity.Player name){
		if (players.contains(name)){
			players.remove(name);
		}
	}
	
	public void setInOperation(boolean b){
		inOperation = b;
	}
	
	public void save(){
		api.getInstance(EmpyrealSQL.class).getType().saveMapToDatabase("rings", this);
	}
	
	public void transfer(){
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("../wa/Divinity/rings/" + getName() + ".yml"));
		
		for (String key : yaml.getKeys(false)){
			set(key, yaml.get(key));
		}
		
		save();
	}
}