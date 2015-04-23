package com.github.lyokofirelyte.Empyreal.Listener;

import lombok.Getter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Events.SocketMessageEvent;
import com.github.lyokofirelyte.Empyreal.Listener.Handler.SocketHandler;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class SocketMessageListener implements AutoRegister<SocketMessageListener>, Listener { 

	@Getter
	private Empyreal main;
	
	@Getter
	private SocketMessageListener type = this;
	
	public SocketMessageListener(Empyreal i){
		main = i;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSocketMessage(SocketMessageEvent e){

		if (Handler.containsValue(e.getReason().toUpperCase())){
			Handler h = Handler.valueOf(e.getReason().toUpperCase());
			SocketHandler handler = e.getHandler() == null ? h.getHandler() : e.getHandler();
			
			if (handler != null && handler.checkConditions(main)){
				h.run(e);
			}
		}
	}
}