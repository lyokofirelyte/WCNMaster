package com.github.lyokofirelyte.Divinity.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.Events.DivinityEventHandler;

public class SkillExpGainEvent extends Event implements DivinityEventHandler {
	

    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private boolean cancelled;
    private ElySkill skill;
    private int xp;

    public SkillExpGainEvent(Player who, ElySkill skill, int xpAmount) {
        p = who;
        this.skill = skill;
        xp = xpAmount;
    }
    
    public void setPlayer(Player p){
    	this.p = p;
    }
    
    public void setSkill(ElySkill skill){
    	this.skill = skill;
    }
    
    public void setXp(int xp){
    	this.xp = xp;
    }
 
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
    
    public int getXp(){
    	return xp;
    }
    
    public ElySkill getSkill(){
    	return skill;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }

    public Player getPlayer(){
    	return p;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}