package com.github.lyokofirelyte.Empyreal.Events;

import java.io.BufferedReader;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.lyokofirelyte.Empyreal.Listener.SocketMessageListener.SocketHandler;
import com.github.lyokofirelyte.Empyreal.Listener.SocketObject;

public class SocketMessageEvent extends Event implements Cancellable {

	@Getter @Setter
	private boolean cancelled = false;
	
	@Getter @Setter
	private HandlerList handlers;
	
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
	
	@Getter @Setter
	private SocketObject object;
	
	public SocketMessageEvent(String fromServer, String toServer, String reason, String message, BufferedReader in){
		setFromServer(fromServer);
		setToServer(toServer);
		setReason(reason);
		setMessage(message);
		setIn(in);
	}
	
	public SocketMessageEvent(SocketObject obj){
		setFromServer(obj.getFromServer());
		setToServer("GameServer");
		setReason(obj.getReason().toString());
		setHandler(obj.getReason().getHandler());
		setObject(obj);
	}
	
	public void fire(){
		Bukkit.getPluginManager().callEvent(this);
	}
}