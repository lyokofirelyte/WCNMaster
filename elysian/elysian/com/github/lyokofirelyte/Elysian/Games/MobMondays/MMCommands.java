/*package com.github.lyokofirelyte.Elysian.Games.MobMondays;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Games.MobMondays.MMMain.locationType;
import com.github.lyokofirelyte.Elysian.api.DivinityGame;
import com.github.lyokofirelyte.Elysian.api.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;

public class MMCommands {

	MMMain root;
	Elysian main;
	
	public MMCommands(MMMain i){
		root = i;
		main = i.main;
	}
	
	@DivCommand(aliases = {"mm", "mobmondays"}, desc = "MobMondays Game Command", help = "/mm help", player = true, min = 1)
	public void onBooth(final Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinityGame dg = root.toDivGame();
		List<String> locations = null;
		switch(args[0]){
			case "select": case "kits":
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
				if(!root.currentPlayers.contains(p.getName())){
					dp.s("You have to do '/mm join' first!");
					return;
				}
				if(args.length != 2 || !root.description.keySet().contains(args[1])){
					showClasses(p);
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
				if(args.length != 2){
					dp.err("Not enough arguments!");
					return;
				}
				root.allowedToJoin = !root.allowedToJoin;
				if(root.allowedToJoin){
					dp.s("You just allowed people to join!");
				}else{
					dp.err("People are no longer able to join!");
				}
				
				root.current = args[1];

				
				break;
				
				
			case "start":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;

				if(root.allowedToJoin == false){
					dp.s("The game is not active yet!");
					return;
				}
				if(root.active){
					dp.s("You can't start it because it has already started!");
					return;
				}
				
				for(String s : root.currentPlayers){
					if(!root.selected.containsKey(s)){
						dp.s("Not everyone has selected a kit!");
						return;
					}
				}
				root.start(root.current);
				root.active = true;
				
				
				
				break;
				
			case "bc":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;
				
				if(args.length == 1){
					dp.s("Not enough arguments!");
					return;
				}
				
				Bukkit.broadcastMessage(main.AS("&3MobMondays &l❅ &9" + DivinityUtilsModule.createString(args, 1)));
				
				break;
				
			case "kick":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;

				if(args.length != 2){
					dp.s("Not enough arguments!");
					return;
				}
				
				if(!Bukkit.getPlayer(args[1]).isOnline()){
					dp.s("Player not online!");
					return;
				}
				
				Bukkit.getPlayer(args[1]).performCommand("mm leave");
				root.msg(args[1] + " was kicked from mob mondays!");
				
				break;
			
			case "leave":
				if(!root.allowedToJoin){
					dp.err("Mob mondays is not active!");
					return;
				}
				
				if(root.currentPlayers.contains(p.getName())){
					root.currentPlayers.remove(p.getName());
					main.api.cancelTask("mobMondaysScore" + p.getName());

					for(PotionEffect pe : p.getActivePotionEffects()){
						p.removePotionEffect(pe.getType());
					}			
					p.getInventory().clear();

					Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){

						@Override
						public void run() {
							p.teleport(root.getLocation(locationType.DEATH));
							
						}
						
					}, 5L);
					
				
					if(root.selected.containsKey(p.getName())){
						root.selected.remove(p.getName());
					}
	
					dp.set(DPI.IN_GAME, false);
					p.getActivePotionEffects().clear();
					p.getInventory().clear();
					p.getInventory().setArmorContents(null);
					p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
					root.msg(dp.name() + " &b has left MobMondays!");
				}

				break;
				
				
			case "join":
				if(!root.allowedToJoin){
					dp.err("Mob mondays is not active!");
					return;
				}
				if(root.currentPlayers.contains(p.getName())){
					dp.s("You already joined!");
					return;
				}
				if(root.active){
					dp.s("You can't join because it has already started!");
					return;
				}
				if(!root.isInventoryEmpty(p)){
					dp.s("Your inventory is not empty!");
					return;
				}
				p.teleport(root.getLocation(locationType.DEATH));
				root.currentPlayers.add(p.getName());
				root.scores.put(dp.name(), 0);
				root.startingPlayers.add(p.getName());
				root.msg(p.getName() + " has joined &aMobMondays!");
				p.setGameMode(GameMode.SURVIVAL);
				dp.set(DPI.IN_GAME, true);
				main.api.repeat(main.api, "event", 0L, 20L, "mobMondaysScore" + dp.name(), new ScoreboardUpdateEvent(Bukkit.getPlayer(dp.uuid()), "mobMondaysGame"));
				
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
				
				locations = new ArrayList<String>(dg.getStringList("arenas." + args[1] + ".monsterspawn"));
				locations.add(root.locationToString(p.getLocation()));
				dg.set("arenas." + args[1] + ".monsterspawn", locations);
				dp.s("Added spawnpoint for the game &6" + args[1]);
				
				break;
				
				
			case "monsterspawnlist":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;

				if(args.length >= 2){
					if(!dg.contains("arenas."  + args[1])){
						dp.err("The arena " + args[1] + " does not exist");
						return;
					}
					
				}
				
				showList(p, args[1]);

				break;
			
			case "showmonsterspawn":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;
				locations = new ArrayList<String>(dg.getStringList("arenas." + args[1] + ".monsterspawn"));
				String[] c = locations.get(Integer.parseInt(args[2])).split("%SPLIT%");
				final Location loc = new Location(Bukkit.getWorld(c[0]), Float.parseFloat(c[1]), Float.parseFloat(c[2]), Float.parseFloat(c[3]));
				loc.getBlock().setType(Material.OBSIDIAN);
				p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ()));
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
					public void run(){
						loc.getBlock().setType(Material.AIR);
					}
				}, 60L);
				
				break;
				
			case "remmonsterspawn":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;
				locations = new ArrayList<String>(dg.getStringList("arenas." + args[1] + ".monsterspawn"));
				locations.remove(Integer.parseInt(args[2]));
				dg.set("arenas." + args[1] + ".monsterspawn", locations);
				
				showList(p, args[1]);
				
				break;
				
			case "spectate":
				if(!root.allowedToJoin){
					dp.err("Mob mondays is not active!");
					return;
				}
				p.teleport(root.getLocation(locationType.DEATH));
				p.setGameMode(GameMode.SURVIVAL);
				
				break;
				
			default:
				
				for(String s : new String[]{
					"select (selects your class)",
					"join",
					"leave",
					"start",
					"spectate",
					"bc <message>", 
					"kick <player>",
					"addarena <name>",
					"remarena <name>",
					"arenalist",
					"setdeath <arena>",
					"setmonsterspawn <arena>",
					"monsterspawnlist <arena>",
					"active <arena>"
				}){
					dp.s("/mm " + s);
				}
				
				break;
		
		
		}
		

		
	}
	
	public void showClasses(Player p){
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		dp.s("The classes are: ");
		for(String s : root.description.keySet()){
			System.out.println(s);
			JSONChatMessage m = new JSONChatMessage("");
			JSONChatExtra info = new JSONChatExtra(main.AS("&b   - " + s));
			info.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&6" + root.getDescription(s) + "\n&aClick to select this kit!"));
			info.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/mm select " + s);
			m.addExtra(info);
			main.s(p, m);
		}
	}
	
	public void showList(Player p, String arena){
		main.s(p, "List of locations for the arena &6 " + arena);
		DivinityGame dg = root.toDivGame();
		int counter = 0;
		List<String> locations = new ArrayList<String>(dg.getStringList("arenas." + arena + ".monsterspawn"));
		for(String s : locations){
			String message = "";
			for(String str : s.split("%SPLIT%")){
				if(str.contains(".")){
					int coord = (int) Float.parseFloat(str);
					message = message + coord + " ";
				}else{
					message = message + str +  " ";
				}
			}
			JSONChatMessage m = new JSONChatMessage(main.AS("&7" + main.numerals.get(counter) + "&f: &3" + message + " "));
			JSONChatExtra showBlock = new JSONChatExtra(main.AS("&7[&e*&7] "));
			JSONChatExtra removeValue = new JSONChatExtra(main.AS("&7[&c-&7]"));
			showBlock.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&eShow the block and tp to it!"));
			showBlock.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/mm showmonsterspawn " + arena + " " + counter);
			removeValue.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&cDelete this spawn location"));
			removeValue.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/mm remmonsterspawn " + arena + " " + counter);
			m.addExtra(showBlock);
			m.addExtra(removeValue);
			main.s(p, m);		
			counter++;
		}
	}
}*/