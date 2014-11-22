package com.github.lyokofirelyte.Elysian.Events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.PlayerMoneyChangeEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyChannel;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityStorage;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;

public class ElyLogger implements Listener, Runnable, AutoRegister {
	
	private Elysian main;
	
	public ElyLogger(Elysian i){
		main = i;
	}
	
	public List<Material> protectedMats = Arrays.asList(
		Material.CHEST,
		Material.FURNACE,
		Material.BREWING_STAND,
		Material.ENCHANTMENT_TABLE,
		Material.BURNING_FURNACE,
		Material.ENDER_CHEST,
		Material.HOPPER,
		Material.JUKEBOX,
		Material.DISPENSER,
		Material.DROPPER,
		Material.BEACON,
		Material.TRAPPED_CHEST
	);
	
	private Map<String, List<String>> recent = new THashMap<String, List<String>>();
	private Map<Player, Map<String, Integer>> warnings = new THashMap<Player, Map<String, Integer>>();
	private Map<Player, Map<String, Integer>> lightLevels = new THashMap<Player, Map<String, Integer>>();
	
	@Override
	public void run(){
		if (main.queue.size() > 0){
			initLog(new THashMap<Location, List<List<String>>>(main.queue));
			main.queue = new THashMap<Location, List<List<String>>>();
			recent = new THashMap<String, List<String>>();
		}
		if (warnings.size() > 0){
			Map<Player, Map<String, Integer>> warningsCurrent = new THashMap<Player, Map<String, Integer>>(warnings);
			Map<Player, Map<String, Integer>> lightLevelsCurrent = new THashMap<Player, Map<String, Integer>>(lightLevels);
			for (Player p : warningsCurrent.keySet()){
				for (String mat : warningsCurrent.get(p).keySet()){
					if (p != null && p.isOnline()){			
						double li = (lightLevelsCurrent.get(p).get(mat)/15.0)*100;
						String light = (li + "").length() > 5 ? (li + "").substring(0, 5) : li + "";
						ElyChannel.STAFF.send("&6System", p.getDisplayName() + " &c&ofound " + warningsCurrent.get(p).get(mat) + " &6&o" + mat + " &c&o@ &6&o" + light + "% &c&olight", main.api);
					}
				}
			}
			warnings = new THashMap<Player, Map<String, Integer>>();
			lightLevels = new THashMap<Player, Map<String, Integer>>();
		}
	}
	
	public boolean isNatural(Location l){
		
		String loc = l.toVector().getBlockX() + "," + l.toVector().getBlockZ();
		float x = Precision.round(l.toVector().getBlockX(), -3);
		float z = Precision.round(l.toVector().getBlockZ(), -3);
		int y = l.toVector().getBlockY();
		File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
		List<String> results = new ArrayList<String>();
		
		if (file.exists()){
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
			results = new ArrayList<String>(yaml.getStringList("History." + l.getWorld().getName() + "." + y));
		}

		return results.size() == 0;
	}
	
	public void addToQue(Location l, String player, String action, String eventName, String whatItWas, String whatItIs){
		if (!main.queue.containsKey(l)){
			List<List<String>> s = new ArrayList<List<String>>();
			main.queue.put(l, s);
		}
		main.queue.get(l).add(Arrays.asList(player, action, eventName, whatItWas, whatItIs));
	}
	
	public void removeFromQue(Location l){
		if (main.queue.containsKey(l)){
			main.queue.remove(l);
		}
	}
	
	private void addToRecent(String p, String thing){
		if (!recent.containsKey(p)){
			recent.put(p, new ArrayList<String>());
		}
		recent.get(p).add(thing);
	}
	
