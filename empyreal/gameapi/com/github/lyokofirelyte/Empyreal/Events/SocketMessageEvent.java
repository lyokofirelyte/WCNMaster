package com.github.lyokofirelyte.Empyreal.Events;

import java.io.BufferedReader;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.lyokofirelyte.Empyreal.Listener.Handler.SocketHandler;

public class SocketMessageEvent extends Event implements Cancellable {

	@Getter @Setter
	private boolean cancelled = false;
	
    private static final HandlerList handlers = new HandlerList();
	
	@Getter @Setter
	private String fromServer;
	
	@Getter @Setter
	private String toServer;
	
	@Getter @Setter
	private String reason;
	
	@Getter @Setter
	private String message;
	
	@Getter @Setter
	private BufferedReader in;
	
	@Getter @Setter
	private SocketHandler handler;
	
	
	public SocketMessageEvent(String fromServer, String toServer, String reason, String message, BufferedReader in){
		setFromServer(fromServer);
		setToServer(toServer);
		setReason(reason);
		setMessage(message);
		setIn(in);
	}
	
	public void fire(){
		Bukkit.getPluginManager().callEvent(this);
	}
	
	@Override
	public HandlerList getHandlers(){
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
}