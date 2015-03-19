package com.github.lyokofirelyte.GameServer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class GameServer extends JavaPlugin implements GameModule {
	
	@Getter @Setter
	private Empyreal api;
	
	@Getter
	private String packageName = "GameServer";
	
	@Getter
	private String jarName = "GameServer-1.0";
	
	@Getter
	private Map<String, String> serverDeployQueue = new HashMap<String, String>();
	
	@Getter
	private Map<String, GameSign> signs = new HashMap<String, GameSign>();
	
	@Getter
	private Map<String, GameWarp> warps = new HashMap<String, GameWarp>();
	
	@Getter
	private Map<String, Integer> socketPorts = new HashMap<String, Integer>();
	
	@Override
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}
	
	@Override
	public void onEnable(){
		setApi((Empyreal) getServer().getPluginManager().getPlugin("Empyreal"));
		getApi().registerModule(this);
	}
	
	@Override
	public void onDisable(){
		getApi().unregisterModule(this);
		for (GameSign sign : signs.values()){
			sign.save();
		}
	}
	
	@Override
	public void onPlayerJoin(Player p){

		GamePlayer<GameServerPlayer> player = new GameServerPlayer(p);
		getApi().registerPlayer(player);
		
		Utils.s(p, "Welcome to the Game Server! This chat is connected to WA and Creative.");
	}
	
	@Override
	public void onPlayerQuit(Player p){
		getApi().unregisterPlayer(getApi().getGamePlayer(p.getUniqueId()));
	}
	
	@Override
	public void onPlayerChat(GamePlayer<?> gp, String msg){
		
		Utils.bc("&7" + gp.getPlayer().getDisplayName() + "&f: &e" + msg);
		getApi().sendToSocket(getApi().getServerSockets().get("wa"), "chat", "&7" + gp.getPlayer().getDisplayName() + "&f: " + msg);
		getApi().sendToSocket(getApi().getServerSockets().get("Creative"), "chat", "&7" + gp.getPlayer().getDisplayName() + "&f: " + msg);
	}
	
	public void updateAllSigns(String serverName, int line, String msg){
		for (GameSign sign : getSigns().values()){
			if (sign.getServerName().equals(serverName)){
				sign.updateLine(line, msg);
			}
		}
	}
	
	@Override
	public void onRegister(){
		
		File file = new File("./plugins/GameServer/signs/");
		file.mkdirs();
		
		for (File sign : file.listFiles()){
			GameSign newSign = new GameSign(sign);
			signs.put(newSign.getFullName(), newSign);
		}
		
		file = new File("./plugins/GameServer/warps/");
		file.mkdirs();
		
		for (File sign : file.listFiles()){
			GameWarp newSign = new GameWarp(sign);
			warps.put(newSign.getName(), newSign);
		}
	}
	
	@Override
	public void closing(){}
}