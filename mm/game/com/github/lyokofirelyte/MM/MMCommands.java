package com.github.lyokofirelyte.MM;

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
import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Elysian.Games.MobMondays.MMMain.locationType;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class MMCommands {

	MMMain root;
	Empyreal api;
	
	public MMCommands(MMMain i){
		root = i;
		api = i.getApi();
	}
	
	@DivCommand(aliases = {"mm", "mobmondays"}, desc = "MobMondays Game Command", help = "/mm help", player = true, min = 1)
	public void onBooth(final Player p, String[] args){
		GamePlayer<MMPlayer> mmp = root.getApi().getGamePlayer(p.getUniqueId());

		List<String> locations = null;
		switch(args[0]){
			case "select": case "kits":

				if(args.length != 2 || !root.description.keySet().contains(args[1])){
					showClasses(p);
					return;
				}
				
				MMClasses mmc = new MMClasses();
				mmc.assignClass(p, args[1]);
				
				mmp.setKit(args[1].toLowerCase());
				
				//TODO: mmp.setKit(args[1].toLowerCase()); To set the current kit of the player.
				//TODO: For loop through all the game players to see if everyone has selected a kit instead of checking the arra
				break;

			case "start":

				for(String s : root.currentPlayers){
					if(!root.selected.containsKey(s)){
						mmp.s("Not everyone has selected a kit!");
						return;
					}
				}
				root.start(root.current);
				
				
				break;
				
			case "bc":
				if(!mmp.getPerms().contains("gameserver.staff")) return;
				
				if(args.length == 1){
					mmp.s("Not enough arguments!");
					return;
				}
				
				Bukkit.broadcastMessage(Utils.AS("&3MobMondays &l❅ &9" + DivinityUtilsModule.createString(args, 1)));
				
				break;
				
			case "kick":
				if(!mmp.getPerms().contains("gameserver.staff")) return;

				if(args.length != 2){
					mmp.s("Not enough arguments!");
					return;
				}
				
				if(!Bukkit.getPlayer(args[1]).isOnline()){
					mmp.s("Player not online!");
					return;
				}
				
				Bukkit.getPlayer(args[1]).performCommand("mm leave");
				Utils.bc(args[1] + " was kicked from mob mondays!");
				
				break;
			
		/*	case "leave":
				if(!root.allowedToJoin){
					mmp.err("Mob mondays is not active!");
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
	
					mmp.set(mmpI.IN_GAME, false);
					p.getActivePotionEffects().clear();
					p.getInventory().clear();
					p.getInventory().setArmorContents(null);
					p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
					root.msg(mmp.name() + " &b has left MobMondays!");
				}

				break;
				*/

			case "addarena":
				if(!mmp.getPerms().contains("gameserver.staff")) return;

				if(args.length != 2){
					mmp.err("Not enough arguments!");
					return;
				}
				root.getSave().set("arenas." + args[1] + ".spawn", root.locationToString(p.getLocation()));
				mmp.s("Created the arena " + args[1]);
				break;
				
			case "remarena":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;

				if(args.length != 2){
					mmp.err("Not enough arguments!");
					return;
				}
				if(!root.getSave().contains("arenas."  + args[1])){
					mmp.err("The arena " + args[1] + " does not exist");
					return;
				}
				root.getSave().set("arenas." + args[1], null);
				mmp.s("The arena " + args[1] + " has been removed!");
				break;
				
			case "arenalist":
				
				if (root.getSave().contains("arenas")){
					for (String arena : root.getSave().getConfigurationSection("arenas").getKeys(false)){
						mmp.s(arena);
					}
				} else {
					mmp.err("No arenas!");
				}
				
				break;
				
			case "setdeath":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;
				if(args.length != 2){
					mmp.err("Not enough arguments!");
					return;
				}
				if(!root.getSave().contains("arenas."  + args[1])){
					mmp.err("The arena " + args[1] + " does not exist");
					return;
				}
				
				root.getSave().set("arenas." + args[1] + ".death", root.locationToString(p.getLocation()));
				mmp.s("You set the death location!");
				break;
			
			case "setmonsterspawn":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;
				if(args.length != 2){
					mmp.err("Not enough arguments!");
					return;
				}
				if(!root.getSave().contains("arenas."  + args[1])){
					mmp.err("The arena " + args[1] + " does not exist");
					return;
				}
				
				locations = new ArrayList<String>(root.getSave().getList("arenas." + args[1] + ".monsterspawn"));
				locations.add(root.locationToString(p.getLocation()));
				root.getSave().set("arenas." + args[1] + ".monsterspawn", locations);
				mmp.s("Added spawnpoint for the game &6" + args[1]);
				
				break;
				
				
			case "monsterspawnlist":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;

				if(args.length >= 2){
					if(!root.getSave().contains("arenas."  + args[1])){
						mmp.err("The arena " + args[1] + " does not exist");
						return;
					}
					
				}
				
				showList(p, args[1]);

				break;
			
			case "showmonsterspawn":
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;
				locations = new ArrayList<String>(root.getSave().getList("arenas." + args[1] + ".monsterspawn"));
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
				locations = new ArrayList<String>(root.getSave().getList("arenas." + args[1] + ".monsterspawn"));
				locations.remove(Integer.parseInt(args[2]));
				root.getSave().set("arenas." + args[1] + ".monsterspawn", locations);
				
				showList(p, args[1]);
				
				break;
				
			case "spectate":
				if(!root.allowedToJoin){
					mmp.err("Mob mondays is not active!");
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
					mmp.s("/mm " + s);
				}
				
				break;
		
		
		}
		

		
	}
	
	public void showClasses(Player p){
		GamePlayer<MMPlayer> mmp = root.getApi().getGamePlayer(p.getUniqueId());
		mmp.s("The classes are: ");
		for(String s : root.description.keySet()){
			System.out.println(s);
			JSONChatMessage m = new JSONChatMessage("");
			JSONChatExtra info = new JSONChatExtra(Utils.AS("&b   - " + s));
			info.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, Utils.AS("&6" + root.getDescription(s) + "\n&aClick to select this kit!"));
			info.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/mm select " + s);
			m.addExtra(info);
			Utils.s(p, m);
		}
	}
	
	public void showList(Player p, String arena){
		Utils.s(p, "List of locations for the arena &6 " + arena);
		int counter = 0;
		List<String> locations = new ArrayList<String>(root.getSave().getList("arenas." + arena + ".monsterspawn"));
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
			JSONChatMessage m = new JSONChatMessage(Utils.AS("&7" + main.numerals.get(counter) + "&f: &3" + message + " "));
			JSONChatExtra showBlock = new JSONChatExtra(Utils.AS("&7[&e*&7] "));
			JSONChatExtra removeValue = new JSONChatExtra(Utils.AS("&7[&c-&7]"));
			showBlock.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, Utils.AS("&eShow the block and tp to it!"));
			showBlock.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/mm showmonsterspawn " + arena + " " + counter);
			removeValue.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, Utils.AS("&cDelete this spawn location"));
			removeValue.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/mm remmonsterspawn " + arena + " " + counter);
			m.addExtra(showBlock);
			m.addExtra(removeValue);
			Utils.s(p, m);		
			counter++;
		}
	}
}
