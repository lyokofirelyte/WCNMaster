package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;

public class DivinityPluginMessageEvent extends Event implements Cancellable {
	
	private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();
    private CommandSender commandSender;
    private Player p;
    private String type;
    private String[] extras;
    private JSONChatMessage jsonMessage;

    public DivinityPluginMessageEvent(CommandSender p, String type) {
        commandSender = p;
        if (commandSender instanceof Player){
        	this.p = (Player) p;
        }
        this.type = type;
    }
    
    public DivinityPluginMessageEvent(Player p, JSONChatMessage msg) {
        commandSender = p;
        this.p = (Player) p;
        this.type = "JSON";
        jsonMessage = msg;
    }
    
    public DivinityPluginMessageEvent(CommandSender p, String type, String[] extras) {
        commandSender = p;
        if (commandSender instanceof Player){
        	this.p = (Player) p;
        }
        this.type = type;
        this.extras = extras;
    }

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	public boolean isJson(){
		return jsonMessage != null;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }
	
	public String[] getExtras(){
		return extras;
	}
	
	public Player getPlayer(){
		return p;
	}
	
	public CommandSender getSender(){
		return commandSender;
	}
	
	public JSONChatMessage getJSONMessage(){
		return jsonMessage;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String t){
		type = t;
	}
}