package com.github.lyokofirelyte.Elysian.Commands;

import java.util.List;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElyHome implements AutoRegister<ElyHome> {

	private Elysian main;
	
	@Getter
	private ElyHome type = this;
	
	public ElyHome(Elysian i){
		main = i;
	}

	@GameCommand(aliases = {"home", "h"}, help = "/home <name>", desc = "Return Home Command", player = true)
	public void noPlaceLikeHome(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		List<String> homes = dp.getList(DPI.HOME);
		
		if (args.length == 0){
			if (homes.size() <= 0){
				main.s(p, "&c&oYou're currently homeless. Set a home with /sethome <name>.");
			} else {
				main.s(p, "&3Home List:");
				for (String home : homes){
					main.s(p, "&6" + home.split(" ")[0]);
				}
			}
		} else {
			for (String home : homes){
				String[] h = home.split(" ");
				if (h[0].equalsIgnoreCase(args[0])){
					main.api.event(new DivinityTeleportEvent(p, h[1], h[2], h[3], h[4], h[5], h[6]));
				}
			}
		}
	}
	
	@GameCommand(aliases = {"sethome", "sh"}, help = "/sethome <name>", desc = "Set Home Command", player = true, min = 1)
	public void onSetHome(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		List<String> homes = dp.getList(DPI.HOME);
		List<String> perms = dp.getList(DPI.PERMS);
		String toRemove = "none";
		int amount = perms.contains("wa.rank.immortal") ? 4 : perms.contains("wa.rank.regional") ? 3 : perms.contains("wa.rank.townsman") ? 2 : 1;
		
		for (String home : homes){
			if (home.split(" ")[0].equalsIgnoreCase(args[0])){
				toRemove = home;
				break;
			}
		}
		
		if (!toRemove.equals("none")){
			homes.remove(toRemove);
		}
		
		if (homes.size() >= amount){
			main.s(p, "&c&oYou have the max amount of homes for your rank.");
			main.s(p, "&c&oUse /delhome to remove one.");
		} else {
			Location l = p.getLocation();
			homes.add(args[0] + " " + l.getWorld().getName() + " " + l.getX() + " " + l.getY() + " " + l.getZ() + " " + l.getYaw() + " " + l.getPitch());
			main.s(p, "Success! Added the home &6" + args[0] + "&b.");
		}	
	}
	
	@GameCommand(aliases = {"delhome", "remhome", "dh", "rh"}, help = "/delhome <name>", desc = "Delete Home Command", player = true, min = 1)
	public void onDelHome(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		List<String> homes = dp.getList(DPI.HOME);
		String toRemove = "none";
		
		for (String home : homes){
			if (home.split(" ")[0].equalsIgnoreCase(args[0])){
				toRemove = home;
				break;
			}
		}
		
		if (!toRemove.equals("none")){
			homes.remove(toRemove);
			main.s(p, "Deleted home &6" + args[0] + "&b.");
		} else {
			main.s(p, "&c&oThat home does not exist. See /home list.");
		}
	}
}