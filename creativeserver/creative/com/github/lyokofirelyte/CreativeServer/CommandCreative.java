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

import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

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
			gp.s("The block inside of you will send you to WA on click.");
		} else {
			gp.s("Sorry, only admins can use this command!");
		}
	}

	@GameCommand(perm = "emp.member", aliases = { "s", "spawn" }, desc = "The spawn command", help = "/s", player = true)
	public void onSpawn(Player cs, GamePlayer<?> gp, String[] args){
		cs.teleport(cs.getWorld().getSpawnLocation());
	}
	
	@GameCommand(perm = "emp.member", aliases = { "time" }, desc = "Set the time of yourself", help = "/time <#>", player = true)
	public void onTimeSet(Player cs, GamePlayer<?> gp, String[] args){
		if(args.length == 0){
				gp.s("/time 0 (sets the time to dawn)");
				gp.s("/time 6000 (sets the time to midday)");
				gp.s("/time 12000 (sets the time to dusk)");
				gp.s("/time 18000 (sets the time to midnight");
				gp.s("Your player time is: &6" + cs.getPlayerTime());
			return;
		}
		if(Utils.isInteger(args[0]) && args.length == 1){
			cs.setPlayerTime(Integer.parseInt(args[0]), false);
			gp.s("Time set to " + args[0]);
		}else{
			gp.s("That is not a number!");
		}
	}
	
	@GameCommand(perm = "emp.member", aliases = { "tp", "teleport" }, desc = "Teleport Command", help = "/tp <player>", player = true)
	public void onTP(Player cs, GamePlayer<?> gp, String[] args){
		if(args.length == 2 && (cs.isOp() || gp.getPerms().contains("gameserver.staff"))){
			if(Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[1]) != null){
				Bukkit.getPlayer(args[0]).teleport(Bukkit.getPlayer(args[1]));
				gp.s("Teleported " + Bukkit.getPlayer(args[0]).getDisplayName() + " to " + Bukkit.getPlayer(args[1]).getDisplayName());
			}
		}else if(args.length == 1){
			if (Bukkit.getPlayer(args[0]) != null){
				cs.teleport(Bukkit.getPlayer(args[0]));
			} else {
				gp.s("&c&oThat player isn't online!");
			}	
		}

	}
	
	@GameCommand(aliases = { "tphere" }, desc = "Teleport Command", help = "/tphere <player>", player = true)
	public void onTPHere(Player cs, GamePlayer<?> gp, String[] args){
		if(cs.isOp() || gp.getPerms().contains("gameserver.staff")){
			if (Bukkit.getPlayer(args[0]) != null){
				Bukkit.getPlayer(args[0]).teleport(cs);
			} else {
				gp.s("&c&oThat player isn't online!");
			}
		}else{
			gp.s("&c&oNo permissions!");
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
				"/plotme undeny",
				"/plot",
				"/plot home",
				"/plot auto",
				"/plot info",
				"/plot biome",
				"/plot list",
				"/plot tp",
				"/plot dispose",
				"/plot add",
				"/plot remove",
				"/plot deny",
				"/plot undeny",
				"/spawn",
				"/s"
			};
			
			String[] blockedCommands = new String[]{
				"//schematic",
				"//limit",
				"/stop",
				"/op",
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
					e.getPlayer().performCommand(e.getMessage().substring(1));
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