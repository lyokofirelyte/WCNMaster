package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ScoreboardUpdateEvent extends Event implements Cancellable {
	

    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private boolean cancelled;
    private String reason;

    public ScoreboardUpdateEvent(Player p, String r){
        this.p = p;
        reason = r;
    }
    
    public ScoreboardUpdateEvent(Player p){
        this.p = p;
        reason = "update";
    }
    
    public String getReason(){
    	return reason;
    }
    
    public void setReason(String s){
    	reason = s;
    }
    
    public void setPlayer(Player p){
    	this.p = p;
    }
 
    public void setCancelled(boolean cancel){
        cancelled = cancel;
    }
    
    public boolean isCancelled(){
        return cancelled;
    }

    public Player getPlayer(){
    	return p;
    }
 
    public HandlerList getHandlers(){
        return handlers;
    }
 
    public static HandlerList getHandlerList(){
        return handlers;
    }
}