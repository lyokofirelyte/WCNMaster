package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerMoneyChangeEvent extends Event implements Cancellable {
	

    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private boolean cancelled = false;
    private int oldBal;
    private int newBal;

    public PlayerMoneyChangeEvent(Player player, int oldBalance, int newBalance){
        p = player;
        oldBal = oldBalance;
        newBal = newBalance;
    }
    
    public boolean isIncrease(){
    	return newBal > oldBal;
    }
    
    public boolean isDecrease(){
    	return newBal < oldBal;
    }
    
    public boolean isSame(){
    	return newBal == oldBal;
    }

	@Override
	public boolean isCancelled(){
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel){
		cancelled = cancel;
	}

	public int getNewBal(){
		return newBal;
	}
	
	public int getDifference(){
		return isIncrease() ? newBal - oldBal : isDecrease() ? oldBal - newBal : 0;
	}

	public void setNewBal(int newBal){
		this.newBal = newBal;
	}

	public int getOldBal(){
		return oldBal;
	}

	public void setOldBal(int oldBal){
		this.oldBal = oldBal;
	}

	public Player getPlayer(){
		return p;
	}

	public void setPlayer(Player p){
		this.p = p;
	}
	
	@Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}