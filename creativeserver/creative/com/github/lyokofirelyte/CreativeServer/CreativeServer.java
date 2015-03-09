package com.github.lyokofirelyte.CreativeServer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.JSONMap;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.sk89q.wepif.PermissionsResolver;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class CreativeServer extends JavaPlugin implements GameModule, PermissionsResolver {
	
	@Getter
	private String packageName = "CreativeServer";
	
	@Getter
	private String jarName = "CreativeServer-1.0";
	
	@Getter
	private Map<String, String> movingServers = new HashMap<String, String>();
	
	@Getter @Setter
	private Empyreal api;
	
	@Getter @Setter
	private Location toWAPort;

	@Override
	public void onEnable(){
		setApi((Empyreal) Bukkit.getPluginManager().getPlugin("Empyreal"));
		getApi().registerModule(this);
		((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit")).getPermissionsResolver().setPluginPermissionsResolver(this);
	}
	
	@Override @SneakyThrows
	public void onDisable(){
		
		if (toWAPort != null){
			JSONObject obj = new JSONObject();
			obj.put("X", toWAPort.getBlockX());
			obj.put("Y", toWAPort.getBlockY());
			obj.put("Z", toWAPort.getBlockZ());
			Empyreal.saveJSON("./plugins/CreativeServer/port.json", obj);
		}
		
		getApi().unregisterModule(this);
	}
	
	@Override @SneakyThrows
	public void onRegister(){
		
		Bukkit.getWorld("world").setSpawnLocation(0, 0, 0);
		
		File file = new File("./plugins/CreativeServer/port.json");
		file.mkdirs();
		
		if (file.exists()){
			JSONMap<Object, Object> map = Empyreal.loadJSON(file.getPath());
			setToWAPort(new Location(Bukkit.getWorld("world"), map.getInt("X"), map.getInt("Y"), map.getInt("Z")));
		} else {
			file.createNewFile();
		}
	}
	
	@Override
	public void closing(){
		
	}
	
	@Override
	public void onPlayerJoin(Player p){
		Utils.s(p, "Welcome to the creative world! See /plotme help for options!");
		getApi().registerPlayer(new CreativePlayer(p));
	}
	
	@Override
	public void onPlayerQuit(Player p){
		if (!movingServers.containsKey(p.getName())){
			Utils.customBC("&4\u03E0 &7" + p.getDisplayName() + " &6<-> &edisconnect");
			getApi().sendToSocket(getApi().getServerSockets().get("wa"), "chat", "&4\u072E &7" + p.getDisplayName() + " &6<-> &edisconnect");
			getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "chat", "&4\u072E &7" + p.getDisplayName() + " &6<-> &edisconnect");
		} else {
			getMovingServers().remove(p.getName());
		}
	}
	
	@Override
	public void onPlayerChat(GamePlayer<?> gp, String msg){
		Utils.bc("&7" + gp.getPlayer().getDisplayName() + "&f: " + msg);
		getApi().sendToSocket(getApi().getServerSockets().get("wa"), "chat", "&e\u26A1 &7" + gp.getPlayer().getDisplayName() + "&f: " + msg);
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "chat", "&e\u26A1 &7" + gp.getPlayer().getDisplayName() + "&f: " + msg);
	}
	
	@Override
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}

	@Override
	public String[] getGroups(String arg0) { return null; }

	@Override
	public String[] getGroups(OfflinePlayer arg0) { return null; }

	@Override
	public boolean hasPermission(String arg0, String arg1) { return true; }

	@Override
	public boolean hasPermission(OfflinePlayer arg0, String arg1) { return true; }

	@Override
	public boolean hasPermission(String arg0, String arg1, String arg2) { return true; }

	@Override
	public boolean hasPermission(String arg0, OfflinePlayer arg1, String arg2) { return true; }

	@Override
	public boolean inGroup(String arg0, String arg1) { return false; }

	@Override
	public boolean inGroup(OfflinePlayer arg0, String arg1) { return false; }

	@Override
	public String getDetectionMessage() { return ""; }

	@Override
	public void load(){}
}