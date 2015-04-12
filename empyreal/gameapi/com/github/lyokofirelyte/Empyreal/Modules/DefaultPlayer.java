package com.github.lyokofirelyte.Empyreal.Modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class DefaultPlayer extends HashMap<String, Object> {

	public Empyreal api;
	
	@Getter
	public UUID UUID;
	
	@Getter @Setter
	private String name;
	
	@Getter
	private Player player;
	
	@Getter
	private List<String> perms = new ArrayList<String>();
	
	public DefaultPlayer(UUID u, Empyreal i){
		api = i;
		UUID = u;
		name = Bukkit.getPlayer(u) != null ? Bukkit.getPlayer(u).getName() : Bukkit.getOfflinePlayer(u).getName();
		player = Bukkit.getPlayer(u) != null ? Bukkit.getPlayer(u) : null;
	}

	public DefaultPlayer(String name, Empyreal i){
		api = i;
		setName(name);
	}
	
	public void s(String msg){}
	
	public String name(){
		return name;
	}
	
	public UUID uuid(){
		return UUID;
	}
	
	public Empyreal api(){
		return api;
	}

	public Object getRawInfo(Object i){
		return containsKey(toString(i)) ? get(toString(i)) : null;
	}
	
	public String getStr(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof String){
				return (String) get(toString(i));
			}
			return get(toString(i)) + "";
		}
		return "none";
	}
	
	public int getInt(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Integer){
				return (Integer) get(toString(i));
			}
			return Utils.isInteger(get(toString(i)) + "") ? Integer.parseInt(get(toString(i)) + "") : 0;
		}
		return 0;
	}
	
	public long getLong(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Long){
				return (Long) get(toString(i));
			}
			try {
				return Long.parseLong(get(toString(i)) + "");
			} catch (Exception e){
				return 0L;
			}
		}
		return 0L;
	}
	
	public byte getByte(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Byte){
				return (Byte) get(toString(i));
			}
			try {
				return Byte.parseByte(get(toString(i)) + "");
			} catch (Exception e){
				return 0;
			}
		}
		return 0;
	}
	
	public double getDouble(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Double){
				return (Double) get(toString(i));
			}
			try {
				return Double.parseDouble(get(toString(i)) + "");
			} catch (Exception e){
				return 0D;
			}
		}
		return 0D;
	}
	
	public boolean getBool(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Boolean){
				return (Boolean) get(toString(i));
			}
			try {
				return Boolean.valueOf(get(toString(i)) + "");
			} catch (Exception e){
				return false;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<String> getList(Object i){
		
		if (containsKey(toString(i))){
			try {
				if (get(toString(i)) instanceof List){
					return (List<String>) get(toString(i));
				}
				set(toString(i), new ArrayList<String>());
			} catch (Exception e){
				set(toString(i), new ArrayList<String>());
			}
		} else {
			set(toString(i), new ArrayList<String>());
		}
		
		return (List<String>) get(toString(i));
	}
	
	public Location getLoc(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Location){
				return (Location) get(toString(i));
			}
			try {
				String[] loc = get(toString(i)).toString().split(" ");
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
	public List<ItemStack> getStack(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof List){
				return (List<ItemStack>) get(toString(i));
			}
		}

		set(toString(i), new ArrayList<ItemStack>());
		return (List<ItemStack>) get(toString(i));
	}
	
	public void set(String i, Object infos){
		if (infos instanceof Location){
			Location l = (Location) infos;
			set(toString(i), l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
		} else {
			set(toString(i), infos);
		}
	}
	
	public void set(Object i, Object infos){
		set(toString(i), infos);
	}
	
	public String toString(Object s){
		
		if (s instanceof String){
			return s + "";
		}
		
		return s.toString();
	}
}