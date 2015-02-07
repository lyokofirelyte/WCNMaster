package com.github.lyokofirelyte.Elysian.Commands;

import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.DRF;
import com.github.lyokofirelyte.Spectral.DataTypes.DRI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyChannel;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRegion;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityStorage;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.RegionSelector;

public class ElyProtect implements Listener, AutoRegister {

	private Elysian main;
	private String signText = "&3/&f*&3/ &bToggle &3/&f*&3/";
	public ElyProtect(Elysian i){
		main = i;
	}
	
	List<String> errorMessages = Arrays.asList(
		"&c&oI have no idea what that is. Try &6&o/ely help&c&o.",
		"&c&oWhat? That's not right. Try &6&o/ely help&c&o.",
		"&c&oWe don't have that here. See &6&o/ely help&c&o.",
		"&c&oEverytime you type in an invalid command, we kill a hostage.",
		"&c&oI too love to make up my own commands. See &6&o/ely help&c&o.",
		"&c&oCommand not found. Did you mean &6&o/kick WinneonSword &c&o?",
		"&c&oYour unknown command level is now 99.",
		"&c&oHelp, I'm stuck in an error message factory!",
		"&c&oCommand not valid. I assume you're a bad speller?",
		"&c&oI'm afraid I can't do that, Dave. See &6&o/ely help&c&o.",
		"&c&oOut of all the possible commands you choose an invalid one.",
		"&c&oYou tried. &6&o/ely help&c&o.",
		"&b&oThis is a blue, misleading error message. &6&o/ely help&b&o.",
		"&a&oSuccess! You typed an invalid command. &6&o/ely help&a&o.",
		"&c&oI'm ashamed of you for trying that. &6&o/ely help&c&o.",
		"&c&oNo, I will not perform that command. &6&o/ely help&c&o.",
		"&c&oAnd the invalid command award goes to...",
		"&c&oGo fish. &6&o/ely help&c&o.",
		"&c&oI'll do a lot of things, but I won't do that!",
		"&6&o/ely helpmepleaseIdontknowwhatImdoing",
		"&c&oThere's a time and place for everything, but not now!",
		"&c&oIf only that was really a command... &7&o/ely help&c&o.",
		"&c&oHelp me help you help us all, by typing &6&o/ely help&c&o."
	);
	