	@EventHandler
	public void onMoney(PlayerMoneyChangeEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		String[] bal = dp.getStr(DPI.DAILY_BALANCE).equals("none") ? new String[]{ 0 + "", 0 + ""} : dp.getStr(DPI.DAILY_BALANCE).split(" ");
		int amount = Integer.parseInt(bal[0]);
		long when = Long.parseLong(bal[1]);
		
		if (when >= System.currentTimeMillis() - (24*60*60*1000)){
			if (e.isIncrease()){
				amount += e.getDifference();
				if (amount >= 200000){
					ElyChannel.STAFF.send("&6System", e.getPlayer().getDisplayName() + " &chas made over 200k today!", main.api);
					dp.set(DPI.DAILY_BALANCE, 0 + " " + when);
				} else {
					dp.set(DPI.DAILY_BALANCE, amount + " " + when);
				}
			}
		} else {
			dp.set(DPI.DAILY_BALANCE, e.getDifference() + " " + System.currentTimeMillis());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getClickedBlock() != null && main.api.getDivPlayer(e.getPlayer()).getBool(DPI.LOGGER) && e.getPlayer().getItemInHand().getType().equals(Material.ENDER_PORTAL_FRAME)){
			
			lookup(e.getPlayer(), e.getClickedBlock().getLocation());
			e.setCancelled(true);
			
		} else if (e.getClickedBlock() != null && !main.api.getDivPlayer(e.getPlayer()).getStr(DPI.CHEST_MODE).equals("none") && protectedMats.contains(e.getClickedBlock().getType())){
		
			DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
			Location l = e.getClickedBlock().getLocation();
			String loc = l.getWorld().getName() + " " + l.toVector().getBlockX() + " " + l.toVector().getBlockY() + " " + l.toVector().getBlockZ();
			e.setCancelled(true);
			
			List<String> names = dp.getList(DPI.CHEST_NAMES);
			List<String> failedNames = new ArrayList<String>();
			String failLine = "";
			
			if (!dp.getList(DPI.OWNED_CHESTS).contains(loc) && !main.api.perms(e.getPlayer(), "wa.staff.mod2", true) && !dp.getStr(DPI.CHEST_MODE).equals("view") && !dp.getStr(DPI.CHEST_MODE).equals("release")){
				main.s(e.getPlayer(), "none", "&c&oThat is not yours to modify!");
				return;
			}
			
			for (String s : names){
				if (!main.api.doesPartialPlayerExist(s) && !s.equals("view") && !s.equals("release")){
					failedNames.add(s);
				} else if (dp.getStr(DPI.CHEST_MODE).equals("add") && main.api.getDivPlayer(s).getList(DPI.OWNED_CHESTS).contains(loc)){
					failedNames.add(s);
				} else if (dp.getStr(DPI.CHEST_MODE).equals("remove") && !main.api.getDivPlayer(s).getList(DPI.OWNED_CHESTS).contains(loc)){
					failedNames.add(s);
				} else if (s.equals("view")){
					String users = "";
					for (DivinityStorage d : main.divinity.api.getAllPlayers()){
						if (d.getList(DPI.OWNED_CHESTS).contains(loc)){
							users = users + "&3" + d.name() + " ";
						}
					}
					users = users.trim();
					main.s(e.getPlayer(), " ", users.replaceAll(" ", "&b, &3"));
					dp.set(DPI.CHEST_MODE, "none");
					dp.set(DPI.CHEST_NAMES, new ArrayList<String>());
					return;
				} else if (s.equals("release")){
					for (DivinityStorage d : main.divinity.api.getAllPlayers()){
						if (d.getList(DPI.OWNED_CHESTS).contains(loc)){
							d.getList(DPI.OWNED_CHESTS).remove(loc);
						}
					}
					main.s(e.getPlayer(), "Released to the public!");
					return;
				}
			}
			
			for (String s : failedNames){
				names.remove(s);
				failLine = failLine + "&c" + s + " ";
			}
			
			failLine = failLine.trim();
			failLine = failLine.replaceAll(" ", "&6, &c");
			
			for (String s : names){
				DivinityPlayer toModify = main.api.getDivPlayer(s);
				if (dp.getStr(DPI.CHEST_MODE).equals("add")){
					toModify.getList(DPI.OWNED_CHESTS).add(loc);
				} else {
					toModify.getList(DPI.OWNED_CHESTS).remove(loc);
				}
			}
			
			if (!failLine.equals("")){
				main.s(e.getPlayer(), "none", "&c&oFailed on adding some people! (Are they already added?)");
				main.s(e.getPlayer(), "none", failLine);
			} else {
				main.s(e.getPlayer(), "none", "All users modified successfully.");
			}
			
			dp.set(DPI.CHEST_MODE, "none");
			e.getPlayer().getWorld().playEffect(e.getClickedBlock().getLocation(), Effect.ENDER_SIGNAL, 3);
			
		} else if (e.getClickedBlock() != null && protectedMats.contains(e.getClickedBlock().getType())){
			
			DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
			Location l = e.getClickedBlock().getLocation();
			String loc = l.getWorld().getName() + " " + l.toVector().getBlockX() + " " + l.toVector().getBlockY() + " " + l.toVector().getBlockZ();
			
			if (dp.getStr(DPI.DEATH_CHEST_LOC).equals(loc)){
				
				for (ItemStack i : dp.getStack(DPI.DEATH_CHEST_INV)){
					if (i != null && !i.getType().equals(Material.AIR) && e.getPlayer().getInventory().firstEmpty() != -1){
						e.getPlayer().getInventory().addItem(i);
					} else if (i != null && !i.getType().equals(Material.AIR)){
						e.getPlayer().getWorld().dropItemNaturally(l, i);
					}
				}
				
				e.getPlayer().updateInventory();
				e.getClickedBlock().setType(Material.AIR);
				
				dp.set(DPI.DEATH_CHEST_INV, "none");
				dp.set(DPI.DEATH_CHEST_LOC, "none");
				
			} else {
				
				if (!dp.getList(DPI.OWNED_CHESTS).contains(loc) && !main.api.perms(e.getPlayer(), "wa.staff.mod2", true)){
					for (DivinityStorage DP : main.divinity.api.getAllPlayers()){
						if (DP.getList(DPI.OWNED_CHESTS).contains(loc)){
							e.setCancelled(true);
							main.s(e.getPlayer(), "none", "&c&oThat is not your storage unit!");
							return;
						}
					}
				}
			}
			
		} else if (e.getClickedBlock() != null && !e.getClickedBlock().getType().equals(Material.AIR)){
			
			String player = e.getPlayer().getName();
			
			switch (e.getMaterial()){
			
				default: break;
			
				case WOOD_DOOR: case FENCE_GATE: case TRAP_DOOR: case LEVER:
				
					if (recent.containsKey(player)){
						if (recent.get(e.getPlayer().getName()).contains("door")){
							break;
						}
					}
					
					addToRecent(player, "door");
					addToQue(e.getClickedBlock().getLocation(), "&b" + player, "&3used &b" + e.getClickedBlock().getType().toString().toLowerCase(), "interact", "DOOR", "DOOR");
					
				break;
				
			}
		}
	}
	
