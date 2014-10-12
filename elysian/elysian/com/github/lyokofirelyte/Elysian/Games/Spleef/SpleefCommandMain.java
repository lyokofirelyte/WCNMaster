package com.github.lyokofirelyte.Elysian.Games.Spleef;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefGame;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefPlayer;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefPlayerData;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import static com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefModule.*;

public class SpleefCommandMain {

	private Spleef main;
	
	public SpleefCommandMain(Spleef i) {
		main = i;
	}

	@DivCommand(aliases = {"spleef"}, desc = "Main Spleef Command", help = "/spleef help", player = true, min = 1)
	public void onSpleef(Player p, String[] args){
		
		SpleefPlayer sender = doesPlayerExist(p.getUniqueId()) ? getSpleefPlayer(p.getUniqueId()) : new SpleefStorage(main.main, SpleefDataType.PLAYER, p.getName()).toPlayer();
		
		if (args.length == 0){
			onSpleef(p, new String[]{"help"});
			return;
		}
		
		boolean rem = !doesPlayerExist(p.getName());
		
		switch (args[0].toLowerCase()){
		
			case "help":
				
				for (String s : new String[]{
					"/spleef addarena",
					"/spleef remarena <ID>",
					"/spleef arenalist",
					"/spleef p1start <ID>",
					"/spleef p2start <ID>",
					"/spleef spec <player>",
					"/spleef invite <player>",
					"/spleef manreset <ID>",
					"/spleef mat <ID>",
					"/spleef enable/disable <ID>",
					"/spleef score <player>",
					"/spleef leaderboard",
				}){
					s(p, s);
				}
				
			break;
			
			case "mat":
				
				if (!perms(p, "wa.staff.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						getSpleefGame(args[1]).putt(SpleefGameData.MATERIAL, p.getItemInHand().getType().toString());
						sender.toDp().s("Changed mat to &6" + p.getItemInHand().getType().toString() + "&b.");
					} else {
						sender.toDp().err("That game does not exist.");
					}
				}
				
			break;
			
			case "enable": case "disable":
				
				if (!perms(p, "wa.staff.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						getSpleefGame(args[1]).setEnabled(Boolean.valueOf(args[0].replace("enable", "true").replace("disable", "false")));
						sender.toDp().s("Updated.");
					} else {
						sender.toDp().err("No game found by that name.");
					}
				}
				
			break;
			
			case "manreset":
				
				if (!perms(p, "wa.staff.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						reset(getSpleefGame(args[1]));
					} else {
						sender.toDp().err("That game does not exist.");
					}
				}
				
			break;
			
			case "invite":
				
				if (a(sender, args.length, 2)){
					if (main.main.api.doesPartialPlayerExist(args[1])){
						SpleefPlayer them = null;
						them = doesPlayerExist(args[1]) ? matchSpleefPlayer(args[1]) : new SpleefStorage(main.main, SpleefDataType.PLAYER, main.main.api.getPlayer(args[1]).getName()).toPlayer();
						if ((them.getInvite() == null || them.getInvite().equals(them)) && !them.inGame()){
							them.setInvite(sender);
							sender.setInvite(sender);
							them.toDp().s(p.getDisplayName() + " &bhas invited you to spleef. Type &6/spleef accept &bor &6/spleef deny&b.");
							sender.toDp().s("Sent invite!");
							rem = false;
						} else {
							sender.toDp().err("They already have an invite.");
						}
					} else {
						sender.toDp().err("That player is offline.");
					}
				} else {
					sender.toDp().err("That player does not exist!");
				}
				
			break;
			
			case "accept":
				
				if (sender.getInvite() != null && !sender.getInvite().equals(sender)){
					if (Bukkit.getPlayer(sender.getInvite().toDp().uuid()) != null){
						if (!sender.inGame() && !sender.getInvite().inGame()){
							for (SpleefGame game : getAllGames()){
								if (game.involvedPlayers().size() <= 0 && game.isEnabled()){
									
									sender.toDp().set(DPI.BACKUP_INVENTORY, new ArrayList<ItemStack>());
									
									for (ItemStack i : Bukkit.getPlayer(sender.toDp().uuid()).getInventory().getContents()){
										if (i != null){
											sender.toDp().getStack(DPI.BACKUP_INVENTORY).add(i);
										}
									}
									
									sender.getInvite().toDp().set(DPI.BACKUP_INVENTORY, new ArrayList<ItemStack>());
									
									for (ItemStack i : Bukkit.getPlayer(sender.getInvite().toDp().uuid()).getInventory().getContents()){
										if (i != null){
											sender.getInvite().toDp().getStack(DPI.BACKUP_INVENTORY).add(i);
										}
									}
									
									Bukkit.getPlayer(sender.toDp().uuid()).getInventory().clear();
									Bukkit.getPlayer(sender.getInvite().toDp().uuid()).getInventory().clear();
									Bukkit.getPlayer(sender.toDp().uuid()).getInventory().addItem(new ItemStack(Material.DIAMOND_SPADE));
									Bukkit.getPlayer(sender.getInvite().toDp().uuid()).getInventory().addItem(new ItemStack(Material.DIAMOND_SPADE));
									game.involvedPlayers().add(sender);
									game.involvedPlayers().add(sender.getInvite());
									sender.setCurrentGame(game);
									sender.getInvite().setCurrentGame(game);
									sender.setInGame(true);
									sender.getInvite().setInGame(true);
									sender.setOpponent(sender.getInvite());
									sender.getInvite().setOpponent(sender);
									sender.getInvite().setInvite(sender.getInvite());
									sender.setInvite(sender);
									game.teleportPlayers();
									return;
								}
							}
						}
						sender.toDp().err("No games are open at the moment!");
					} else {
						sender.toDp().err("They're offline. Invite incinerated.");
						sender.getInvite().setInvite(sender.getInvite());
						sender.setInvite(sender);
					}
				} else {
					sender.toDp().err("You don't have any invites.");
				}
				
			break;
			
			case "saveArenas":

				main.update();
				sender.toDp().s("Updated!");
				
			break;
			
			case "deny":
				
				if (!sender.inGame()){
					sender.getInvite().toDp().err("Invite denied.");
					sender.getInvite().setInvite(sender.getInvite());
					sender.setInvite(sender);
					sender.toDp().s("Denied!");
				}
				
			break;
			
			case "arenalist":
				
				if (!perms(p, "wa.staff.admin")){ return; }
				
				String list = "";
				
				for (SpleefStorage game : data.values()){
					if (game.type().equals(SpleefDataType.GAME)){
						list = list +( list.equals("") ? "&b" + game.name().replace("GAME ", "") : "&7, &b" + game.name().replace("GAME ", ""));
					}
				}
				
				sender.toDp().s(list);
				
			break;
			
			case "score":
				
				if (a(sender, args.length, 2)){
					if (main.main.api.doesPartialPlayerExist(args[1])){
						try {
							sender.toDp().s("Total Score: " + main.main.api.getDivPlayer(args[1]).getInt(SpleefPlayerData.TOTAL_SCORE));
							sender.toDp().s("Game Wins: " + main.main.api.getDivPlayer(args[1]).getInt(SpleefPlayerData.TOTAL_WINS));
							sender.toDp().s("Game Losses: " + main.main.api.getDivPlayer(args[1]).getInt(SpleefPlayerData.TOTAL_LOSSES));
						} catch (Exception e){
							sender.toDp().s("No score found!");
						}
					} else {
						sender.toDp().err("That player does not exist.");
					}
				}
				
			break;
			
			case "p1start": case "p2start":
				
				if (!perms(p, "wa.staff.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						Location l = p.getLocation();
						getSpleefGame(args[1]).putt((args[0].contains("p1") ? SpleefGameData.PLAYER_START_1 : SpleefGameData.PLAYER_START_2), l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
						sender.toDp().s("Updated &6" + args[0] + "&b!");
					} else {
						sender.toDp().err("That game does not exist.");
					}
				}
				
			break;
			
			case "addarena":
				
				if (!perms(p, "wa.staff.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					
					if (!doesGameExist(args[1])){
				
						if (main.main.we.getSelection(p) != null && main.main.we.getSelection(p) instanceof CuboidSelection){
							Selection sel = main.main.we.getSelection(p);
							
							if (sel.getHeight() <= 1){
								
								Vector max = sel.getMaximumPoint().toVector();
								Vector min = sel.getMinimumPoint().toVector();
								SpleefStorage game = new SpleefStorage(main.main, SpleefDataType.GAME, SpleefDataType.GAME.s() + " " + args[1]);
								game.put(SpleefGameData.MAX, p.getWorld().getName() + " " + max.getBlockX() + " " + max.getBlockY() + " " + max.getBlockZ());
								game.put(SpleefGameData.MIN, p.getWorld().getName() + " " + min.getBlockX() + " " + min.getBlockY() + " " + min.getBlockZ());
								game.put(SpleefGameData.MATERIAL, Material.SNOW_BLOCK.toString());
								game.toGame().setEnabled(false);
								data.put(game.name(), game);
								reset(game.toGame());
								
								sender.toDp().s("Added the arena named &6" + args[1] + "&b!");
								sender.toDp().s("Reminder, you must set all of the data & then run /spleef enable <name> to use this arena!");
								
							} else {
								sender.toDp().err("Your height must be 1!");
							}
							
						} else {
							sender.toDp().err("You must select a flat rectangle with WorldEdit.");
						}
						
					} else {
						sender.toDp().err("That arena already exists.");
					}
				}
				
			break;
			
			case "remarena":
				
				if (!perms(p, "wa.staff.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						data.remove(SpleefDataType.GAME.s() + " " + args[1]);
						new File("./plugins/Spleef/game/GAME" + args[1] + ".yml").delete();
						sender.toDp().s("Deleted!");
					} else {
						sender.toDp().err("That arena does not exist!");
					}
				}
				
			break;
		}
		
		if (rem){
			data.remove(p.getName());
		}
	}
	
	public void reset(SpleefGame game){
		
		String[] max = ((String) game.gett(SpleefGameData.MAX)).split(" ");
		String[] min = ((String) game.gett(SpleefGameData.MIN)).split(" ");
		
		Vector maxLoc = new Location(Bukkit.getWorld(max[0]), Integer.parseInt(max[1]), Integer.parseInt(max[2]), Integer.parseInt(max[3])).toVector();
		Vector minLoc = new Location(Bukkit.getWorld(min[0]), Integer.parseInt(min[1]), Integer.parseInt(min[2]), Integer.parseInt(min[3])).toVector();
		
		for (int x = minLoc.getBlockX(); x <= maxLoc.getBlockX(); x++){
			for (int z = minLoc.getBlockZ(); z <= maxLoc.getBlockZ(); z++){
				new Location(Bukkit.getWorld(max[0]), x, maxLoc.getBlockY(), z).getBlock().setType(Material.valueOf((String) game.gett(SpleefGameData.MATERIAL)));
			}
		}
	}
	
	private boolean a(SpleefPlayer sender, int supplied, int needed){
		if (supplied >= needed){
			return true;
		}
		sender.toDp().err("Invalid arg count.");
		return false;
	}
	
	private boolean perms(Player p, String perm){
		return main.main.api.perms(p, perm, true);
	}
}