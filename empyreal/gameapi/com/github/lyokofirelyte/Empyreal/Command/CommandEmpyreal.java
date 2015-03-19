package com.github.lyokofirelyte.Empyreal.Command;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class CommandEmpyreal implements AutoRegister<CommandEmpyreal> {
	
	private Empyreal main;
	
	@Getter
	private CommandEmpyreal type = this;
	
	@Setter
	private boolean shutdownInProgress = false;
	
	public CommandEmpyreal(Empyreal i){
		main = i;
	}
	
	String[] empLogo = new String[]{
		"&e. . . . .&f(  &6E m p y r e a l &f  )&e. . . . .",
		"",
		"&7&oA Game API by Hugs, for WA Minigame Servers",	
		"&6&oOnline Servers: &7",
	};
	
	@GameCommand(min = 1, aliases = { "empyreal", "emp" }, help = "/emp ?", desc = "Empyreal (GAME API) root command", perm = "emp.member")
	public void onEmpyreal(CommandSender sender, GamePlayer<?> gp, String[] args){
		
		String str = "";
		
		for (String game : main.getServerSockets().keySet()){
			str += str.equals("") ? game : ", " + game;
		}
		
		empLogo[3] += str;
		
		for (String l : empLogo){
			sender.sendMessage(Utils.AS(l));
		}
	}
	
	@GameCommand(aliases = { "globalcast", "gcast", "gc" }, help = "/gc <msg>", desc = "Global broadcast + title", perm = "gameserver.staff")
	public void onGC(CommandSender cs, GamePlayer<?> gp, String[] args){
		
		if (!main.getServerName().equals("GameServer")){
			main.sendToSocket(main.getServerSockets().get("GameServer"), "forward", "globalcast", Utils.createString(args, 0));
			main.sendToSocket(main.getServerSockets().get("GameServer"), "globalcast", Utils.createString(args, 0));
		} else {
			main.sendToAllServerSockets("globalcast", Utils.createString(args, 0));
			Utils.customBC(Utils.createString(args, 0));
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title '" + Utils.createString(args, 0) + "'");
		}
		
	}
	
	@GameCommand(aliases = { "setop" }, help = "/setop <player>", desc = "Set OP Command", perm = "gameserver.staff")
	public void onSetOP(CommandSender cs, GamePlayer<?> gp, String[] args){
		
		if (!main.getServerName().equals("GameServer")){
			main.sendToSocket(main.getServerSockets().get("GameServer"), "forward", "setop", args[0]);
			main.sendToSocket(main.getServerSockets().get("GameServer"), "setop", args[0]);
		} else {
			main.sendToAllServerSockets("setop", args[0]);
			Bukkit.getOfflinePlayer(args[0]).setOp(true);
		}
		
		Utils.s(cs, "Complete.");
	}
	
	@GameCommand(aliases = { "s", "spawn", "hub", "lobby" }, help = "/spawn", desc = "Spawn Command", player = true, perm = "emp.member")
	public void onSpawn(Player p, GamePlayer<?> gp, String[] args){
		p.teleport(p.getWorld().getSpawnLocation());
	}
	
	@GameCommand(aliases = { "setspawn" }, help = "/setspawn", desc = "Set Spawn Command", player = true, perm = "gameserver.staff")
	public void onSetSpawn(Player p, GamePlayer<?> gp, String[] args){
		p.getWorld().setSpawnLocation(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
		gp.s("Default world spawn set.");
	}
	
	@GameCommand(aliases = { "o" }, help = "/o <msg>", desc = "Staff Chat", player = true)
	public void onO(Player p, GamePlayer<?> gp, String[] args){
		
		if (p.isOp() || gp.getPerms().contains("gameserver.staff")){
			
			if (!main.getServerName().equals("GameServer")){
				main.sendToSocket(main.getServerSockets().get("GameServer"), "forward", "o", "&7" + p.getDisplayName() + "&f: &c&o" + Utils.createString(args, 0));
				main.sendToSocket(main.getServerSockets().get("GameServer"), "o", "&c" + p.getDisplayName() + "&f: &c&o" + Utils.createString(args, 0));
			} else {
				for (Player player : Bukkit.getOnlinePlayers()){
					if (player.isOp()){
						player.sendMessage(Utils.AS("&4\u273B &c" + p.getDisplayName() + "&f: &c&o" + Utils.createString(args, 0)));
					}
				}
				main.sendToAllServerSockets("o", "&7" + p.getDisplayName() + "&f: &c&o" + Utils.createString(args, 0));
			}
			
		} else {
			gp.s("&c&oSorry, only staff can use staff chat!");
		}
	}
}