	@DivCommand(aliases = {"chest"}, desc = "Elysian Chest Protection Command", help = "/chest help", min = 1, player = true)
	public void onChestCommand(final Player p, final String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		List<String> names = new ArrayList<String>();
		
		switch (args[0]){
		
			case "add": case "remove":
				dp.set(DPI.CHEST_MODE, args[0]);
				for (int i = 1; i < args.length; i++){
					names.add(args[i]);
				}
				dp.set(DPI.CHEST_NAMES, names);
				main.s(p, "none", "Left-click on a storage unit to " + args[0] + " the names.");
				p.setGameMode(GameMode.SURVIVAL);
			break;
			
			case "help":
				main.s(p, "none", "/chest add/remove player1 player2 player3 etc");
				main.s(p, "none", "/chest view");
				main.s(p, "none", "/chest massrelease <radius>");
				main.s(p, "none", "/chest release");
				main.s(p, "none", "/chest cancel");
			break;
			
			case "view":
				dp.set(DPI.CHEST_MODE, args[0]);
				dp.set(DPI.CHEST_NAMES, "view");
				main.s(p, "none", "Left-click on a storage unit to view the owners.");
				p.setGameMode(GameMode.SURVIVAL);
			break;
			
			case "release":
				
				dp.set(DPI.CHEST_MODE, args[0]);
				dp.set(DPI.CHEST_NAMES, "release");
				main.s(p, "Left-click a chest to make it public.");
				p.setGameMode(GameMode.SURVIVAL);
				
			break;
			
			case "massrelease":
				
				if (main.api.perms(p, "wa.staff.admin", false)){
					if (args.length == 2 && DivinityUtilsModule.isInteger(args[1])){
						String released = "";
						int radius = Integer.parseInt(args[1]) <= 20 ? Integer.parseInt(args[1]) : 5;
						for (Location l : DivinityUtilsModule.circle(p.getLocation(), radius, radius, false, false, 0)){
							if (protectedMats.contains(l.getBlock().getType())){
								String loc = l.getWorld().getName() + " " + l.toVector().getBlockX() + " " + l.toVector().getBlockY() + " " + l.toVector().getBlockZ();
								for (DivinityStorage div : main.divinity.api.getAllPlayers()){
									if (div.getList(DPI.OWNED_CHESTS).contains(loc)){
										div.getList(DPI.OWNED_CHESTS).remove(loc);
										released = released.equals("") ? div.getStr(DPI.DISPLAY_NAME) : released + "&6, " + div.getStr(DPI.DISPLAY_NAME);
									}
								}
							}
						}
						dp.s("Released the following chests: ");
						dp.s(released.equals("") ? "&7&oNone found!" : released);
					} else {
						dp.err("Invalid args! /chest massrelease <radius>");
					}
				}
				
			break;
			
			case "cancel":
				dp.set(DPI.CHEST_MODE, "none");
				main.s(p, "none", "Action cancelled.");
			break;
			
			/*case "folder":TODO
				dp.set(DPI.CHEST_MODE, "folder " + args[1]);
			break;*/
		}
	}
	
