package com.github.lyokofirelyte.Elysian.Games.Gotcha;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class GotchaCommand {

	private Elysian main;
	private Gotcha root;
	
	public GotchaCommand(Gotcha i){
		root = i;
		main = root.main;
	}
	
	@DivCommand(aliases = {"gotcha"}, desc = "Gotcha Game Command", help = "/gotcha help", player = true, min = 1)
	public void onGotcha(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinityGame dg = root.toDivGame();
		
		if (!args[0].equals("join") && !args[0].equals("leave")){
			if (!main.api.perms(p, "wa.staff.mod2", false)){
				return;
			}
		}
		
		switch (args[0]){
		
			case "help":
				
				for (String msg : new String[]{
					"/gotcha addarena <name>",
					"/gotcha remarena <name>",
					"/gotcha arenalist",
					"/gotcha lobby <arena>",
					"/gotcha addspawnpoint <arena>",
					"/gotcha clearspawnpoints <arena>",
					"/gotcha join <arena>",
					"/gotcha leave <arena>"
				}){
					dp.s(msg);
				}
				
			break;
			
			case "arenalist":
				
				if (root.toDivGame().contains("Arenas")){
					for (String game : root.toDivGame().getConfigurationSection("Arenas").getKeys(false)){
						dp.s(game);
					}
				} else {
					dp.err("No arenas!");
				}
				
			break;
			
			case "addarena":
				
				if (args.length == 2 && !dg.contains("Arenas." + args[1])){
					dg.set("Arenas." + args[1] + ".Name", args[1]);
					dp.s("Created the arena &6" + args[1] + "&b!");
				} else {
					dp.err("Invalid args or that arena already exists.");
				}
				
			break;
			
			case "remarena":
				
				if (args.length == 2 && dg.contains("Arenas." + args[1])){
					dg.set("Arenas." + args[1], null);
					dp.s("Removed the arena &6" + args[1] + "&b!");
				} else {
					dp.err("Invalid args or that arena does not exist.");
				}
				
			break;
			
			case "addspawnpoint":
				
				Location l = p.getLocation();
				
				if (args.length == 2 && dg.contains("Arenas." + args[1])){
					if (!dg.contains("Arenas." + args[1] + ".Spawns")){
						dg.set("Arenas." + args[1] + ".Spawns", new ArrayList<String>());
					}
					String locs = l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch();
					List<String> locz = dg.getStringList("Arenas." + args[1] + ".Spawns");
					locz.add(locs);
					dg.set("Arenas." + args[1] + ".Spawns", locz);
					dp.s("Adjusted the spawn point or lobby for &6" + args[1] + "&b!");
				} else {
					dp.err("Invalid args or that arena does not exist.");
				}
				
			break;
			
			case "clearspawnpoints":
				
				dg.set("Arenas." + args[1] + ".Spawns", null);
				dp.s("Cleared!");
				
			break;
			
			case "lobby":
				
				l = p.getLocation();
				
				if (args.length == 2 && dg.contains("Arenas." + args[1])){
					dg.set("Arenas." + args[1] + "." + args[0], l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
					dp.s("Adjusted the spawn point or lobby for &6" + args[1] + "&b!");
				} else {
					dp.err("Invalid args or that arena does not exist.");
				}
				
			break;
			
			case "join":
				
				if (!dp.getBool(DPI.IN_GAME) && args.length == 2){
					
					if (!root.containsKey(args[1]) && dg.contains("Arenas." + args[1])){
						root.getGame(args[1]);
					}
					
					if (root.containsKey(args[1]) && !root.getGame(args[1]).isInProgress()){
						root.getGame(args[1]).addPlayer(dp);
						dp.s("Added to the game!");
						main.api.event(new DivinityTeleportEvent(p, root.getGame(args[1]).getLobby()));
					} else {
						dp.err("No arena found or it is in progress!");
					}
				} else {
					dp.err("You're in a game!");
				}
				
			break;
			
			case "leave":
				
				if (dp.getBool(DPI.IN_GAME) && args.length == 2){
					if (root.containsKey(args[1])){
						root.getGame(args[1]).remPlayer(dp);
						dp.s("Removed from the game!");
						main.api.event(new DivinityTeleportEvent(p, root.getGame(args[1]).getLobby()));
					} else {
						dp.err("No arena found!");
					}
				} else {
					dp.err("You're not in a game!");
				}
				
			break;
		}
	}
}