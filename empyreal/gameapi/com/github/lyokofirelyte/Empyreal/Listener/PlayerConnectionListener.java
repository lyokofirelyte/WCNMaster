package com.github.lyokofirelyte.Empyreal.Listener;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.google.common.collect.Iterables;

public class PlayerConnectionListener implements AutoRegister<PlayerConnectionListener>, Listener {

	private Empyreal main;
	
	@Getter
	private PlayerConnectionListener type = this;
	
	public PlayerConnectionListener(Empyreal i){
		main = i;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		
		e.setJoinMessage(null);
		
		for (GameModule m : main.getGameModules()){
			m.onPlayerJoin(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onQuit(final PlayerQuitEvent e){
		
		e.setQuitMessage(null);
		
		if (!main.getServerName().equals("GameServer") && !main.getServerName().equals("Creative")){
			APIScheduler.DELAY.start(main, "quit " + e.getPlayer().getName(), 5L, new Runnable(){
				public void run(){
					if (Bukkit.getServer().getOnlinePlayers().size() <= 0 || (Bukkit.getServer().getOnlinePlayers().size() == 1 && Iterables.getFirst(Bukkit.getOnlinePlayers(), null).equals(e.getPlayer()))){
						Bukkit.getServer().shutdown();
					}
				}
			});
		}
		
		for (GameModule m : main.getGameModules()){
			m.onPlayerQuit(e.getPlayer());
		}
	}
}