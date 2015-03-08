package com.github.lyokofirelyte.CreativeServer;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class CommandCreative implements AutoRegister<CommandCreative>, Listener {

	private CreativeServer main;
	
	@Getter
	private CommandCreative type = this;
	
	public CommandCreative(CreativeServer i){
		main = i;
	}
	
	@GameCommand(aliases = { "setport" }, desc = "Set the location of the to-wa port", help = "/setport", player = true)
	public void onSetPort(Player cs, GamePlayer<?> gp, String[] args){
		
		if (cs.isOp()){
			main.setToWAPort(gp.getPlayer().getLocation());
			gp.s("The block under you will send you to WA on click.");
		} else {
			gp.s("Sorry, only admins can use this command!");
		}
	}
	
	@GameCommand(aliases = { "tp", "teleport" }, desc = "Teleport Command", help = "/tp <player>", player = true)
	public void onTP(Player cs, GamePlayer<?> gp, String[] args){
		
		if (Bukkit.getPlayer(args[0]) != null){
			cs.teleport(Bukkit.getPlayer(args[0]));
		} else {
			gp.s("&c&oThat player isn't online!");
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e){
		
		if (!e.getPlayer().isOp()){
			
			String[] allowedCommands = new String[]{
				"/plotme",
				"/plotme home",
				"/plotme auto",
				"/plotme info",
				"/plotme biome",
				"/plotme list",
				"/plotme tp",
				"/plotme dispose",
				"/plotme add",
				"/plotme remove",
				"/plotme deny",
				"/plotme undeny"
			};
			
			String[] blockedCommands = new String[]{
				"//schematic",
				"//limit"
			};
			
			for (String cmd : blockedCommands){
				if (e.getMessage().startsWith(cmd)){
					e.setCancelled(true);
					Utils.s(e.getPlayer(), "&c&oNo permissions!");
					return;
				}
			}
			
			for (String cmd : allowedCommands){
				if (e.getMessage().toLowerCase().startsWith(cmd)){
					e.setCancelled(true);
					e.getPlayer().setOp(true);
					e.getPlayer().performCommand(e.getMessage());
					e.getPlayer().setOp(false);
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null){
			
			Location port = main.getToWAPort();
			Location clicked = e.getClickedBlock().getLocation();
			
			if (port.getBlockX() == clicked.getBlockX() && port.getBlockY() == clicked.getBlockY() && port.getBlockZ() == clicked.getBlockZ()){
				main.getMovingServers().put(e.getPlayer().getName(), "wa");
				main.getApi().sendToServer(e.getPlayer().getName(), "wa");
			}
		}
	}
}