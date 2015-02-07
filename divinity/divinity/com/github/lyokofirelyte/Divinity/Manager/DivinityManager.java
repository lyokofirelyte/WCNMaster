package com.github.lyokofirelyte.Divinity.Manager;

import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityAllianceModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityGameModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayerModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityRegionModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityRingModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorageModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinitySystemModule;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;

public class DivinityManager {

	private API api;
	
	public DivinityManager(API i){
		api = i;
		refresh();
	}
	
	public Map<String, Map<String, DivinityStorageModule>> data = new THashMap<>();
	private List<String> dirs = Arrays.asList(allianceDir, regionsDir, ringsDir, sysDir, gamesDir, backupDir);
	
	final static public String sysDir = "./plugins/Divinity/system/";
	final static public String dir = "./plugins/Divinity/users/";
	final static public String allianceDir = "./plugins/Divinity/alliances/";
	final static public String regionsDir = "./plugins/Divinity/regions/";
	final static public String ringsDir = "./plugins/Divinity/rings/";
	final static public String gamesDir = "./plugins/Divinity/games/";
	final static public String backupDir = "./plugins/Divinity/backup/";
	
	public DivinityPlayerModule searchForPlayer(String s){
		
		if (!data.containsKey(dir)){
			data.put(dir, new THashMap<String, DivinityStorageModule>());
		}

		for (DivinityStorageModule dp : data.get(dir).values()){
			if (dp.name().toLowerCase().startsWith(s.toLowerCase()) || dp.uuid().toString().equals(s)){
				return (DivinityPlayerModule) dp;
			}
		}
		
		try {
			if (Bukkit.getPlayer(UUID.fromString(s)) != null){
				return (DivinityPlayerModule) modifyObject(dir, s, true, true);
			}
			if (Bukkit.getOfflinePlayer(UUID.fromString(s)) != null){
				return (DivinityPlayerModule) modifyObject(dir, s, true, true);
			}
		} catch (Exception e){}
		
		return null;
	}
	
	public Map<String, DivinityStorageModule> getMap(String directory){
		return data.containsKey(directory) ? data.get(directory) : new THashMap<String, DivinityStorageModule>();
	}
	
	public DivinityStorageModule getStorage(String directory, String name){
		return data.containsKey(directory) && data.get(directory).containsKey(name) ? data.get(directory).get(name) : modifyObject(directory, name, true, true);
	}
	
	public YamlConfiguration lc(File file){
		try {
			return YamlConfiguration.loadConfiguration(file); 
		} catch (Exception e){
			return new YamlConfiguration();
		}
	}
	
	@SuppressWarnings("deprecation")
	private YamlConfiguration userTemplate() {
		try {
			return YamlConfiguration.loadConfiguration(api.main.getResource("newUser.yml"));
		} catch (Exception e){
			return new YamlConfiguration();
		}
	}

	public DivinityStorageModule modifyObject(String directory, String name, boolean newFile, boolean load){
		
		if (directory.equals(dir)){
			
			if (new File(directory + "/" + name + ".yml").exists()){
				newFile = false;
			}
			
			try {
				UUID u = UUID.fromString(name);
			} catch (Exception e){
				if (new File(directory + name + ".yml").exists()){
					new File(directory + name + ".yml").delete();
				}
				return null;
			}
		}
		
		if (!new File(directory).exists()){
			new File(directory).mkdirs();
		}
		
		File file = new File(directory + name + ".yml");
		
		if (!Arrays.asList(new File(directory).list()).contains(name + ".yml") && load){
			try { file.createNewFile(); } catch (Exception e){} newFile = true;
		}
		
		YamlConfiguration yaml = newFile && directory.equals(dir) && load ? userTemplate() : newFile && load ? new YamlConfiguration() : lc(file);
		DivinityStorageModule storage = directory.equals(dir) && load ? new DivinityStorageModule(UUID.fromString(name), api) : load ? new DivinityStorageModule(name, api) : data.get(directory).get(name);
		storage.set(DPI.CHAOS_LIST, "none");
		
		if (load){
			
			switch (directory){
				case dir: storage = newFile ? newUser(new DivinityPlayerModule(storage.uuid(), api)) : new DivinityPlayerModule(storage.uuid(), api); break;
				case allianceDir: storage = new DivinityAllianceModule(name, api); break;
				case regionsDir: storage = new DivinityRegionModule(name, api); break;
				case ringsDir: storage = new DivinityRingModule(name, api); break;
				case sysDir: storage = new DivinitySystemModule(name, api); break;
				case gamesDir: storage = new DivinityGameModule("system", name, api);
				default:
					storage = directory.startsWith(gamesDir) ? new DivinityGameModule(directory.split("\\/")[4], name, api) : storage;
				break;
			}
			
			for (String sec : yaml.getKeys(true)){
				storage.set(sec, yaml.get(sec));
			}
			
			if (!data.containsKey(directory)){
				data.put(directory, new THashMap<String, DivinityStorageModule>());
			}
			
			data.get(directory).put(name, storage);
			
		} else {
			
			try { storage.save(file); } catch (Exception e){}
			
			if (directory.equals(sysDir) && name.contains("system")){
				((DivinitySystemModule) storage).saveMarkkit();
			}
		}

		return storage;
	}
	
