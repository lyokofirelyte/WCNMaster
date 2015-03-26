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
		
		System.setOut(main.getOut());

		for (GameModule m : main.getGameModules()){
			m.onPlayerChat(main.getGamePlayer(e.getPlayer().getUniqueId()), e.getMessage());
		}
		
		System.setOut(main.getSecondOut());
		
		if (main.getServerName().equals("GameServer")){
			for (String s : main.getServerSockets().keySet()){
				if (s.startsWith("WCNConsole")){
					main.sendToSocket(main.getServerSockets().get(s), "wcn_logger", "(&6" + main.getServerName() + "&7) " + e.getPlayer().getDisplayName() + "&f: " + e.getMessage(), "END");
				}
			}
		} else {
			main.sendToSocket(main.getServerSockets().get("GameServer"), "wcn_logger", "(&6" + main.getServerName() + "&7) " + e.getPlayer().getDisplayName() + "&f: " + e.getMessage(), "END");
		}
	}
}