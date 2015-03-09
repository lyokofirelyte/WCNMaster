package com.github.lyokofirelyte.Gotcha;

import java.util.ArrayList;
import java.util.Random;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class GotchaCommand implements AutoRegister<GotchaCommand> {

	private Gotcha main;

	@Getter
	private GotchaCommand type = this;
	
	public GotchaCommand(Gotcha i){
		main = i;
	}
	
	@GameCommand(aliases = {"gotcha"}, desc = "Gotcha Game Command", help = "/gotcha help", player = true)
	public void onGotcha(CommandSender sender, GamePlayer<?> gp, String[] args){
		
		if (!sender.isOp()){
			gp.s("&c&oGotcha commands are for admins only, sorry!");
		}
		
		args = args.length == 0 ? new String[] { "help" } : args;
		
		switch (args[0]){
		
			case "help":
				
				for (String msg : new String[]{
					"/gotcha addarena <name>",
					"/gotcha remarena <name>",
					"/gotcha arenalist",
					"/gotcha lobby <arena>",
					"/gotcha addspawnpoint <arena>",
					"/gotcha clearspawnpoints <arena>",
				}){
					gp.s(msg);
				}
				
			break;
			
			case "arenalist":
				
				String list = "";
				
				for (int i = 0; i < main.getArenas().size(); i++){
					list += "&e" + main.getArenas().get(i) + (i == main.getArenas().size()-1 ? "" : "&f, ");
				}
				
			break;
			
			case "addarena":
				
				if (args.length == 2){
					if (!main.getArenas().containsKey(args[1])){
						main.getArenas().put(args[1], new GotchaArena(args[1]));
						gp.s("Arena added as " + args[1] + "!");
						if (main.getArenas().size() == 1){
							main.setChosenArena(main.getArenas().get(0));
						}
					} else {
						gp.s("&c&oThat arena already exists!");
					}
				} else {
					gp.s("&c&o/gotcha addarena <name>");
				}
				
			break;
			
			case "remarena":
				
				if (args.length == 2){
					if (main.getArenas().containsKey(args[1])){
						main.getArenas().get(args[1]).delete();
						main.getArenas().remove(args[1]);
					} else {
						gp.s("&c&oThat arena does not exist!");
					}
				} else {
					gp.s("&c&o/gotcha remarena <name>");
				}
				
			break;
			
			case "addspawnpoint":
				
				Location l = gp.getPlayer().getLocation();
				
				if (args.length == 2){
					if (main.getArenas().containsKey(args[1])){
						main.getArenas().get(args[1]).getSpawnPoints().add(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
						gp.s("Added a spawn point! There are now " + main.getArenas().get(args[1]).getSpawnPoints().size() + ".");
					} else {
						gp.s("&c&oThat arena does not exist!");
					}
				} else {
					gp.s("&c&o/gotcha addspawnpoint <arena>");
				}
				
			break;
			
			case "clearspawnpoints":
				
				if (args.length == 2){
					if (main.getArenas().containsKey(args[1])){
						main.getArenas().get(args[1]).setSpawnPoints(new ArrayList<String>());
						gp.s("Removed all spawn points for " + args[1] + ".");
					} else {
						gp.s("&c&oThat arena does not exist!");
					}
				} else {
					gp.s("&c&o/gotcha clearspawnpoints <arena>");
				}
				
			break;
			
			case "lobby":
				
				l = gp.getPlayer().getLocation();
				
				if (args.length == 2){
					if (main.getArenas().containsKey(args[1])){
						main.getArenas().get(args[1]).setLobby(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
						gp.s("Added a lobby to " + args[1] + ".");
					} else {
						gp.s("&c&oThat arena does not exist!");
					}
				} else {
					gp.s("&c&o/gotcha lobby <arena>");
				}
				
			break;
		}
	}
}