	private DivinityPlayerModule newUser(DivinityPlayerModule player){

		DivinityUtilsModule.bc("Welcome &6" + player.name() + " &bto Worlds Apart!");
		Bukkit.getPlayer(player.uuid()).setDisplayName("&7" + player.name());
		player.set(DPI.DISPLAY_NAME, "&7" + player.name());
		
		return player;
	}
	
	public void load(boolean full) throws Exception {
		
		if (full){

			for (String d : dirs){
				
				if (!new File(d).exists()){
					new File(d).mkdirs();
				}
				
				for (String file : new File(d).list()){
					if (!file.contains("~") && file.contains(".yml") && !new File(file).isDirectory()){
						modifyObject(d, file.replace(".yml", ""), false, true);
					}
				}
			}
			
			try {
				api.getDivSystem().setMarkkit(lc(new File(DivinityManager.sysDir + "markkit.yml")));
				api.getDivSystem().loadEffects();
				
				for (AutoSave save : api.saveClasses.values()){
					save.load();
				}
			} catch (Exception e){
				e.printStackTrace();
			}
			
		} else {
			
			for (String gameFolder : new File(gamesDir).list()){
				if (new File(gamesDir + gameFolder).isDirectory()){
					for (String gameFile : new File(gamesDir + gameFolder + "/").list()){
						if (!new File(gameFile).isDirectory() && gameFile.contains("yml")){
							try {
								modifyObject(gamesDir + gameFolder + "/", gameFile.replace(".yml", ""), false, true);
							} catch (Exception e){}
						}
					}
				} else if (gameFolder.contains(".yml")){
					modifyObject(gamesDir, gameFolder.replace(".yml", ""), false, true);
				}
			}
		}
	}
	
	public void save() throws IOException {
		
		for (AutoSave save : api.saveClasses.values()){
			try {
				save.save();
			} catch (Exception e){
				System.out.println(save.toString() + " failed to save.");
			}
		}
		
 		for (String objectType : data.keySet()){
 			for (String objectName : data.get(objectType).keySet()){
				modifyObject(objectType, objectName, false, false);
				if (!objectName.contains("markkit")){
					modifyObject(objectType, objectName, false, false);
				} else {
					try{
						api.getDivSystem().getMarkkit().save(new File(DivinityManager.sysDir, "markkit.yml"));
					} catch (Exception e){
						e.printStackTrace();
					}
				}
 			}
 		}
	}
	
	public void backup(){
		
		String backup = backupDir + api.divUtils.getMonthAndDay() + "@" + ((int) new File(backupDir).list().length+1) + "/";
		List<String> paths = new ArrayList<String>();
		Set<String> datas = new HashSet<String>(data.keySet());
		
		for (String dir : datas){
			if (!dir.contains("logger") && !dir.contains("backup")){
				String path = backup + dir.substring(dir.indexOf(dir.split("\\/")[3]));
				File backupDir = new File(path);
				backupDir.mkdirs();
				data.put(path, data.get(dir));
				paths.add(path);
			}
		}
		
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String path : paths){
			data.remove(path);
		}
	}
	
	private void refresh(){
		for (String d : dirs){
			data.put(d, new THashMap<String, DivinityStorageModule>());
		}
	}
}