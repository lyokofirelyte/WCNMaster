package com.github.lyokofirelyte.Platform.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.lyokofirelyte.Platform.Rounds.Round;

public class RoundEndEvent extends Event implements Cancellable {
	
	private Player p;
	private Round round;
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	
	public RoundEndEvent(Player player, Round r){
		p = player;
		round = r;
	}
	
	public Round getRound(){
		return round;
	}

	public Player getPlayer() {
		return p;
	}

	public void setPlayer(Player player) {
		p = player;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	public boolean isCancelled() {
		return cancelled;
	}

    public HandlerList getHandlers() {
        return handlers;
    }
 
	public static HandlerList getHandlerList() {
        return handlers;
    }
}