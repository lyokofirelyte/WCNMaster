package com.github.lyokofirelyte.Divinity;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;
import com.github.lyokofirelyte.Divinity.Commands.DivinityRegistry;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.Manager.JSONManager;
import com.github.lyokofirelyte.Divinity.Manager.PlayerLocation;
import com.github.lyokofirelyte.Divinity.Manager.TeamspeakManager;
import com.github.lyokofirelyte.Divinity.Manager.WebsiteManager;
import com.github.lyokofirelyte.Divinity.Manager.JSONManager.JSONClickType;
import com.github.lyokofirelyte.Divinity.PublicUtils.FW;
import com.github.lyokofirelyte.Divinity.PublicUtils.TitleExtractor;
import com.github.lyokofirelyte.Divinity.Storage.DivinityAllianceModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityGameModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityRegionModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityRingModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorageModule;
import com.github.lyokofirelyte.Divinity.Storage.DivinitySystemModule;
import com.github.lyokofirelyte.Spectral.SpectralAPI;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;
import com.github.lyokofirelyte.Spectral.Identifiers.DivGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityAlliance;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRegion;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRing;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;
import com.google.common.collect.ImmutableMap;

public class API implements SpectralAPI {
	
	public Divinity main;
	public DivinityUtilsModule divUtils;
	public DivinityManager divManager;
	public DivinityRegistry divReg;
	
	public JSONManager json;
	public WebsiteManager web;
	public TeamspeakManager ts3;
	public PlayerLocation playerLocation;
	public TitleExtractor title;
	public Reflections ref;
	public FW fw;
	
    public Map<String, Integer> activeTasks = new HashMap<String, Integer>();
	public Map<String, Object> clazzez = new THashMap<String, Object>();
	public Map<String, AutoSave> saveClasses = new THashMap<String, AutoSave>();
	