	@EventHandler
	public void onSignChang(SignChangeEvent e){
		
		if(main.api.perms(e.getPlayer(), "wa.staff.mod", true)){
			if(e.getLine(0).equalsIgnoreCase("toggle")){
				String region = isInAnyRegion(e.getBlock().getLocation());

				if(e.getLine(1) != null && !e.getLine(1).equals("") && !region.equals("none")){
					boolean found = false;

					for(DRF d: DRF.values()){
						if(d.toString().equals(ChatColor.stripColor(e.getLine(1)).toUpperCase())){
							found = true;
						}
					}
					
					if (found){
						e.setLine(0, main.AS(signText));
						e.setLine(1, "&6" + e.getLine(1).toUpperCase());
						
					} else {
						e.setLine(0, main.AS(signText));
						e.setLine(1, main.AS("&c&oNot found!"));
					}
				}else{
					e.setLine(0, main.AS(signText));
					e.setLine(1, main.AS("&4Invalid!"));
				}
			}
			
			
		}
		
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		
		String result = isInAnyRegion(e.getBlock().getLocation());
		
		if (hasFlag(result, DRF.BLOCK_BREAK)){
			if (!hasRegionPerms(e.getPlayer(), result)){
				e.setCancelled(true);
				main.s(e.getPlayer(), "&c&oYou are not authorized to build at &6" + result + "&c&o.");
			}
		}
		
		if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE) && e.getPlayer().getItemInHand().getType().equals(Material.BLAZE_ROD)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFoodLevel(FoodLevelChangeEvent e){
		
		if (e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			String result = isInAnyRegion(e.getEntity().getLocation());
			
			if (hasFlag(result, DRF.TAKE_DAMAGE) || main.api.getDivPlayer(p).getBool(DPI.IN_GAME)){
				if(e.getFoodLevel() < p.getFoodLevel()){
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		
		String result = isInAnyRegion(e.getBlock().getLocation());
		
		if (hasFlag(result, DRF.BLOCK_PLACE)){
			if (!hasRegionPerms(e.getPlayer(), result)){
				e.setCancelled(true);
				main.s(e.getPlayer(), "&c&oYou are not authorized to place at &6" + result + "&c&o.");
			}
		}
	}
	
	@EventHandler (priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent e){
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		String result = isInAnyRegion(p.getLocation());
		Location l = e.getClickedBlock() != null ? e.getClickedBlock().getLocation() : e.getPlayer().getLocation();
		
		if (hasFlag(result, DRF.INTERACT)){
			if (!e.getAction().toString().contains("AIR") && !hasRegionPerms(p, result)){
				e.setCancelled(true);
			}
		}
		
		if (p.getGameMode().equals(GameMode.CREATIVE) && p.getItemInHand() != null && p.getItemInHand().getType().equals(Material.BLAZE_ROD)){
			
			RegionSelector sel = main.we.getSession(p).getRegionSelector(main.we.wrapPlayer(p).getWorld());
			com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
			
			if (e.getAction() == Action.LEFT_CLICK_BLOCK){
				sel.selectPrimary(v);
				main.s(p, "Selected first position!");
			} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
				sel.selectSecondary(v);
				main.s(p, "Selected second position!");
			}
		}else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getState() instanceof Sign){
			Sign s = (Sign) e.getClickedBlock().getState();

			if(s.getLine(0).equals(main.AS(signText))){
				String toggle = ChatColor.stripColor(s.getLine(1)).toUpperCase();
				String region = isInAnyRegion(e.getClickedBlock().getLocation());
				
				if(!hasRegionPerms(p, region)){
					dp.err("You don't have permission!");
					return;
				}

				if (!region.equals("none")){
					boolean isAllowed = !main.api.getDivRegion(region).getBool(DRF.valueOf(toggle));
					System.out.println(isAllowed);
					main.api.getDivRegion(region).set(DRF.valueOf(toggle), isAllowed);
					main.s(p, "Flag &6" + toggle + " &bfor &6" + region + " &bchanged to &6" + isAllowed);
				}else{
					dp.s("No region found!");
				}
			}
			
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e){
		
		if (e.getEntity() instanceof Player){
		
			Player p = (Player) e.getEntity();
			
			String result = isInAnyRegion(p.getLocation());
			
			if (hasFlag(result, DRF.TAKE_DAMAGE) || e.getDamager() instanceof SmallFireball){
				e.setCancelled(true);
			}
			
		} else if (e.getEntity() instanceof ItemFrame || e.getEntity() instanceof Painting){
			if (hasFlag(isInAnyRegion(e.getEntity().getLocation()), DRF.TAKE_DAMAGE)){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageEvent e){
		
		if (e.getEntity() instanceof Player){
		
			Player p = (Player)e.getEntity();
			String result = isInAnyRegion(p.getLocation());

			if (hasFlag(result, DRF.TAKE_DAMAGE) || main.api.getDivPlayer(p).getBool(MMO.IS_CHOPPING) || main.api.getDivPlayer(p).getBool(MMO.IS_BLADING)){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e){
		
		if (Bukkit.getHelpMap().getHelpTopic(e.getMessage().split(" ")[0]) == null){
			e.setCancelled(true);
			main.s(e.getPlayer(), errorMessages.get(new Random().nextInt(errorMessages.size())));
		}
		
		String result = isInAnyRegion(e.getPlayer().getLocation());
		
		if (hasFlag(result, DRF.USE_COMMANDS)){
			if (!hasRegionPerms(e.getPlayer(), result)){
				e.setCancelled(true);
				main.s(e.getPlayer(), "&c&oYou are not authorized to use commands at &6" + result + "&c&o.");
			}
		}
		
		if (!e.getPlayer().isOp()){
		
			Player p = e.getPlayer();
			String[] args = e.getMessage().toLowerCase().split(" ");
			
			switch (args[0]){
			
				case "/plot": case "/plotme":
					
					if (args.length > 1){
						
						switch (args[1]){
						
							case "home": case "claim": case "auto": case "tp": case "info": case "biome": case "add": case "deny": case "undeny":
								
								e.setCancelled(true);
								op(p, e.getMessage());
								
							break;
						}
						
					} else {
						e.setCancelled(true);
						op(p, e.getMessage());
					}
					
				break;
			}
			
			if ((args[0].startsWith("//") && !e.getMessage().contains("schematic")) || args[0].startsWith("/wand") || args[0].equals("/j")){
				if (p.getWorld().getName().equalsIgnoreCase("WACP") || e.getPlayer().getWorld().getName().equalsIgnoreCase("not_cylum") || main.api.perms(p, "wa.staff.mod2", false)){
					e.setCancelled(true);
					op(p, e.getMessage());
					if (p.getWorld().getName().equals("world")){
						ElyChannel.CUSTOM.send("&6System", p.getDisplayName() + " used &6" + e.getMessage() + " &c&oin the main world.", "wa.staff.admin", main.api);
					}
				}
			}
		}
	}
	
	private void op(Player p, String command){
		p.setOp(true);
		p.performCommand(command.replaceFirst("/", ""));
		p.setOp(false);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onTP(DivinityTeleportEvent e){
		
		String result = isInAnyRegion(e.getPlayer().getLocation());
		
		if (hasFlag(result, DRF.TP_OUT)){
			if (!hasRegionPerms(e.getPlayer(), result)){
				e.setCancelled(true);
				main.s(e.getPlayer(), "&c&oYou are not authorized to TP out of &6" + result + "&c&o.");
			}
		}
		
		String resultTwo = isInAnyRegion(e.getTo());
		
		if (hasFlag(resultTwo, DRF.TP_IN)){
			if (!hasRegionPerms(e.getPlayer(), resultTwo)){
				e.setCancelled(true);
				main.s(e.getPlayer(), "&c&oYou are not authorized to TP into &6" + resultTwo + "&c&o.");
			}
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e){
		
		String result = isInAnyRegion(e.getPlayer().getLocation());
		
		if (hasFlag(result, DRF.CHAT)){
			if (!hasRegionPerms(e.getPlayer(), result)){
				e.setCancelled(true);
				main.s(e.getPlayer(), "&c&oYou are not authorized to chat at &6" + result + "&c&o.");
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {

		if (hasFlag(isInAnyRegion(e.getEntity().getLocation()), DRF.TNT_EXPLODE)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e){
		
		if (hasFlag(isInAnyRegion(e.getEntity().getLocation()), DRF.MOB_SPAWN) && e.getSpawnReason() != SpawnReason.BREEDING){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onIgnite(BlockIgniteEvent e){
		
		if (hasFlag(isInAnyRegion(e.getBlock().getLocation()), DRF.FIRE_SPREAD)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onGravity(EntityChangeBlockEvent e){
		
		if (hasFlag(isInAnyRegion(e.getBlock().getLocation()), DRF.GRAVITY)){
			e.setCancelled(true);
		}
	
		if (e.getBlock().getType().equals(Material.LADDER)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFlow(BlockPhysicsEvent e){
		
		Material mat = e.getBlock().getType();
		
		if (mat.equals(Material.WATER)){
			if (hasFlag(isInAnyRegion(e.getBlock().getLocation()), DRF.WATER_FLOW)){
				e.setCancelled(true);
			}
		} else if (mat.equals(Material.LAVA)){
			if (hasFlag(isInAnyRegion(e.getBlock().getLocation()), DRF.LAVA_FLOW)){
				e.setCancelled(true);
			}
		} else if (mat.equals(Material.LADDER)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFlow(BlockFromToEvent e){
		
		Material mat = e.getToBlock().getType();
		
		if (mat.equals(Material.WATER)){
			if (hasFlag(isInAnyRegion(e.getToBlock().getLocation()), DRF.WATER_FLOW)){
				e.setCancelled(true);
			}
		} else if (mat.equals(Material.LAVA)){
			if (hasFlag(isInAnyRegion(e.getToBlock().getLocation()), DRF.LAVA_FLOW)){
				e.setCancelled(true);
			}
		} else if (e.getBlock().getType().equals(Material.ICE)){
			if (hasFlag(isInAnyRegion(e.getToBlock().getLocation()), DRF.MELT)){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDecay(BlockFadeEvent e){
		
		Material mat = e.getBlock().getType();
		
		if (mat.equals(Material.LEAVES) || mat.equals(Material.LEAVES_2)){
			if (hasFlag(isInAnyRegion(e.getBlock().getLocation()), DRF.LEAF_DECAY)){
				e.setCancelled(true);
			}
		} else if (mat.equals(Material.SNOW_BLOCK) || mat.equals(Material.SNOW) || mat.equals(Material.ICE)){
			if (hasFlag(isInAnyRegion(e.getBlock().getLocation()), DRF.MELT)){
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@DivCommand(aliases = {"protect", "pro"}, desc = "Elysian World Protection Command", help = "/pro help", player = true, min = 1)
	public void onProtect(final Player p, String[] args){
		
		if (!args[0].equals("info") && !args[0].equals("view")){
			if (!main.api.perms(p, "wa.staff.mod2", false)){
				return;
			}
		}
		
		switch (args[0]){
		
			case "remove": case "redefine": case "flag": case "view": case "select": case "tp": case "disable": case "visual": case "priority":
				
			if (args.length < 2 || !main.api.doesRegionExist(args[1])){
				main.s(p, "&c&oThat region does not exist!");
				return;
			}
		}
		
		switch (args[0]){
		
			case "help":
				
				String[] messages = new String[] {
					"/pro create <region>",
					"/pro remove <region>",
					"/pro redefine <region>",
					"/pro flag <region> <flag> <value>",
					"/pro viewflags",
					"/pro priority <region> <number>",
					"/pro view <region>",
					"/pro perms <add, remove> <region> <perm>",
					"/pro select <region>",
					"/pro list",
					"/pro tp <region>",
					"/pro disable <region>",
					"/pro visual <region>",
					"/pro info"
				};
				
				for (String s : messages){
					main.s(p, s);
				}
				
			break;
			
			case "list":
				
				List<String> regions = new ArrayList<String>(main.divinity.api.divManager.getMap(DivinityManager.regionsDir).keySet());
				String rgColor = regions.size() > 0 && !main.api.getDivRegion(regions.get(0)).isDisabled() ? "&a" : "&c";
				String msg = regions.size() > 0 ? rgColor + regions.get(0) : "&c&oNo regions are defined.";
				
				for (int i = 1; i < regions.size(); i++){
					rgColor = regions.size() > i && !main.api.getDivRegion(regions.get(i)).isDisabled() ? "&a" : "&c";
					msg = regions.size() > i ? msg + "&7, " + rgColor + regions.get(i) : msg;
				}
				
				main.s(p, "&3Region List");
				main.s(p, msg);
				
			break;
			
			case "visual":
				
				if (!main.api.getDivPlayer(p).getBool(DPI.VISUAL)){
					
					DivinityRegion rg = main.api.getDivRegion(args[1]);
					
					if (rg.getLength() > 500 || rg.getWidth() > 500){
						main.s(p, "&c&oThis region is too big to visualize.");
						return;
					}
					
					final List<Location> locs = new ArrayList<Location>();
					List<Location> toRemove = new ArrayList<Location>();
					
					String[] minBlock = rg.getMinBlock().split(" ");
					String[] maxBlock = rg.getMaxBlock().split(" ");
					int counter = 0;
					
					main.api.getDivPlayer(p).set(DPI.VISUAL, true);
					
					for (int i = 0; i <= rg.getWidth(); i++){
						locs.add(new Location(Bukkit.getWorld(rg.getWorld()), d(minBlock[0]) + i, p.getLocation().getY(), d(minBlock[2])));
					}
					
					for (int i = 0; i <= rg.getLength(); i++){
						locs.add(new Location(Bukkit.getWorld(rg.getWorld()), d(minBlock[0]), p.getLocation().getY(), d(minBlock[2]) + i));
					}
					
					for (int i = 0; i <= rg.getWidth(); i++){
						locs.add(new Location(Bukkit.getWorld(rg.getWorld()), d(maxBlock[0]) - i, p.getLocation().getY(), d(maxBlock[2])));
					}
					
					for (int i = 0; i <= rg.getLength(); i++){
						locs.add(new Location(Bukkit.getWorld(rg.getWorld()), d(maxBlock[0]), p.getLocation().getY(), d(maxBlock[2]) - i));
					}
					
					for (Location l : locs){
						if (counter == 0){
							l.getBlock().setTypeIdAndData(Material.STAINED_GLASS.getId(),(byte) new Random().nextInt(16), true);
							counter = 3;
						} else {
							counter--;
							toRemove.add(l);
						}
					}
					
					for (Location l : toRemove){
						locs.remove(l);
					}
					
					main.s(p, "The visualization will revert in 20 seconds.");
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){
						
						for (Location l : locs){
							l.getBlock().setType(Material.AIR);
						}
						
						main.api.getDivPlayer(p).set(DPI.VISUAL, false);
						main.s(p, "Visualization terminated.");
						
					}}, 400L);
					
				} else {
					main.s(p, "&c&oYou're already viewing a visualization.");
				}

			break;
			
			case "redefine":
					
				if (main.we.getSelection(p) != null && main.we.getSelection(p) instanceof CuboidSelection){
						
					Selection sel = main.we.getSelection(p);
					DivinityRegion region = main.api.getDivRegion(args[1].toLowerCase());
					Vector max = sel.getMaximumPoint().toVector();
					Vector min = sel.getMinimumPoint().toVector();
						
					region.set(DRI.WORLD, p.getWorld().getName());
					region.set(DRI.MAX_BLOCK, max.getBlockX() + " " + max.getBlockY() + " " + max.getBlockZ());
					region.set(DRI.MIN_BLOCK, min.getBlockX() + " " + min.getBlockY() + " " + min.getBlockZ());
					region.set(DRI.AREA, sel.getArea());
					region.set(DRI.LENGTH, sel.getLength());
					region.set(DRI.HEIGHT, sel.getHeight());
					region.set(DRI.WIDTH, sel.getWidth());
					
					if (isInAnyRegion(p.getLocation()).equals("none")){
						region.set(DRI.PRIORITY, 0);
					} else {
						region.set(DRI.PRIORITY, main.api.getDivRegion(isInAnyRegion(p.getLocation())).getPriority() + 1);
					}
						
					main.s(p, "Redefine successful.");
						
				} else {
					main.s(p, "&c&oYou must select a cuboid with WorldEdit.");
				}
				
			break;
			
			case "tp":

				DivinityRegion rg = main.api.getDivRegion(args[1]);
				String[] mins = rg.getMinBlock().split(" ");
				main.api.event(new DivinityTeleportEvent(p, rg.getWorld(), "" + (i(mins[0]) + (rg.getWidth()/2)), mins[1], "" + (i(mins[2]) + (rg.getLength()/2))));
				
			break;
			
			case "remove":

				main.divinity.api.divManager.getMap(DivinityManager.regionsDir).remove(args[1].toLowerCase());
				File file = new File("./plugins/Divinity/regions/" + args[1].toLowerCase() + ".yml");
				file.delete();
				main.s(p, "Deleted &6" + args[1] + "&b.");
				
			break;
			
			case "disable":

				main.api.getDivRegion(args[1]).set(DRI.DISABLED, !main.api.getDivRegion(args[1]).isDisabled());
				main.s(p, args[1] + " updated.");
				
			break;
			
			case "create":
				
				if (args.length == 2){
					
					if (!main.api.doesRegionExist(args[1])){
						
						if (main.we.getSelection(p) != null && main.we.getSelection(p) instanceof CuboidSelection){
								
							Selection sel = main.we.getSelection(p);
							DivinityRegion region = main.api.getDivRegion(args[1].toLowerCase());
							Vector max = sel.getMaximumPoint().toVector();
							Vector min = sel.getMinimumPoint().toVector();
								
							region.set(DRI.WORLD, p.getWorld().getName());
							region.set(DRI.MAX_BLOCK, max.getBlockX() + " " + max.getBlockY() + " " + max.getBlockZ());
							region.set(DRI.MIN_BLOCK, min.getBlockX() + " " + min.getBlockY() + " " + min.getBlockZ());
							region.set(DRI.AREA, sel.getArea());
							region.set(DRI.LENGTH, sel.getLength());
							region.set(DRI.HEIGHT, sel.getHeight());
							region.set(DRI.WIDTH, sel.getWidth());
								
							if (isInAnyRegion(p.getLocation()).equals("none")){
								region.set(DRI.PRIORITY, 0);
							} else {
								region.set(DRI.PRIORITY, main.api.getDivRegion(isInAnyRegion(p.getLocation())).getPriority() + 1);
							}
								
							main.s(p, "Creation successful.");
							main.s(p, "&7&oFlag with /pro flag <region> <flag> <value>.");
							main.s(p, "&7&oAdjust perms with /pro perms <add, remove> <region> <perm>.");
								
						} else {
							main.s(p, "&c&oYou must select a cubiod region with WorldEdit.");
						}
						
					} else {
						main.s(p, "&c&oThat region already exists!");
					}
					
				} else {
					main.s(p, "/pro create <region>");
				}
				
			break;
			
			case "perms":
				
				if (args.length == 4){
					
					if (args[1].equals("add") || args[1].equals("remove")){
						
						if (main.api.doesRegionExist(args[2])){
							
							rg = main.api.getDivRegion(args[2]);
							
							if (args[1].equals("add")){
								rg.getList(DRI.PERMS).add(args[3]);
								main.s(p, "Added &6" + args[3]);
							} else {
								rg.getList(DRI.PERMS).remove(args[3]);
								main.s(p, "Removed &6" + args[3]);
							}
							
						} else {
							main.s(p, "&c&oThat region does not exist.");
						}
						
					} else {
						main.s(p, "/pro perms <add, remove> <region> <perm>");
					}
					
				} else {
					main.s(p, "/pro perms <add, remove> <region> <perm>");
				}
				
			break;
			
			case "select":

				rg = main.api.getDivRegion(args[1]);
				String[] max = rg.getMaxBlock().split(" ");
				String[] min = rg.getMinBlock().split(" ");
						
				if (rg.getWorld().equals(p.getWorld().getName())){
							
					RegionSelector sel = main.we.getSession(p).getRegionSelector(main.we.wrapPlayer(p).getWorld());
					com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(d(max[0]), d(max[1]), d(max[2]));
					com.sk89q.worldedit.Vector v2 = new com.sk89q.worldedit.Vector(d(min[0]), d(min[1]), d(min[2]));
					sel.selectPrimary(v);
					sel.selectSecondary(v2);
							
					main.s(p, "Selected &6" + args[1] + " &bas a cuboid!");
							
				} else {
					main.s(p, "&c&oWrong world! You must be in &6" + rg.getWorld() + "&b!");
				}
				
			break;
			
			case "view":

				rg = main.api.getDivRegion(args[1]);
				Map<DRF, Boolean> flagz = rg.getFlags();
				List<DRF> keys = new ArrayList<DRF>(flagz.keySet());
				List<String> perms = rg.getPerms();
				String flagMessage = flagz.size() > 0 ? "&6" + keys.get(0).s().toLowerCase() + "&f: &7" + flagz.get(keys.get(0)).toString().replace("true", "&cdeny").replace("false", "&aallow") : "&c&oNo flags listed.";
				String permMessage = perms.size() > 0 ? "&6" + perms.get(0) : "&c&oNo perms listed.";
						
				for (int i = 1; i < keys.size(); i++){
					flagMessage = keys.size() > i ? flagMessage + "&3, &6" + keys.get(i).s().toLowerCase() + "&f: " + flagz.get(keys.get(i)).toString().replace("true", "&cdeny").replace("false", "&aallow") : flagMessage;
				}
						
				for (int i = 1; i < perms.size(); i++){
					permMessage = perms.size() > i ? permMessage + "&3, &6" + perms.get(i) : permMessage;
				}
						
				main.s(p, "&3Viewing Region: &6" + args[1]);
				main.s(p, "World: &6" + rg.getWorld());
				main.s(p, "Minimum Bound: &6" + rg.getMinBlock());
				main.s(p, "Maximum Bound: &6" + rg.getMaxBlock());
				main.s(p, "Priority: &6" + rg.getPriority());
				main.s(p, flagMessage);
				main.s(p, permMessage);
				
			break;
			
			case "priority":
				
				rg = main.api.getDivRegion(args[1]);
				
				if (DivinityUtilsModule.isInteger(args[2])){
					rg.set(DRI.PRIORITY, i(args[2]));
					main.s(p, "Priority changed for &6" + args[1] + " &bto &6" + args[2] + "&b.");
				} else {
					main.s(p, "&c&oMust be a number!");
				}
				
			break;
			
			case "info":
				
				String results = isInAnyRegion(p.getLocation());
				
				if (!results.equals("none")){
					p.performCommand("pro view " + results);
				} else {
					main.s(p, "&c&oYou're not standing in any regions.");
				}
				
			break;
			
			case "viewflags":
				
				List<DRF> flags = Arrays.asList(DRF.values());
				msg = flags.size() > 0 ? "&6" + flags.get(0).s().toLowerCase() : "&c&oNo flags avalible.";
				
				for (int i = 1; i < flags.size(); i++){
					msg = flags.size() > i ? msg + "&7, &6" + flags.get(i).s().toLowerCase() : msg;
				}
				
				main.s(p, msg);
				
			break;
			
			case "flag":
				
				if (args.length == 4){
					
					if (main.api.doesRegionExist(args[1])){
						
						try {
							
							if (args[3].equals("allow") || args[3].equals("deny")){
								boolean flag = args[3].equals("allow") ? false : true;
								main.api.getDivRegion(args[1]).set(DRF.valueOf(args[2].toUpperCase()), flag);
								main.s(p, "Flag &6" + args[2] + " &bfor &6" + args[1] + " &bchanged to &6" + args[3]);
							} else {
								main.s(p, "&c&oallow or deny value.");
							}
							
						} catch (Exception e){
							main.s(p, "&c&oInvalid flag. See /pro viewflags");
						}
						
					} else {
						main.s(p, "&c&oThat region does not exist.");
					}
					
				} else {
					main.s(p, "/pro flag <region> <flag> <allow, deny>");
				}
				
			break;
			
			default:
				
				main.s(p, "/pro help");
				
			break;
		}
	}
	
	public String isInAnyRegion(Location l){
		
		Map<Integer, DivinityRegion> foundRegions = new THashMap<Integer, DivinityRegion>();
		
		for (DivinityStorage rg : main.divinity.api.divManager.getMap(DivinityManager.regionsDir).values()){
			if (isInRegion(l, rg.name())){
				foundRegions.put(((DivinityRegion)rg).getPriority(), (DivinityRegion)rg);
			}
		}
		
		if (foundRegions.size() > 0){
			List<Integer> priority = new ArrayList<Integer>(foundRegions.keySet());
			Collections.sort(priority);
			Collections.reverse(priority);
			return foundRegions.get(priority.get(0)).name();
		}
		
		return "none";
	}
	
	public boolean isInRegion(Location l, String region){
		Vector v = l.toVector();
		return isInRegion(l.getWorld().getName(), v.getBlockX(), v.getBlockY(), v.getBlockZ(), region);
	}
	
	public boolean isInRegion(String c, String region){
		String[] coords = c.split(" ");
		return isInRegion(coords[0], i(coords[1]), i(coords[2]), i(coords[3]), region);
	}
	
	public boolean isInRegion(String world, String c, String region){
		String[] coords = c.split(" ");
		return isInRegion(world, i(coords[0]), i(coords[1]), i(coords[2]), region);
	}
	
	public boolean isInRegion(String world, int x, int y, int z, String region){
		
		if (main.api.doesRegionExist(region)){
			
			DivinityRegion rg = main.api.getDivRegion(region);
			String[] min = rg.getMinBlock().split(" "); //x y z
			String[] max = rg.getMaxBlock().split(" ");
			
			if (rg.isDisabled()){
				return false;
			}
			
			if (rg.getWorld().equals(world)){
				if (x >= i(min[0]) && x <= i(max[0])){
					if (y >= i(min[1]) && y <= i(max[1])){
						if (z >= i(min[2]) && z <= i(max[2])){
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean hasRegionPerms(Player p, String region){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (main.api.doesRegionExist(region)){
			
			if (main.api.getDivRegion(region).isDisabled()){
				return true;
			}
			
			for (String perm : main.api.getDivRegion(region).getPerms()){
				if (dp.getList(DPI.PERMS).contains(perm)){
					return true;
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	public boolean hasFlag(String region, DRF flag){
		if (main.api.doesRegionExist(region)){
			return main.api.getDivRegion(region).getRawInfo(flag) != null ? main.api.getDivRegion(region).getBool(flag) : false;
		}
		return false;
	}
	
	private int i(String i){
		return Integer.parseInt(i);
	}
	
	private double d(String d){
		return Double.parseDouble(d);
	}
}