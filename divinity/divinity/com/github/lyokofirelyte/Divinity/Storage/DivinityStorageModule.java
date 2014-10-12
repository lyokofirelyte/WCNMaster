package com.github.lyokofirelyte.Divinity.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Divinity.Divinity;
import com.github.lyokofirelyte.Divinity.Events.PlayerMoneyChangeEvent;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityStorage;

public class DivinityStorageModule extends YamlConfiguration implements DivinityStorage {

	public API api;
	public UUID uuid;
	
	private String name;
	private String gameName;
	private boolean isGame = false;
	
	public DivinityStorageModule(UUID u, API i){
		api = i;
		uuid = u;
		name = Bukkit.getPlayer(u) != null ? Bukkit.getPlayer(u).getName() : Bukkit.getOfflinePlayer(u).getName();
	}

	public DivinityStorageModule(String n, API i){
		api = i;
		name = n;
	}
	
	public DivinityStorageModule(String gameType, String n, API i){
		gameName = gameType;
		name = n;
		api = i;
		isGame = true;
	}
	
	public String name(){
		return name;
	}
	
	public UUID uuid(){
		return uuid;
	}
	
	public API api(){
		return api;
	}
	
	public boolean isGame(){
		return isGame;
	}
	
	public String gameName(){
		return isGame ? gameName : "none";
	}

	public Object getRawInfo(Enum<?> i){
		return contains(i.toString()) ? get(i.toString()) : null;
	}
	
	public String getStr(Enum<?> i){

		if (contains(i.toString())){
			if (get(i.toString()) instanceof String){
				return (String) get(i.toString());
			}
			return get(i.toString()) + "";
		}
		return "none";
	}
	
	public int getInt(Enum<?> i){

		if (contains(i.toString())){
			if (get(i.toString()) instanceof Integer){
				return (Integer) get(i.toString());
			}
			return api.divUtils.isInteger(get(i.toString()) + "") ? Integer.parseInt(get(i.toString()) + "") : 0;
		}
		return 0;
	}
	
	public long getLong(Enum<?> i){

		if (contains(i.toString())){
			if (get(i.toString()) instanceof Long){
				return (Long) get(i.toString());
			}
			try {
				return Long.parseLong(get(i.toString()) + "");
			} catch (Exception e){
				return 0L;
			}
		}
		return 0L;
	}
	
	public byte getByte(Enum<?> i){

		if (contains(i.toString())){
			if (get(i.toString()) instanceof Byte){
				return (Byte) get(i.toString());
			}
			try {
				return Byte.parseByte(get(i.toString()) + "");
			} catch (Exception e){
				return 0;
			}
		}
		return 0;
	}
	
	public double getDouble(Enum<?> i){

		if (contains(i.toString())){
			if (get(i.toString()) instanceof Double){
				return (Double) get(i.toString());
			}
			try {
				return Double.parseDouble(get(i.toString()) + "");
			} catch (Exception e){
				return 0D;
			}
		}
		return 0D;
	}
	
	public boolean getBool(Enum<?> i){

		if (contains(i.toString())){
			if (get(i.toString()) instanceof Boolean){
				return (Boolean) get(i.toString());
			}
			try {
				return Boolean.valueOf(get(i.toString()) + "");
			} catch (Exception e){
				return false;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<String> getList(Enum<?> i){
		
		if (contains(i.toString())){
			try {
				if (get(i.toString()) instanceof List){
					return (List<String>) get(i.toString());
				}
				set(i.toString(), new ArrayList<String>());
			} catch (Exception e){
				set(i.toString(), new ArrayList<String>());
			}
		} else {
			set(i.toString(), new ArrayList<String>());
		}
		
		return (List<String>) get(i.toString());
	}
	
	public Location getLoc(Enum<?> i){
		
		if (contains(i.toString())){
			if (get(i.toString()) instanceof Location){
				return (Location) get(i.toString());
			}
			try {
				String[] loc = get(i.toString()).toString().split(" ");
				if (loc.length == 4){
					return new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
				} else if (loc.length == 6){
					return new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
				}
			} catch (Exception e){
				return new Location(Bukkit.getWorld("world"), 0, 0, 0);
			}
		}
		
		return new Location(Bukkit.getWorld("world"), 0, 0, 0);
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemStack> getStack(Enum<?> i){
		
		if (contains(i.toString())){
			if (get(i.toString()) instanceof List){
				return (List<ItemStack>) get(i.toString());
			}
		}

		set(i.toString(), new ArrayList<ItemStack>());
		return (List<ItemStack>) get(i.toString());
	}
	
	public void set(Enum<?> i, Object infos){
		
		if (i.equals(DPI.BALANCE) && infos instanceof Integer){
			PlayerMoneyChangeEvent e = new PlayerMoneyChangeEvent(Bukkit.getPlayer(uuid()), getInt(DPI.BALANCE), (Integer) infos);
			api.event(e);
			if (e.isCancelled()){
				return;
			}
		}
		
		if (infos instanceof Location){
			Location l = (Location) infos;
			set(i.toString(), l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
		} else {
			set(i.toString(), infos);
		}
	}
	
	public DivinityAllianceModule toAlliance(){
		return (DivinityAllianceModule) this;
	}
}