	public API(Divinity i){
		
		main = i;
		divUtils = new DivinityUtilsModule(this);
		divManager = new DivinityManager(this);
		divReg = new DivinityRegistry(this, main);
		title = new TitleExtractor(this);
		playerLocation = new PlayerLocation(this);
		ts3 = new TeamspeakManager(this);
		fw = new FW(this);
		json = new JSONManager(this);
		web = new WebsiteManager(this);
		
		clazzez.put(DivInvManager.class.toString(), new DivInvManager(this));
		clazzez.put(WebsiteManager.class.toString(), web);
		
		try {
			divManager.load(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object getInstance(Class<?> clazz){
		return clazzez.get(clazz.toString());
	}

	public DivinityPlayer getDivPlayer(Object o){
		
		String uuid = "";
		
		if (o instanceof String){
			
			uuid = (String) o;
			
		} else if (o instanceof Player){
			
			Player p = (Player) o;
			
			if (p.isOnline()){
				uuid = p.getUniqueId().toString();
			}
			
		} else if (o instanceof UUID){
			uuid = ((UUID) o).toString();
		}
		
		return divManager.searchForPlayer(uuid);
	}
	
	public Collection<DivinityStorageModule> getAllPlayers(){
		return divManager.data.get(DivinityManager.dir).values();
	}
	
	public DivinityAlliance getDivAlliance(String alliance){
		return (DivinityAllianceModule) divManager.getStorage(DivinityManager.allianceDir, alliance.toLowerCase());
	}
	
	public DivinityRegion getDivRegion(String region){
		return (DivinityRegionModule) divManager.getStorage(DivinityManager.regionsDir, region.toLowerCase());
	}
	
	public DivinityRing getDivRing(String ring){
		return (DivinityRingModule) divManager.getStorage(DivinityManager.ringsDir, ring.toLowerCase());
	}
	
	public DivinityGame getDivGame(String gameType, String gameName){
		return (DivinityGameModule) divManager.getStorage(DivinityManager.gamesDir + gameType + "/", gameName.toLowerCase());
	}
	
	public DivinitySystem getDivSystem(){
		return (DivinitySystemModule) divManager.getStorage(DivinityManager.sysDir, "system");
	}
	
	public Player getPlayer(String name){
		return Bukkit.getPlayer(getDivPlayer(name).uuid());
	}
	
	public String AS(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public boolean doesPartialPlayerExist(String name){
		
		for (DivinityStorageModule dp : divManager.data.get(divManager.dir).values()){
			if (dp.name().toLowerCase().startsWith(name.toLowerCase()) || dp.uuid().toString().equals(name)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean doesRegionExist(String region){
		
		for (DivinityStorageModule dp : divManager.data.get(divManager.regionsDir).values()){
			if (dp.name().toLowerCase().startsWith(region.toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean doesRingExist(String ring){
		
		for (DivinityStorageModule dp : divManager.data.get(divManager.ringsDir).values()){
			if (dp.name().toLowerCase().startsWith(ring.toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isOnline(String p){
		return Bukkit.getPlayer(getDivPlayer(p).uuid()) != null;
	}

	public boolean perms(CommandSender cs, String perm, boolean silent){
		
		boolean success = cs instanceof Player ? getDivPlayer((Player) cs).getList(DPI.PERMS).contains(perm) || cs.isOp() : cs.isOp();
		
		if (!silent && !success){
			cs.sendMessage(AS("&c&oYou don't have enough permissions! :("));
		}
		
		return success;
	}
	
	public void registerCommands(Object... o){
		divReg.registerCommands(o);
	}
	
	public void backup(){
		divManager.backup();
	}
	
	public JSONChatMessage createJSON(String message, ImmutableMap<String, ImmutableMap<JSONClickType, String[]>> jsonValue){
		return json.create(message, jsonValue);
	}
	
	public void registerListeners(Plugin plugin, List<Listener> o){
		for (Listener obj : o){
			Bukkit.getPluginManager().registerEvents(obj, plugin);
		}
	}
	
	public void event(Event e){
		Bukkit.getPluginManager().callEvent(e);
	}
	
	public void cancelTask(String name){
		if (activeTasks.containsKey(name)){
			Bukkit.getScheduler().cancelTask(activeTasks.get(name));
		}
	}
	
	public void schedule(Object clazz, String method, long delay, String taskName, Object... args){
		
		for (Method m : clazz.getClass().getMethods()){
			if (m.getName().equals(method)){
				activeTasks.put(taskName, Bukkit.getScheduler().scheduleSyncDelayedTask(main, args != null ? new DivinityScheduler(clazz, m, args) : new DivinityScheduler(clazz, m), delay));
				return;
			}
		}
	}
	
	public void repeat(Object clazz, String method, long delay, long period, String taskName, Object... args){
		
		for (Method m : clazz.getClass().getMethods()){
			if (m.getName().equals(method)){
				activeTasks.put(taskName, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, args != null ? new DivinityScheduler(clazz, m, args) : new DivinityScheduler(clazz, m), delay, period));
				return;
			}
		}
	}
	
	public void saveAllFiles(){
		
		try {
			divManager.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadAllFiles(boolean full){
		
		try {
			divManager.load(full);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerAll(Object mainClassInstanceOfElysian){
  
        List<Class<?>> allClasses = new ArrayList<Class<?>>();
        
        try {
        
	        List<String> classNames = new ArrayList<String>();
	        ZipInputStream zip = new ZipInputStream(new FileInputStream("./plugins/Elysian-1.0.jar"));
	        boolean look = false;
	        
	        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
	        	
	        	if (entry.isDirectory()){
	        		look = entry.getName().contains("Elysian");
	        	}
	        	
	            if (entry.getName().endsWith(".class") && !entry.isDirectory() && look) {
	            	
	                StringBuilder className = new StringBuilder();
	                
	                for (String part : entry.getName().split("/")) {
	                	
	                    if (className.length() != 0){
	                        className.append(".");
	                    }
	                    
	                    className.append(part);
	                    
	                    if (part.endsWith(".class")){
	                        className.setLength(className.length()-".class".length());
	                    }
	                }
	                
	                classNames.add(className.toString());
	            }
	        }
	        
	        for (String clazz : classNames){
	        	allClasses.add(Class.forName(clazz));
	        }
	        
        } catch (Exception e){
        	e.printStackTrace();
        }
        
		for (Class<?> clazz : allClasses){
			
			Object obj = null;

			try {
				Constructor<?> con = clazz.getConstructors()[0];
				con.setAccessible(true);
				obj = con.newInstance(mainClassInstanceOfElysian);
			} catch (Exception e1){
				continue;
			}
			
			if (obj instanceof AutoRegister && !clazz.toString().contains("\\$") && !clazzez.containsKey(clazz.toString())){
				clazzez.put(clazz.toString(), obj);
			}
		}
		
		int x = 0;
		
		for (Object obj : clazzez.values()){
			
			if (obj instanceof Listener){
				Bukkit.getPluginManager().registerEvents((Listener) obj, main);
			}
			
			if (obj instanceof AutoSave){
				saveClasses.put(obj.toString(), (AutoSave) obj);
				((AutoSave) saveClasses.get(obj.toString())).load();
			}
			
			if (obj instanceof DivGame){
				for (Object o : ((DivGame) obj).registerSubClasses()){
					if (o instanceof Listener){
						Bukkit.getPluginManager().registerEvents((Listener) o, main);
					}
					registerCommands(o);
					x++;
				}
			}
			
			registerCommands(obj);
		}
		
		main.getLogger().log(Level.INFO, "Registered " + (clazzez.size()+x) + " classes and " + main.commandMap.size() + " unique commands!");
	}
}