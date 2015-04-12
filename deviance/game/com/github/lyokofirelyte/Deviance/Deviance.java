package com.github.lyokofirelyte.Deviance;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Events.CountdownEndEvent;
import com.github.lyokofirelyte.Empyreal.Listener.SocketMessageListener.Handler;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class Deviance extends JavaPlugin implements GameModule {
	
	@Getter
	private String packageName = "Deviance";
	
	@Getter
	private String jarName = "Deviance-1.0";
	
	@Getter @Setter
	private Empyreal api;
	
	@Getter @Setter
	private boolean gameStarted = false;

	@Override
	public void onEnable(){
		api = (Empyreal) Bukkit.getPluginManager().getPlugin("Empyreal");
		api.registerModule(this);	
	}
	
	@Override
	public void onDisable(){
		
		
	}

	@Override
	public void onRegister(){
		
		api.sendToSocket("GameServer", Handler.SERVER_BOOT_COMPLETE);
		
		if (!gameStarted && Bukkit.getOnlinePlayers().size() >= 3){
			setGameStarted(true);
			api.startCountdown(60, 1, "&eDeviance &f-&e", Countdown.STARTUP.toString());
		}
	}
	
	@EventHandler
	public void onCountdown(CountdownEndEvent e){
		
		switch (Countdown.valueOf("" + e.getId())){
			
			case STARTUP:
				
				if (Bukkit.getOnlinePlayers().size() < 3){
					setGameStarted(false);
					Utils.bc("Too many players left. Waiting for players...");
				} else {
					api.startCountdown(10, 1, "&eDeviance &f-&e ", Countdown.START_GAME.toString());
				}
				
			break;
			
			case START_GAME:
				
				api.sendToSocket("GameServer", Handler.GAME_IN_PROGRESS);
				
			break;
		}
	}
	
	@Override
	public void onPlayerJoin(Player p){
		
		DeviantPlayer dp = new DeviantPlayer(p);
		api.registerPlayer(dp);
		
		if (p.isOp()){
			dp.getPerms().add("gameserver.staff");
			p.setOp(false);
		}
		
		p.teleport(p.getWorld().getSpawnLocation());
	}
	
	@Override
	public void onPlayerQuit(Player p){
		api.unregisterPlayer(api.getGamePlayer(p.getUniqueId()));
	}
	
	@Override
	public void onPlayerChat(GamePlayer<?> gp, String msg){
		Utils.bc("&7" + gp.getPlayer().getDisplayName() + "&f: " + msg);
	}
	
	@Override
	public void closing(){
		api.unregisterModule(this);
	}
	
	@Override
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}
	
	enum Countdown {
		
		STARTUP(0),
		START_GAME(1);
		
		Countdown(int id){
			this.id = id;
		}
		
		@Getter
		int id;
	}
}
