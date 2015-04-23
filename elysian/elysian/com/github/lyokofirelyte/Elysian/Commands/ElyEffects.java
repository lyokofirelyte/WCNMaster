package com.github.lyokofirelyte.Elysian.Commands;

import lombok.Getter;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElyEffects implements AutoRegister<ElyEffects> {

	private Elysian main;
	
	@Getter
	private ElyEffects type = this;
	
	public ElyEffects(Elysian i){
		main = i;
	}
	
	@GameCommand(name = "FW", aliases = {"fw", "firework", "flare"}, perm = "wa.rank.statesman", desc = "Firework command", help = "/fw <color> <type> [player]", player = true)
	public void onFirework(Player p, String[] args, String cmd){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		boolean pass = false;
		
		if (args.length == 1 && args[0].equals("list")){
			main.s(p, "&3Types:");
			for (Type t : Type.values()){
				main.s(p, t.name().toLowerCase());
			}
			main.s(p, "&3Colors:");
			main.s(p, "All basic colors (like red, orange, green, etc)");
		} else if (args.length >= 2){
		
			if (System.currentTimeMillis() >= dp.getLong(DPI.FIREWORK_COOLDOWN)){
			
				Player victim = p;
				dp.set(DPI.FIREWORK_COOLDOWN, System.currentTimeMillis() + (20*1000));
				
				if (args.length == 3){
					if (main.api.doesPartialPlayerExist(args[2])){
						if (main.api.isOnline(args[2])){
							victim = main.api.getPlayer(args[2]);
						} else {
							main.s(p, "&c&oThat player is not online.");
						}
					} else {
						main.s(p, "playerNotFound");
					}
				}
				
				for (Type t : Type.values()){
					if (t.name().equalsIgnoreCase(args[1])){
						pass = !pass;
						break;
					}
				}
				
				if (pass){
				
					if (cmd.equalsIgnoreCase("flare") && main.api.perms(p, "wa.rank.national", false)){
						overHeadFW(victim, getColor(args[0]), Type.valueOf(args[1].toUpperCase()) != null ? Type.valueOf(args[1].toUpperCase()) : Type.BURST);
					} else {
						playCircleFw(victim, getColor(args[0]), Type.valueOf(args[1].toUpperCase()) != null ? Type.valueOf(args[1].toUpperCase()) : Type.BURST, 5, 1, 1, true, false);
					}
					
				} else {
					main.s(p, "&c&oInvalid type. See /fw list");
				}
					
			} else {
				main.s(p, "&c&oThis command is still on cooldown! " + ((dp.getLong(DPI.FIREWORK_COOLDOWN) - System.currentTimeMillis())/1000) + " seconds.");
			}
		} else {
			main.s(p, main.help("fw", this));
		}
	}
	
	public void playCircleFw(Player p, Color color, Type type, int radius, int height, int addY, boolean hollow, boolean sphere){
		for (Location l : DivinityUtilsModule.circle(p.getLocation(), radius, height, hollow, sphere, addY)){
			main.fw(p.getWorld(), l, type, color);
		}
	}
	
	public void overHeadFW(Player p, Color color, Type type){
		for (Location l : DivinityUtilsModule.circle(p.getLocation(), 5, 5, true, true, 5)){
			main.fw(p.getWorld(), l, type, color);
		}
	}
	
	private Color getColor(String color){ // There's no valueOf or values() so it's messy.
		switch (color){
			case "white":
				return Color.WHITE;
			case "black":
				return Color.BLACK;
			case "aqua":
				return Color.AQUA;
			case "green":
				return Color.GREEN;
			case "grey": case "gray":
				return Color.GRAY;
			case "purple": case "pink":
				return Color.PURPLE;
			case "red":
				return Color.RED;
			case "fuchsia":
				return Color.FUCHSIA;
			case "orange":
				return Color.ORANGE;
			case "olive":
				return Color.OLIVE;
			case "blue":
				return Color.BLUE;
			case "navy":
				return Color.NAVY;
			case "lime":
				return Color.LIME;
			case "maroon":
				return Color.MAROON;
			case "silver":
				return Color.SILVER;
			case "teal":
				return Color.TEAL;
			default:
				return Color.WHITE;
		}
	}
}