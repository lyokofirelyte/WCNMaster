package com.github.lyokofirelyte.Elysian.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class ElyWarps implements AutoRegister {

	private Elysian main;
	
	public ElyWarps(Elysian i){
		main = i;
	}
	
	private String dir = "./plugins/Divinity/warps/";
	String[] warps = new String[]{};
	
	{
		u();
	}
	
	private void u(){
		warps = new File(dir).list();
	}
	

	@DivCommand(aliases = {"s", "spawn"}, desc = "Elysian Spawn Command", help = "/s", player = true)
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
			
			main.api.event(new DivinityTeleportEvent(p, extractLoc("wacp")));
			
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