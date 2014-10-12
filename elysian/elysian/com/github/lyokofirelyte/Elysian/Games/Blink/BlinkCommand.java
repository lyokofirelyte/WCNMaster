package com.github.lyokofirelyte.Elysian.Games.Blink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.Blink.Blink.BlinkGame;
import com.github.lyokofirelyte.Elysian.Games.Blink.Blink.BlinkSlot;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class BlinkCommand implements Listener {

	private Blink blink;
	private Elysian main;
	
	public BlinkCommand(Blink i) {
		blink = i;
		main = blink.main;
	}
	
	private Map<String, BlinkInfo> playerInfo = new THashMap<String, BlinkInfo>();
	
	@DivCommand(perm = "wa.staff.admin", aliases = {"blink"}, help = "/blink", desc = "Blink Setup Command", player = true)
	public void onBlink(Player p, final String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (args.length == 0){
			for (String s : new String[]{
				"/blink add <name>",
				"/blink rem <name>",
				"/blink addslot <name> <slotNumber> <purchasePrice> <cashOut>",
				"/blink remslot <name> <slotNumber>",
				"/blink info <name>",
				"/blink list"
			}){
				main.s(p, s);
			}
		} else {
			switch (args[0].toLowerCase()){
			
				case "info":
				
					if (blink.games.containsKey(args[1])){
						Location l = blink.getGame(args[1]).getSign(0).getBlock().getLocation();
						dp.s("Blink Game &6" + args[1]);
						dp.s("Location of first slot: &6" + l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
						dp.s("Total slots involved: &6" + blink.getGame(args[1]).getSlots().size());
					} else {
						dp.err("No game found!");
					}
					
				break;
			
				case "add":
					
					if (!blink.games.containsKey(args[1])){
						blink.createGame(args[1]);
						main.s(p, "Created. Now add some slots to it!");
					} else {
						dp.err("That game already exists.");
					}
					
				break;
				
				case "rem":
					
					if (blink.games.containsKey(args[1])){
						for (int i : blink.getGame(args[1]).getSlots().keySet()){
							blink.getGame(args[1]).getSign(i).getBlock().setType(Material.AIR);
						}
						blink.games.remove(args[1]);
						main.s(p, "Removed!");
					} else {
						dp.err("There's not a game with that name.");
					}
					
				break;
				
				case "addslot":
					
					if (blink.games.containsKey(args[1])){
						
						final List<Integer> ints = new ArrayList<Integer>();
						
						try {
							ints.add(Integer.parseInt(args[2]));
							ints.add(Integer.parseInt(args[3]));
							ints.add(Integer.parseInt(args[4]));
						} catch (Exception e){
							dp.err("Invalid numbers!");
							return;
						}
						
						playerInfo.put(p.getName(), new BlinkInfo(){
							
							private String name = args[1];
							private int slot = ints.get(0);
							private int purchase = ints.get(1);
							private int cashOut = ints.get(2);
							
							public String getName(){
								return name;
							}
							
							public int getPurchase(){
								return purchase;
							}
							
							public int getCashOut(){
								return cashOut;
							}
							
							public int getSlot(){
								return slot;
							}
						});
						
						dp.s("Alright, place a sign on the wall!");
						
					} else {
						dp.err("No game found!");
					}
					
				break;
				
				case "remslot":
					
					if (blink.games.containsKey(args[1])){
						if (DivinityUtilsModule.isInteger(args[2]) && blink.getGame(args[1]).hasSlot(Integer.parseInt(args[2]))){
							blink.getGame(args[1]).getSign(Integer.parseInt(args[2])).getBlock().setType(Material.AIR);
							blink.getGame(args[1]).remSlot(Integer.parseInt(args[2]));
							dp.s("Removed!");
						} else {
							dp.err("No slot found!");
						}
					} else {
						dp.err("No game found!");
					}
					
				break;
				
				case "list":
					
					String gameList = "";
					
					for (String game : blink.games.keySet()){
						gameList = gameList.equals("") ? "&3" + game : gameList + "&7, &3" + game;
					}
					
					dp.s(gameList);
					
				break;
			}
		}
	}
	
	private interface BlinkInfo {
		public String getName();
		public int getPurchase();
		public int getCashOut();
		public int getSlot();
	}
	
	private void checkSlots(BlinkGame game){
		
		int inUse = 0;
		
		for (BlinkSlot slot : game.getSlots().values()){
			if (slot.isInUse()){
				inUse++;
			}
		}
		
		if (inUse >= game.getSlots().size()){
			int rand = new Random().nextInt(game.getSlots().size());
			Sign sign = game.getSign(rand);
			sign.setLine(3, sign.getLine(3).replace(main.AS("&6"), main.AS("&a")));
			sign.update();
			BlinkSlot winner = game.getSlot(rand);
			DivinityPlayer winnerDP = main.api.getDivPlayer(winner.getPlayer());
			winnerDP.set(DPI.BALANCE, winnerDP.getInt(DPI.BALANCE) + winner.getCashOut());
			if (main.api.isOnline(winner.getPlayer())){
				winnerDP.s("You've won a blink! Added &6" + winner.getCashOut() + " &bto your account!");
			}
			main.api.schedule(this, "reset", 100L, "reset" + winner.getPlayer(), game);
		}
	}
	
	public void reset(BlinkGame game){
		for (int slot : game.getSlots().keySet()){
			game.getSlot(slot).setInUse(false);
			Sign sign = game.getSign(slot);
			try {
				sign.setLine(0, main.AS("&b\u2744 &3BLINK &b\u2744"));
				sign.setLine(1, main.AS("&a-> " + game.getSlot(slot).getCashOut()));
				sign.setLine(2, main.AS("&c<- " + game.getSlot(slot).getPurchasePrice()));
				sign.setLine(3, main.AS("&f[ open ]"));
				sign.update();
			} catch (Exception e){}
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().equals(Material.WALL_SIGN)){
			DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
			for (BlinkGame g : blink.games.values()){
				for (int slot : g.getSlots().keySet()){
					Location l = g.getSign(slot).getBlock().getLocation();
					Location clicked = e.getClickedBlock().getLocation();
					if (l.getWorld().getName().equals(clicked.getWorld().getName())){
						if (l.getBlockX() == clicked.getBlockX() && l.getBlockY() == clicked.getBlockY() && l.getBlockZ() == clicked.getBlockZ()){
							BlinkSlot finalSlot = g.getSlot(slot);
							if (!finalSlot.isInUse()){
								if (dp.getInt(DPI.BALANCE) >= finalSlot.getPurchasePrice()){
									finalSlot.setInUse(true);
									finalSlot.setPlayer(e.getPlayer().getName());
									String dispName = e.getPlayer().getDisplayName().length() >= 8 ? e.getPlayer().getDisplayName().substring(0, 8) : e.getPlayer().getDisplayName();
									Sign sign = g.getSign(slot);
									sign.setLine(3, main.AS("&6&o" + ChatColor.stripColor(main.AS(dispName))));
									sign.update();
									dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) - finalSlot.getPurchasePrice());
									checkSlots(g);
								} else {
									dp.err("You don't have enough money!");
								}
							} else {
								dp.err("This slot is already in use.");
							}
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onBreak(BlockBreakEvent e){
		if (e.getBlock().getType().equals(Material.WALL_SIGN)){
			for (BlinkGame g : blink.games.values()){
				for (int slot : g.getSlots().keySet()){
					Location l = g.getSign(slot).getBlock().getLocation();
					Location clicked = e.getBlock().getLocation();
					if (l.getWorld().getName().equals(clicked.getWorld().getName())){
						if (l.getBlockX() == clicked.getBlockX() && l.getBlockY() == clicked.getBlockY() && l.getBlockZ() == clicked.getBlockZ()){
							main.s(e.getPlayer(), "&c&oYou can't destroy a blink slot. Use /blink remslot.");
							e.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent e){
		
		if (e.getBlock().getType().equals(Material.WALL_SIGN)){
			if (main.api.perms(e.getPlayer(), "wa.staff.admin", true)){
				if (playerInfo.containsKey(e.getPlayer().getName())){
					BlinkInfo info = playerInfo.get(e.getPlayer().getName());
					blink.getGame(info.getName()).addSlot(info.getSlot(), info.getPurchase(), info.getCashOut(), e.getBlock().getLocation());
					modSign(e.getPlayer(), info);
					main.s(e.getPlayer(), "Slot added! :D");
				}
			}
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onSign(SignChangeEvent e){
		if (playerInfo.containsKey(e.getPlayer().getName())){
			playerInfo.remove(e.getPlayer().getName());
			e.setCancelled(true);
		}
	}
	
	private void modSign(Player p, BlinkInfo info){
		
		Sign sign = blink.getGame(info.getName()).getSign(info.getSlot());
		
		try {
			sign.setLine(0, main.AS("&b\u2744 &3BLINK &b\u2744"));
			sign.setLine(1, main.AS("&a-> " + info.getCashOut()));
			sign.setLine(2, main.AS("&c<- " + info.getPurchase()));
			sign.setLine(3, main.AS("&f[ open ]"));
			sign.update();
		} catch (Exception e){
			main.s(p, "&c&oSign not found!");
		}
	}
}