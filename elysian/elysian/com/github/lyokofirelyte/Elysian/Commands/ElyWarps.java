package com.github.lyokofirelyte.Elysian.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class ElyWarps implements AutoRegister, Listener {

	private Elysian main;
	
	public ElyWarps(Elysian i){
		main = i;
	}
	
	private String dir = "./plugins/Divinity/warps/";
	private String warpText = "&3/&f*&3/ &bWarp &3/&f*&3/";
	String[] warps = new String[]{};
	
	{
		u();
	}
	
	private void u(){
		warps = new File(dir).list();
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e){		
		if(main.api.perms(e.getPlayer(), "wa.staff.mod", true)){
			if(e.getLine(0).equalsIgnoreCase("warp")){
				if(e.getLine(1) != null && !e.getLine(1).equals("")){
	
					if (new ArrayList<String>(Arrays.asList(warps)).contains(e.getLine(1).toLowerCase() + ".yml")){
						e.setLine(0, main.AS(warpText));

						Sign s = (Sign) e.getBlock().getState();
						if(e.getLine(2) != null && !e.getLine(2).equals("")){
							List<String> signs = new ArrayList<String>(main.api.getDivSystem().getList(DPI.SIGN_LOCATION));
							Location l = e.getBlock().getLocation();
							signs.add(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " %PERM%" + e.getLine(2));
							main.api.getDivSystem().set(DPI.SIGN_LOCATION, signs);
							e.setLine(1, main.AS("&6" + e.getLine(1)));
							e.setLine(2, "");
						}else{
							e.setLine(1, "&b" + e.getLine(1));
							List<String> signs = new ArrayList<String>(main.api.getDivSystem().getList(DPI.SIGN_LOCATION));
							Location l = e.getBlock().getLocation();
							signs.add(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " %PERM%wa.member");
							main.api.getDivSystem().set(DPI.SIGN_LOCATION, signs);
						}
						
					} else {
						e.setLine(0, main.AS(warpText));
						e.setLine(1, main.AS("&c&oNot found!"));
					}
				}else{
					e.setLine(0, main.AS(warpText));
					e.setLine(1, main.AS("&4Invalid!"));
				}
			}
			
			
		}
		
		
	}
	
	@EventHandler
	public void onClickyTheSign(PlayerInteractEvent e){
		
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getState() instanceof Sign){
			Sign s = (Sign) e.getClickedBlock().getState();
			
			if(s.getLine(0).equals(main.AS(warpText))){
				String warp = ChatColor.stripColor(s.getLine(1));
				Location loc = e.getClickedBlock().getLocation();

				for(String str : main.api.getDivSystem().getList(DPI.SIGN_LOCATION)){
					String[] loc2 = str.split(" ");
					System.out.println(loc2[1]);
					System.out.println(loc.getBlockX());
					System.out.println(loc2[1].equals(loc.getBlockX() + ""));
		
					if(loc2[0].equals(loc.getWorld().getName()) && loc2[1].equals(loc.getBlockX() + "") && loc2[2].equals(loc.getBlockY() + "") && loc2[3].equals(loc.getBlockZ() + "")){
						String perm = str.split("%PERM%")[1];
						if(main.api.perms(e.getPlayer(), perm, false)){
							if (new ArrayList<String>(Arrays.asList(warps)).contains(warp.toLowerCase() + ".yml")){
								main.api.event(new DivinityTeleportEvent(e.getPlayer(), extractLoc(warp)));
		
							}else{
								s.setLine(1,  main.AS("&c&oNot found!"));
								s.update();
							}
						}
						return;
					}
					
				}	
			}
			
		}
		
	}
	
	@EventHandler
	public void onSignBreak(BlockBreakEvent e){
		
		if(e.getBlock().getState() instanceof Sign){
			Sign s = (Sign) e.getBlock().getState();
			if(s.getLine(0).equals(main.AS(warpText))){
				Location loc = e.getBlock().getLocation();
				for(String str : main.api.getDivSystem().getList(DPI.SIGN_LOCATION)){
					String[] loc2 = str.split(" ");
					if(loc2[0].equals(loc.getWorld().getName()) && loc2[1].equals(loc.getBlockX() + "") && loc2[2].equals(loc.getBlockY() + "") && loc2[3].equals(loc.getBlockZ() + "")){
						List<String> signs = new ArrayList<String>(main.api.getDivSystem().getList(DPI.SIGN_LOCATION));
						signs.remove(signs.indexOf(str));
						main.api.getDivSystem().set(DPI.SIGN_LOCATION, signs);
						return;
					}
				}
			}
			
			
		}
		
	}
	
	@DivCommand(perm = "wa.guest", aliases = {"s", "spawn"}, desc = "Elysian Spawn Command", help = "/s", player = true)
	public void onSpawn(Player p, String[] args){
		String[] loc = main.api.getDivSystem().getStr(DPI.SPAWN_POINT).split("%SPLIT%");
		Location spawn = new Location(Bukkit.getWorld(loc[0]), Float.parseFloat(loc[1]), Float.parseFloat(loc[2]), Float.parseFloat(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
		p.teleport(spawn);
		main.s(p, "You have arrived at spawn!");
	}
	
	@DivCommand(perm = "wa.staff.admin", aliases = {"setspawn"}, desc = "Elysian Spawn Set Command", help = "/setspawn", player = true)
	public void onSpawnSet(Player p, String[] args){
		Location loc = p.getLocation();
		main.api.getDivSystem().set(DPI.SPAWN_POINT, loc.getWorld().getName() + "%SPLIT%" + loc.getX() + "%SPLIT%" + loc.getY() + "%SPLIT%" + loc.getZ() + "%SPLIT%" + loc.getYaw() + "%SPLIT%" + loc.getPitch());
		main.s(p, "Spawn point set!");
	}
	
	
	@DivCommand(name = "Warp", aliases = {"warp", "w", "creative"}, desc = "Elysian Warp Command", help = "/warp <name>", player = true)
	public void onWarp(Player p, String[] args, String cmd){
		
		if (cmd.equals("creative")){
			
			main.api.sendToServer(p.getName(), "Creative");
			
		} else if (args.length == 0 || DivinityUtilsModule.isInteger(args[0])){
			
			warpList(p, args.length == 0 ? 1 : Integer.parseInt(args[0]));
			
		} else if (main.api.perms(p, "wa.staff.mod2", false)){
			
			if (new ArrayList<String>(Arrays.asList(warps)).contains(args[0].toLowerCase() + ".yml")){
				main.api.event(new DivinityTeleportEvent(p, extractLoc(args[0])));
			} else {
				main.s(p, "&c&oWarp not found.");
			}
		}
	}

	@DivCommand(perm = "wa.staff.mod2", name = "SW", aliases = {"setwarp", "remwarp", "delwarp"}, desc = "Elysian Set/Rem Warp Command", help = "/setwarp <name>, /remwarp <name>", player = true, min = 1)
	public void onSetWarp(Player p, String[] args, String cmd){
		
		if (cmd.equals("setwarp")){
			
			if (!new ArrayList<String>(Arrays.asList(warps)).contains(args[0].toLowerCase() + ".yml")){
				
				File file = new File(dir + args[0].toLowerCase() + ".yml");
				
				try {
					file.createNewFile();
				} catch (Exception e){
					main.s(p, "&c&oFailed to create warp!");
					e.printStackTrace();
				}
				
				YamlConfiguration yaml = new YamlConfiguration();
				Vector v = p.getLocation().toVector();
				
				yaml.set("world", p.getWorld().getName());
				yaml.set("x", v.getBlockX());
				yaml.set("y", v.getBlockY());
				yaml.set("z", v.getBlockZ());
				yaml.set("yaw", p.getLocation().getYaw());
				yaml.set("pitch", p.getLocation().getPitch());
				
				try {
					yaml.save(file);
					main.s(p, "Saved warp &6" + args[0] + "&b.");
					u();
				} catch (Exception e){}
				
			} else {
				main.s(p, "&c&oThat warp already exists!");
			}
			
		} else {
			
			if (new ArrayList<String>(Arrays.asList(warps)).contains(args[0].toLowerCase() + ".yml")){
				
				File file = new File(dir + args[0].toLowerCase() + ".yml");
				
				try {
					file.delete();
					main.s(p, "Deleted warp &6" + args[0] + "&b.");
					u();
				} catch (Exception e){
					main.s(p, "&c&oFailed to delete warp!");
					e.printStackTrace();
				}
				
			} else {
				main.s(p, "&c&oThat warp does not exist!");
			}
		}
	}
	
	private void warpList(Player p, int page){
		
		String msg = "&3";
		
		for (int x = (page*20)-20; x < page*20; x++){
			msg = !msg.equals("&3") && warps.length > x ? msg + "&6, &3" + warps[x].replace(".yml", "") : warps.length > x ? msg + warps[x].replace(".yml", "") : msg + "";
		}
		
		main.s(p, "&bViewing Warp Page &6" + page + "&b. (&6" + page*20 + "&b/&6" + warps.length + "&b)");
		p.sendMessage(main.AS(msg));
	}
	
	private Location extractLoc(String warp){
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(dir + warp.toLowerCase() + ".yml"));
		World w = Bukkit.getWorld(yaml.getString("world"));
		return new Location(w, yaml.getDouble("x"), yaml.getDouble("y"), yaml.getDouble("z"), Float.parseFloat(yaml.getString("yaw")), Float.parseFloat(yaml.getString("pitch")));
	}
}