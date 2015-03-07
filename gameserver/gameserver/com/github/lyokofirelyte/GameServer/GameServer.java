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
import com.github.lyokofirelyte.Empyreal.GameModule;
import com.github.lyokofirelyte.Empyreal.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils;

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
	}
	
	@Override
	public void onPlayerJoin(Player p){

		GamePlayer<GameServerPlayer> player = new GameServerPlayer(p);
		getApi().registerPlayer(player);
		
		Utils.s(p, "Welcome to the Game Server!");
	}
	
	@Override
	public void onPlayerQuit(Player p){
		getApi().unregisterPlayer(getApi().getGamePlayer(p.getUniqueId()));
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
		
		System.out.println("Registered.");
		
		File file = new File("./plugins/GameServer/signs/");
		file.mkdirs();
		
		for (File sign : file.listFiles()){
			GameSign newSign = new GameSign(sign);
			signs.put(newSign.getFullName(), newSign);
		}
	}
	
	@Override
	public void closing(){
		for (GameSign sign : signs.values()){
			sign.save();
		}
	}
}