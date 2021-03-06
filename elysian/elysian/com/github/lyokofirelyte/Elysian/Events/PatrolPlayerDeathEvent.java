package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PatrolPlayerDeathEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private String patrolId;
    private Location loc;
    private boolean cancelled;
    
    public PatrolPlayerDeathEvent(Player p, String id){
    	patrolId = id;
    	player = p;
    	setLoc(player.getLocation());
    	//p.setHealth((Damagable)p).getMaxHealth());
    }
    
    public void setPlayer(Player p){
    	player = p;
    }
 
    public void setCancelled(boolean cancel){
        cancelled = cancel;
    }
    
    public boolean isCancelled(){
        return cancelled;
    }

    public Player getPlayer(){
    	return player;
    }
    
    public HandlerList getHandlers(){
        return handlers;
    }
 
    public static HandlerList getHandlerList(){
        return handlers;
    }

	public String getPatrolId(){
		return patrolId;
	}

	public void setPatrolId(String patrolId){
		this.patrolId = patrolId;
	}


	public Location getLoc(){
		return loc;
	}

	public void setLoc(Location loc){
		this.loc = loc;
	}
}