	//location, player, message, event, what it was, what it now is
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent e){

		Material mat = e.getBlock().getType();
		String matName = mat.name().toLowerCase();
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (!dp.getList(DPI.PERMS).contains("wa.member") || dp.getBool(DPI.IS_STAFF_TP)){
			e.setCancelled(true);
			return;
		}
		
		if (protectedMats.contains(mat) || e.getBlock() instanceof DoubleChest || e.getBlock() instanceof Chest){
			Location l = e.getBlock().getLocation();
			String loc = l.getWorld().getName() + " " + l.toVector().getBlockX() + " " + l.toVector().getBlockY() + " " + l.toVector().getBlockZ();
			if (!dp.getList(DPI.OWNED_CHESTS).contains(loc) && !main.api.perms(e.getPlayer(), "wa.staff.mod2", false)){
				for (DivinityStorage DP : main.divinity.api.getAllPlayers()){
					if (DP.getList(DPI.OWNED_CHESTS).contains(loc)){
						e.setCancelled(true);
						main.s(e.getPlayer(), "none", "&c&oThat is not your storage unit!");
						ElyChannel.STAFF.send("&6System", e.getPlayer().getDisplayName() + " &cattempted to destroy a storage unit!", main.api);
						return;
					}
				}
			} else {
				for (DivinityStorage DP : main.divinity.api.getAllPlayers()){
					if (DP.getList(DPI.OWNED_CHESTS).contains(loc)){
						DP.getList(DPI.OWNED_CHESTS).remove(loc);
					}
				}
			}
		}
		
		if (e.getBlock().getWorld().getName().equals("world")){
			addToQue(e.getBlock().getLocation(), "&b" + e.getPlayer().getName(), "&cdestroyed &b" + matName, "break", matName + "split" + e.getBlock().getData() + "split" + (new Integer(Integer.parseInt("" + e.getPlayer().getLocation().getBlock().getLightLevel()))), "AIRsplit0");
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		
		if (!main.api.getDivPlayer(e.getPlayer()).getList(DPI.PERMS).contains("wa.member")){
			e.setCancelled(true);
			return;
		}
		
		String matName = e.getBlock().getType().toString().toLowerCase();
		
		if (e.getBlock() != null && main.api.getDivPlayer(e.getPlayer()).getBool(DPI.LOGGER) && e.getPlayer().getItemInHand().getType().equals(Material.ENDER_PORTAL_FRAME)){
			lookup(e.getPlayer(), e.getBlock().getLocation());
			e.setCancelled(true);
		} else if (e.getBlock().getWorld().getName().equals("world")){
			addToQue(e.getBlock().getLocation(), "&b" + e.getPlayer().getName(), "&aplaced &b" + e.getBlock().getType().toString().toLowerCase(), "place", "AIRsplit0", matName + "split" + e.getBlock().getData());
		}
		
		if (main.api.getDivPlayer(e.getPlayer()).getBool(DPI.IS_STAFF_TP)){
			e.setCancelled(true);
			return;
		}
		
		if (!e.getPlayer().getWorld().getName().equals("WACP") && protectedMats.contains(e.getBlock().getType())){
			main.s(e.getPlayer(), "none", "This storage unit is now protected. Allow friend access with /chest add <player>.");
			Location l = e.getBlock().getLocation();
			String loc = l.getWorld().getName() + " " + l.toVector().getBlockX() + " " + l.toVector().getBlockY() + " " + l.toVector().getBlockZ();
			main.api.getDivPlayer(e.getPlayer()).getList(DPI.OWNED_CHESTS).add(loc);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		
		if (main.api.getDivPlayer(e.getPlayer()).getBool(DPI.IS_STAFF_TP)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		
		if (main.api.getDivPlayer(e.getPlayer()).getBool(DPI.IS_STAFF_TP)){
			e.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		Entity e = event.getEntity();
		int amt = 0;

	    for (Entity entity : e.getNearbyEntities(7.0D, 7.0D, 7.0D)) {
	    	if ((entity.getType().equals(EntityType.MINECART_TNT)) || (entity.getType().equals(EntityType.PRIMED_TNT))){
	    		event.setCancelled(true);
	    		break;
	    	}
	    }

	    for (Block block : event.blockList()){
	    	
	    	if (protectedMats.contains(block.getType())){
	    		event.setCancelled(true);
	    	}
	    	
	    	if (block.getType().equals(Material.TNT)){
	    		amt++;
	    		if (amt >= 3){
	    			event.setCancelled(true);
	    		}
	    		break;
	    	}
	    	
			addToQue(block.getLocation(), "&benvironment-explosion", "&cblew up &b" + block.getType().name().toLowerCase(), "break", block.getType().name().toLowerCase() + "split" + block.getData(), "AIRsplit0");
	    }
	}
	
	@DivCommand(aliases = {"log", "logger"}, perm = "wa.staff.intern", desc = "Elysian Logging Command", help = "/log help", player = true)
	public void onLogCommand(final Player p, final String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinitySystem system = main.api.getDivSystem();
		
		if (system.getBool(DPI.ROLLBACK_IN_PROGRESS)){
			main.s(p, "&c&oRollback in progress - command blocked to prevent file corruption.");
			return;
		}
		
		if (args.length == 0){
			
			dp.set(DPI.LOGGER, !dp.getBool(DPI.LOGGER));
			
			if (dp.getBool(DPI.LOGGER)){
				
				main.s(p, "none", "&oLogger activated! (Use ender portal frame)");
				
				if (p.getItemInHand() == null || p.getItemInHand().getType().equals(Material.AIR)){
					p.setItemInHand(new ItemStack(Material.ENDER_PORTAL_FRAME, 1));
				} else {
					p.getInventory().addItem(new ItemStack(Material.ENDER_PORTAL_FRAME, 1));
				}
				
			} else {
				main.s(p, "none", "&oLogger deactivated!");
				p.getInventory().removeItem(new ItemStack(Material.ENDER_PORTAL_FRAME));
			}
			
		} else if (DivinityUtilsModule.isInteger(args[0])){
			
			try {
				
				int base = Integer.parseInt(args[0])-1;
				List<String> results = dp.getList(DPI.LOGGER_RESULTS);
				main.s(p, "none", "Viewing page &6" + (base+1) + "&b. SysTime: &7" + DivinityUtilsModule.getTime(System.currentTimeMillis()));

				for (int x = 5*base; x < (5*base)+5; x++){
					if (results.size()-1 >= x){
						main.s(p, "none", results.get(x));
					}
				}
				
			} catch (Exception e){
				main.s(p, "none", "&4No results found!");
			}
			
		} else {
			
			switch (args[0]){
			
				case "help":
					
					main.s(p, "none", "/log <page number>");
					main.s(p, "none", "/log rollback <radius> <time> [player]");
					main.s(p, "none", "Time examples: 40s, 10m, 2h, 1d, 2w. Only use one time measurement at a time.");
					
				break;
				
				case "rollback": //log rollback <radius> 5m/5h [player]
					
					main.api.getDivSystem().set(DPI.ROLLBACK_IN_PROGRESS, true);
					
					new Thread(new Runnable(){ public void run(){
					
						String timeType = "";
						int radius = 0;
						long time = 0;
						
						try {
							
							if (DivinityUtilsModule.isInteger(args[1])){
								
								main.s(p, "none", "&oRollback started...");
								
								radius = Integer.parseInt(args[1]);
								timeType = args[2].substring(args[2].length()-1);
								time = Long.parseLong(args[2].replace(timeType, ""));
								
								switch (timeType){
									case "s": time = time*1000; break;
									case "m": time = time*60*1000; break;
									case "h": time = time*60*60*1000; break;
									case "d": time = time*24*60*60*1000; break;
									case "w": time = time*7*24*60*60*1000; break;
									default: main.s(p, "none", "s, m, h, d, or w"); return;
								}
								
								time = System.currentTimeMillis() - time;
								Location pLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()-radius, p.getLocation().getZ());
								
								final List<String> locs = DivinityUtilsModule.strCircle(pLoc, radius, radius*2, false, false, 0);
								final Map<Location, Material> newBlocks = new THashMap<Location, Material>();
								final Map<Location, Byte> newBlockIds = new THashMap<Location, Byte>();
								
								Map<String, YamlConfiguration> filesToCheck = new THashMap<String, YamlConfiguration>();
								Map<String, Map<Integer, List<String>>> results = new THashMap<String, Map<Integer, List<String>>>();
								List<String> allowedEvents = Arrays.asList("break", "place");
								
								for (String l : locs){
									String loc = l.split(" ")[1] + "," + l.split(" ")[3];
									float x = Precision.round(Integer.parseInt(l.split(" ")[1]), -3);
									float z = Precision.round(Integer.parseInt(l.split(" ")[3]), -3);
									File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
									
									if (file.exists()){
										filesToCheck.put(loc, YamlConfiguration.loadConfiguration(file));
										results.put(loc, new THashMap<Integer, List<String>>());
									}
								}
								
								if (results.size() <= 0){
									main.s(p, "none", "&oThere was nothing to roll back.");
									return;
								}
								
								for (String yaml : filesToCheck.keySet()){
									for (int x = 0; x < 257; x++){
										if (x > p.getLocation().getY() - radius && x < p.getLocation().getY() + radius){
											results.get(yaml).put(x, new ArrayList<String>());
											for (String s : filesToCheck.get(yaml).getStringList("History." + p.getWorld().getName() + "." + x)){
												if (allowedEvents.contains(s.split("%")[3].toLowerCase())){
													if (args.length == 4){
														if (s.split("%")[0].toLowerCase().contains(args[3].toLowerCase())){
															results.get(yaml).get(x).add(s);
														}
													} else {
														results.get(yaml).get(x).add(s);
													}
												}
											}
										}
									}
								}
								
								//player, message, time, event, what it was, what it now is, y coord
								
								for (String loc : results.keySet()){
									for (int yCoord : results.get(loc).keySet()){
										Map<Long, String> resultTimes = new THashMap<Long, String>();
										Map<Long, String> finalTimes = new THashMap<Long, String>();
										List<Long> times = new ArrayList<Long>();
										
										for (String result : results.get(loc).get(yCoord)){
											resultTimes.put(Long.parseLong(result.split("%")[2]), result);
										}
										
										for (Long l : resultTimes.keySet()){
											times.add(l);
										}
										
										Collections.sort(times);
										Collections.reverse(times);
										
										int loops = 1;
										
										for (Long daTime : times){
											finalTimes.put(daTime, resultTimes.get(daTime));
										}
										
										for (String result : finalTimes.values()){
											String[] ss = result.split("%");
											if (Long.parseLong(ss[2]) <= time){
												newBlocks.put(new Location(p.getWorld(), Double.parseDouble(loc.split(",")[0]), yCoord, Double.parseDouble(loc.split(",")[1])), Material.valueOf((ss[5].split("split")[0]).toUpperCase()));
												newBlockIds.put(new Location(p.getWorld(), Double.parseDouble(loc.split(",")[0]), yCoord, Double.parseDouble(loc.split(",")[1])), Byte.parseByte(ss[5].split("split")[1]));
												break;
											} else if (loops == finalTimes.size()){
												newBlocks.put(new Location(p.getWorld(), Double.parseDouble(loc.split(",")[0]), yCoord, Double.parseDouble(loc.split(",")[1])), Material.valueOf((ss[4].split("split")[0]).toUpperCase()));
												newBlockIds.put(new Location(p.getWorld(), Double.parseDouble(loc.split(",")[0]), yCoord, Double.parseDouble(loc.split(",")[1])), Byte.parseByte(ss[4].split("split")[1]));
											}
											loops++;
										}
									}
								}

								final List<Location> finalLocs = new ArrayList<Location>();
								final DivinitySystem system = main.api.getDivSystem();
								system.set(DPI.EXP, 0);
								
								for (Location l : newBlocks.keySet()){
									finalLocs.add(l);
								}
								
								if (finalLocs.size() <= 0){
									main.s(p, "none", "Nothing could be found to rollback.");
									return;
								}
								
								system.set(DPI.HOME, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){ @SuppressWarnings("deprecation")
								public void run(){
									
									for (int x = 0; x < 5; x++){
										
										if (finalLocs.size() > x){
										
											Material oldBlock = finalLocs.get(system.getInt(DPI.EXP)).getBlock().getType();
											Material newBlock = newBlocks.get(finalLocs.get(system.getInt(DPI.EXP)));
											byte newBlockId = newBlockIds.get(finalLocs.get(system.getInt(DPI.EXP)));
	
											if (!oldBlock.equals(newBlock)){
												addToQue(finalLocs.get(system.getInt(DPI.EXP)), "&b" + p.getName(), "&erolled back &b" + oldBlock.name().toLowerCase() + " -> " + newBlock.name().toLowerCase(), "rollback", oldBlock.name().toLowerCase(), newBlock.name().toLowerCase());
											}
											
											finalLocs.get(system.getInt(DPI.EXP)).getBlock().setTypeIdAndData(newBlock.getId(), newBlockId, true);
											system.set(DPI.EXP, system.getInt(DPI.EXP) + 1);
											
											if (system.getInt(DPI.EXP) >= finalLocs.size()){
												Bukkit.getScheduler().cancelTask(system.getInt(DPI.HOME));
												main.s(p, "none", "&oRollback completed.");
												main.api.getDivSystem().set(DPI.ROLLBACK_IN_PROGRESS, false);
												break;
											}
										}
									}

								}}, 1L, 1L));
								
							} else {
								main.s(p, "invalidNumber");
							}
							
						} catch (Exception e){
							e.printStackTrace();
							main.s(p, "none", "&cRollback failed! Invalid inputs.");
						}
						
					}}).start();
					
				break;
			}
		}
	}
	
	private void initLog(final Map<Location, List<List<String>>> map){
		new Thread(new Runnable(){ public void run(){
			for (Location l : map.keySet()){
				for (List<String> list : map.get(l)){
					try {
						log(l, list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
					} catch (Exception e){}
				}
			}
		}}).start();
	}
	
	private void lookup(final Player p, final Location l){
		
		if (main.api.getDivSystem().getBool(DPI.ROLLBACK_IN_PROGRESS)){
			main.s(p, "&c&oRollback in progress - command blocked to prevent file corruption.");
			return;
		}
		
		new Thread(new Runnable(){ public void run(){
		
			String loc = l.toVector().getBlockX() + "," + l.toVector().getBlockZ();
			float x = Precision.round(l.toVector().getBlockX(), -3);
			float z = Precision.round(l.toVector().getBlockZ(), -3);
			int y = l.toVector().getBlockY();
				
			File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
			
			if (!file.exists()){
				
				main.s(p, "none", "&4No data found!");
				
			} else {
				
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
				List<String> results = new ArrayList<String>(yaml.getStringList("History." + p.getWorld().getName() + "." + y));
				Collections.reverse(results);
				
				main.api.getDivPlayer(p).set(DPI.LOGGER_RESULTS, new ArrayList<String>());
				main.s(p, "none", "Viewing page &61&b/&6" + (Math.round(results.size()/5)+1) + "&b. SysTime: &7" + DivinityUtilsModule.getTime(System.currentTimeMillis()));
				
				for (String s : results){
					String[] ss = s.split("%");
					String time = DivinityUtilsModule.getTime(Long.parseLong(ss[2]));
					main.api.getDivPlayer(p).getList(DPI.LOGGER_RESULTS).add("&7" + time + " " + ss[0] + " " + ss[1]);
				}
				
				for (int g = 0; g < 5; g++){
					if (results.size() > g){
						String[] ss = results.get(g).split("%");
						main.s(p, "none", "&7" + DivinityUtilsModule.getTime(Long.parseLong(ss[2])) + " " + ss[0] + " " + ss[1]);
					}
				}
				
				if (results.size() >= 5){
					main.s(p, "none", "Type /log <number> to view a different page!");
				}
			}
				
		}}).start();
	}
	
	private void log(final Location l, final String player, final String action, final String eventName, final String whatWasIt, final String whatIsIt){
		
		String loc = l.toVector().getBlockX() + "," + l.toVector().getBlockZ();
		int y = l.toVector().getBlockY();
		float x = Precision.round(l.toVector().getBlockX(), -3);
		float z = Precision.round(l.toVector().getBlockZ(), -3);
			
		File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
			
		if (!file.exists()){
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (Exception ee){
				ee.printStackTrace();
			}
		}
			
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		List<String> history = new ArrayList<String>();	
		
		if (yaml.getStringList("History." + l.getWorld().getName() + "." + y) == null || yaml.getStringList("History." + l.getWorld().getName() + "." + y).equals("") || yaml.getStringList("History." + l.getWorld().getName() + "." + y).equals(new ArrayList<String>())){
			yaml.set("History." + l.getWorld().getName() + "." + y, history);
		}
		
		history = yaml.getStringList("History." + l.getWorld().getName() + "." + y);
		history.add(player + "%" + action + "%" + System.currentTimeMillis() + "%" + eventName + "%" + whatWasIt + "%" + whatIsIt);
		yaml.set("History." + l.getWorld().getName() + "." + y, history);

		if (history.size() <= 1){
			
			String[] wut = whatWasIt.split("split");
			
			switch (wut[0].toLowerCase()){
			
				case "diamond_ore": case "lapis_ore": case "emerald_ore": case "gold_ore": case "spawner":
					
					Player p = main.api.getPlayer(player.substring(2));
					
					if (!warnings.containsKey(p)){
						warnings.put(p, new THashMap<String, Integer>());
						lightLevels.put(p, new THashMap<String, Integer>());
					}
					
					String what = wut[0].toLowerCase();
					
					if (!warnings.get(p).containsKey(what)){
						warnings.get(p).put(what, 1);
						lightLevels.get(p).put(what, Integer.parseInt(wut[2]));
					} else {
						warnings.get(p).put(what, (warnings.get(p).get(what)+1));
					}
					
				break;
			}
		}
		
		recent = new THashMap<String, List<String>>();

		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}