package com.github.lyokofirelyte.Elysian.Events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PatrolEntityDeathEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Entity dead;
    private DamageCause damageCause;
    private String patrolId;
    private List<ItemStack> drops;
    private Location loc;
    private boolean cancelled;
    
    public PatrolEntityDeathEvent(EntityDeathEvent e){
    	player = (Player) e.getEntity().getKiller();
    	dead = e.getEntity();
    	setDamageCause(e.getEntity().getLastDamageCause().getCause());
    	setPatrolId(dead.hasMetadata("PatrolID") ? dead.getMetadata("PatrolID").get(0).asString() : "none");
    	setDrops(e.getDrops());
    	setLoc(dead.getLocation());
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

	public DamageCause getDamageCause(){
		return damageCause;
	}

	public void setDamageCause(DamageCause damageCause){
		this.damageCause = damageCause;
	}

	public String getPatrolId(){
		return patrolId;
	}

	public void setPatrolId(String patrolId){
		this.patrolId = patrolId;
	}

	public List<ItemStack> getDrops(){
		return drops;
	}

	public void setDrops(List<ItemStack> drops){
		this.drops = drops;
	}

	public Location getLoc(){
		return loc;
	}

	public void setLoc(Location loc){
		this.loc = loc;
	}
	
	public Entity getDead(){
		return dead;
	}
}