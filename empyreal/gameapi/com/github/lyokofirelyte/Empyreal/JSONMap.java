package com.github.lyokofirelyte.Empyreal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JSONMap<K, V> extends HashMap<Object, Object> {
	
	private static final long serialVersionUID = 1L;

	public char getChar(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Character){
				return (Character) get(toString(i));
			}
		}
		
		return 'n';
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
			try {
				return Integer.parseInt(get(toString(i)) + "");
			} catch (Exception e){
				return 0;
			}
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
	
	public float getFloat(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Float){
				return (Float) get(toString(i));
			}
			try {
				return Float.parseFloat(get(toString(i)) + "");
			} catch (Exception e){
				return 0F;
			}
		}
		return 0F;
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
				set(i, new ArrayList<String>());
			} catch (Exception e){
				set(i, new ArrayList<String>());
			}
		} else {
			set(i, new ArrayList<String>());
		}
		
		return (List<String>) get(toString(i));
	}
	
	@SuppressWarnings("unchecked")
	public List<LivingEntity> getLivingList(Object i){
		
		if (containsKey(toString(i))){
			try {
				if (get(toString(i)) instanceof List){
					return (List<LivingEntity>) get(toString(i));
				}
				set(i, new ArrayList<LivingEntity>());
			} catch (Exception e){
				set(i, new ArrayList<LivingEntity>());
			}
		} else {
			set(i, new ArrayList<LivingEntity>());
		}
		
		return (List<LivingEntity>) get(toString(i));
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
	
	public List<ItemStack> getStack(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof List){
				return (List<ItemStack>) get(toString(i));
			}
		}

		set(i, new ArrayList<ItemStack>());
		return (List<ItemStack>) get(toString(i));
	}
	
	public List<Block> getBlockList(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof List){
				return (List<Block>) get(toString(i));
			}
		}
		
		set(i, new ArrayList<Block>());
		return (List<Block>) get(toString(i));
	}
	
	public List<Player> getPlayerList(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof List){
				return (List<Player>) get(toString(i));
			}
		}
		
		set(i, new ArrayList<Player>());
		return (List<Player>) get(toString(i));
	}
	
	public Method getMethod(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Method){
				return (Method) get(toString(i));
			}
			try {
				return getClass().getMethod(get(toString(i)).toString());
			} catch (Exception e){}
		}
		
		return null;
	}
	
	public LivingEntity getLivingEntity(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof LivingEntity){
				return (LivingEntity) get(toString(i));
			}
		}
		
		return null;
	}
	
	public HashMap getTMap(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof HashMap){
				return (HashMap) get(toString(i));
			}
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		return map;
	}
	
	public Map getMap(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Map){
				return (Map) get(toString(i));
			}
		}
		
		Map map = new HashMap<>();
		return map;
	}
	
	public String[] getStrArray(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof String[]){
				return (String[]) get(toString(i));
			}
			return new String[]{get(toString(i)) + ""};
		}
		
		return new String[]{"none"};
	}
	
	public Integer[] getIntArray(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Integer[]){
				return (Integer[]) get(toString(i));
			}
			try {
				return new Integer[]{Integer.parseInt(get(toString(i)) + "")};
			} catch (Exception e){}
		}
		
		return new Integer[]{0};
	}
	
	public Object[] getObjArray(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Object[]){
				return (Object[]) get(toString(i));
			}
			try {
				return new Object[]{(get(toString(i)) + "")};
			} catch (Exception e){}
		}
		
		return new Object[]{"none"};
	}
	
	public String valuesToString(){
		
		String str = "";
		
		for (Object obj : values()){
			str = str.equals("") ? toString(obj) : str + ", " + toString(obj);
		}
		
		return str;
	}
	
	public String valuesToSortedString(){
		
		List<String> str = new ArrayList<String>();
		String newStr = "";
		
		for (Object obj : values()){
			str.add(toString(obj));
		}
		
		Collections.sort(str);
		
		for (String string : str){
			newStr = newStr.equals("") ? string : newStr + ", " + string;
		}
		
		return newStr;
	}
	
	public boolean editValue(Object oldValue, Object newValue){
		
		List<Object> oldKeys = new ArrayList<Object>(keySet());
	
		for (Object o : oldKeys){
			if (get(toString(o)).equals(oldValue)){
				put(toString(o), newValue);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean editKey(Object oldKey, Object newKey){
		
		if (containsKey(toString(oldKey)) && !containsKey(toString(newKey))){
			set(newKey, get(toString(oldKey)));
			remove(toString(oldKey));
			return true;
		}
		
		return false;
	}
	
	private String toString(Object i){
		
		if (i instanceof String){
			return (String) i;
		} else if (i instanceof Enum){
			return ((Enum) i).toString();
		}
		return i.toString();
	}
	
	public boolean isEmpty(){
		return size() <= 0;
	}
	
	public boolean isSize(int size){
		return size() >= size;
	}

	public void set(Object i, Object infos){

		if (infos instanceof Location){
			Location l = (Location) infos;
			put(toString(i), l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
		} else {
			put(toString(i), infos);
		}
	}
}