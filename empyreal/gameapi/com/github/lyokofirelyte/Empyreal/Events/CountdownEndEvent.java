package com.github.lyokofirelyte.Empyreal.Events;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CountdownEndEvent extends Event implements Cancellable {

	@Getter @Setter
	private boolean cancelled = false;
	
	@Getter @Setter
	private HandlerList handlers;
	
	@Getter @Setter
	private int id;
	
	public CountdownEndEvent(int id){
		setId(id);
	}
	
	public CountdownEndEvent(String integer){
		setId(Integer.parseInt(integer));
	}
}