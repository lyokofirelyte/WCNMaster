package com.github.lyokofirelyte.Elysian.Games.MobMondays;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.MobMondays.MMMain.locationType;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class MMCommands {

	MMMain root;
	Elysian main;
	
	public MMCommands(MMMain i){
		root = i;
		main = i.main;
	}
	
	@DivCommand(aliases = {"mm", "mobmondays"}, desc = "MobMondays Game Command", help = "/mm help", player = true, min = 1)
	public void onBooth(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinityGame dg = root.toDivGame();
		
		switch(args[0]){
			case "select":
				if(!root.allowedToJoin){
					dp.err("Mob mondays is not active!");
					return;
				}
				if(root.active){
					dp.s("You can't join because it has already started!");
					return;
				}
//				if(p.getWorld().getName().contains("world") || p.getWorld().getName().equalsIgnoreCase("end") || p.getWorld().getName().equalsIgnoreCase("nether")){
//					dp.s("You can't perform this command in the survival world!");
//					return;
//				}
				if(!root.players.contains(p.getName())){
					dp.s("You have to do '/mm join' first!");
					return;
				}
				if(args.length != 2 || !root.classes.contains(args[1])){
					dp.s("The classes are: ");
					for(String s : root.classes){
						dp.s("   - " + s);
					}
					return;
				}
				
				MMClasses mmc = new MMClasses();
				mmc.assignClass(p, args[1]);
				if(root.selected.containsKey(p.getName())){
					root.selected.remove(p.getName());
					root.selected.put(p.getName(), args[1].toLowerCase());
				}else{
					root.selected.put(p.getName(), args[1].toLowerCase());
				}
				break;
			
			
			case "active":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;

				root.allowedToJoin = !root.allowedToJoin;
				if(root.allowedToJoin){
					dp.s("You just allowed people to join!");
				}else{
					dp.err("People are no longer able to join!");
				}
				
				root.current = args[1];

				
				break;
				
				
			case "start":
				if(root.allowedToJoin == false){
					dp.s("The game is not active yet!");
					return;
				}
				if(root.active){
					dp.s("You can't start it because it has already started!");
					return;
				}
				root.start(root.current);
				root.active = true;
				
				break;
				
			
			case "join":
				if(!root.allowedToJoin){
					dp.err("Mob mondays is not active!");
					return;
				}
				if(root.players.contains(p.getName())){
					dp.s("You already joined!");
					return;
				}
				if(root.active){
					dp.s("You can't join because it has already started!");
					return;
				}
				p.teleport(root.getLocation(locationType.DEATH));
				root.players.add(p.getName());
				root.msg(p.getName() + " has joined &aMobMondays!");
				p.setGameMode(GameMode.SURVIVAL);
				
				
				break;
				
			
			case "addarena":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;

				if(args.length != 2){
					dp.err("Not enough arguments!");
					return;
				}
				dg.set("arenas." + args[1] + ".spawn", root.locationToString(p.getLocation()));
				dp.s("Created the arena " + args[1]);
				break;
				
			case "remarena":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;

				if(args.length != 2){
					dp.err("Not enough arguments!");
					return;
				}
				if(!dg.contains("arenas."  + args[1])){
					dp.err("The arena " + args[1] + " does not exist");
					return;
				}
				dg.set("arenas." + args[1], null);
				dp.s("The arena " + args[1] + " has been removed!");
				break;
				
			case "arenalist":
				
				if (dg.contains("arenas")){
					for (String arena : dg.getConfigurationSection("arenas").getKeys(false)){
						dp.s(arena);
					}
				} else {
					dp.err("No arenas!");
				}
				
				break;
				
			case "setdeath":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;
				if(args.length != 2){
					dp.err("Not enough arguments!");
					return;
				}
				if(!dg.contains("arenas."  + args[1])){
					dp.err("The arena " + args[1] + " does not exist");
					return;
				}
				
				dg.set("arenas." + args[1] + ".death", root.locationToString(p.getLocation()));
				dp.s("You set the death location!");
				break;
			
			case "setmonsterspawn":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;
				if(args.length != 2){
					dp.err("Not enough arguments!");
					return;
				}
				if(!dg.contains("arenas."  + args[1])){
					dp.err("The arena " + args[1] + " does not exist");
					return;
				}
				
				List<String> locations = new ArrayList<String>(dg.getStringList("arenas." + args[1] + ".monsterspawn"));
				locations.add(root.locationToString(p.getLocation()));
				dg.set("arenas." + args[1] + ".monsterspawn", locations);
				
				
				break;
				
				
			default:
				
				for(String s : new String[]{
					"select (selects your class)",
					"start",
					"join",
					"addarena <name>",
					"remarena <name>",
					"arenalist",
					"setdeath <arena>",
					"setmonsterspawn <arena>",
					"active <arena>"
				}){
					dp.s("/mm " + s);
				}
				
				break;
		
		
		}
		

		
	}
	
}
