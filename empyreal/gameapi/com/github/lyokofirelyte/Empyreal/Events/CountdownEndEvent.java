package com.github.lyokofirelyte.Empyreal.Events;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CountdownEndEvent extends Event implements Cancellable {

	@Getter @Setter
	private boolean cancelled = false;
	
    private static final HandlerList handlers = new HandlerList();
	
	@Getter @Setter
	private int id;
	
	public CountdownEndEvent(int id){
		setId(id);
	}
	
	public CountdownEndEvent(String integer){
		setId(Integer.parseInt(integer));
	}
	
	@Override
	public HandlerList getHandlers(){
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
}