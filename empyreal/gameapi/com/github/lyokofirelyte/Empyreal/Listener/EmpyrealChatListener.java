package com.github.lyokofirelyte.Empyreal.Listener;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;

public class EmpyrealChatListener implements AutoRegister<EmpyrealChatListener>, Listener {

	@Getter
	private EmpyrealChatListener type = this;
	
	@Setter
	private Empyreal main;
	
	public EmpyrealChatListener(Empyreal i){
		main = i;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		
		e.setCancelled(true);
		
		for (GameModule m : main.getGameModules()){
			m.onPlayerChat(main.getGamePlayer(e.getPlayer().getUniqueId()), e.getMessage());
		}
	}
}