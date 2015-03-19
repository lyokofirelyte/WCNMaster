package com.github.lyokofirelyte.GameServer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import lombok.Getter;

import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
public class CommandGameServer implements AutoRegister<CommandGameServer> {

	@Getter
	private CommandGameServer type = this;
	
	private GameServer main;
	
	public CommandGameServer(GameServer i){
		main = i;
	}
	
	@GameCommand(aliases = { "gs", "gameserver" }, desc = "GameServer Command", help = "/gs")
	public void onGameServer(CommandSender cs, GamePlayer<?> gp, String[] args){
		if (cs.isOp()){
			for (String s : main.getApi().getServerSockets().keySet()){
				gp.s(s + " isClosed(): " + main.getApi().getServerSockets().get(s).isClosed() + ", getPort(): " + main.getApi().getServerSockets().get(s).getPort());
			}
		} else {
			gp.s("&c&oSorry, this command is super seekrit!");
		}
	}
	
	@GameCommand(aliases = { "setwarp" }, desc = "GameServer SetWarp Command", help = "/setwarp <name>", player = true, perm = "gameserver.staff")
	public void onSetWarp(Player p, GamePlayer<?> gp, String[] args){
		
		Location l = p.getLocation();
		GameWarp warp = new GameWarp(main, args[0], l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch(), l.getWorld().getName());
		main.getWarps().put(args[0], warp);
		
		Utils.s(p, "Complete.");
	}
	
	@GameCommand(aliases = { "remwarp" }, desc = "GameServer RemWarp Command", help = "/remwarp <name>", player = true, perm = "gameserver.staff")
	public void onRemWarp(Player p, GamePlayer<?> gp, String[] args){
		
		boolean found = false;
		
		for (String name : main.getWarps().keySet()){
			if (name.equals(args[0])){
				main.getWarps().get(name).delete();
				main.getWarps().remove(name);
				Utils.s(p, "Deleted.");
				found = true;
				break;
			}
		}
		
		if (!found){
			Utils.s(p, "&c&oNo warp found.");
		}
	}
	
	@GameCommand(aliases = { "warp" }, desc = "GameServer Warp Command", help = "/warp <name>", player = true, perm = "gameserver.staff")
	public void onWarp(Player p, GamePlayer<?> gp, String[] args){
		
		boolean found = false;
		
		for (String name : main.getWarps().keySet()){
			if (name.equals(args[0])){
				main.getWarps().get(name).teleport(p);
				found = true;
				break;
			}
		}
		
		if (!found){
			Utils.s(p, "&c&oNo warp found.");
		}
	}
	
	@GameCommand(aliases = { "warplist" }, desc = "GameServer WarpList Command", help = "/warplist", player = true, perm = "gameserver.staff")
	public void onWarpList(Player p, GamePlayer<?> gp, String[] args){
		
		String str = "";
		
		for (String warp : main.getWarps().keySet()){
			str += str.equals("") ? warp : "&f, &6" + warp;
		}
		
		Utils.s(p, str);
	}
}