package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.github.lyokofirelyte.Divinity.Events.DivinityPluginMessageEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class ElyMessages implements Listener, AutoRegister {
	
	private Elysian main;
	private String h = "&3Elysian &7\u2744 &b";
	private String h2 = "&7\u2744 &b";
	private Player p;
	private CommandSender cs;
	private DivinityPlayer dp;
	
	public ElyMessages(Elysian i){
		main = i;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onMessage(DivinityPluginMessageEvent e){
		
		if (e.isCancelled()){
			return;
		}
		
		cs = e.getSender();
		
		if (cs instanceof Player){
			p = e.getPlayer();
			dp = main.api.getDivPlayer(p);
		}
		
		if (dp != null){
			if (dp.getBool(DPI.IS_BANNING)){
				if (e.getType().equals("globalChat")){
					dp.getList(DPI.PAUSED_CHAT).add(e.getExtras()[0]);
				}
				return;
			}
		}
		
		switch (e.getType()){
		
			case "globalChat": break;
			
			case "JSON":
				
				if (e.isJson() && p != null){
					e.getJSONMessage().sendToPlayer(p);
				}
				
			break;
		
			case "noPerms":
				
				s(cs, "&4No permissions!");
				
			break;
		
			case "balance":
				
				s(cs, "You currently have &6" + dp.getInt(DPI.BALANCE) + " &bshinies!");
			
			break;
			
			case "playerNotFound":
				
				s(cs, "&c&oThat player could not be found. Did you spell it correctly?");
				
			break;
			
			case "invalidNumber":
				
				s(cs, "&c&oThat number is invalid.");
				
			break;
			
			case "muted":
				
				s(cs, "&c&oYou are currently muted. Time left: &6" + ((dp.getLong(DPI.MUTE_TIME) - System.currentTimeMillis())/1000)/60 + " minutes.");
				
			break;
			
			default:
				
				if (e.getExtras() == null){
					s(cs, e.getType());
				} else {
					for (String ss : e.getExtras()){
						s(cs, ss);
					}
				}
				
			break;
		}
	}
	
	public void s(CommandSender sender, String message){
		
		if (sender instanceof Player){
			if (main.api.getDivPlayer((Player)sender).getBool(DPI.ELY)){
				sender.sendMessage(main.AS(h2 + message));
			} else {
				main.api.getDivPlayer((Player)sender).set(DPI.ELY, true);
				sender.sendMessage(main.AS(h + message));
			}
		} else {
			sender.sendMessage(main.AS(h + message));
		}
	}
}