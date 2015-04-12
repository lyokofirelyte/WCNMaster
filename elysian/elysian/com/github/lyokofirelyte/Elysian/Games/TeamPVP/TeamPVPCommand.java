/*package com.github.lyokofirelyte.Elysian.Games.TeamPVP;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.TeamPVP.TeamPVPData.TeamPVPGame;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityGame;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;

public class TeamPVPCommand {

	private Elysian main;
	private TeamPVP root;
	
	public TeamPVPCommand(TeamPVP i){
		root = i;
		main = root.main;
	}
	
	@DivCommand(perm = "wa.staff.mod2", aliases = {"teampvp"}, desc = "Team PVP Command", help = "/teampvp help", player = true, min = 1)
	public void onTeamPVP(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinityGame dg = root.toDivGame();
		
		switch (args[0]){
		
			case "help":
				
				for (String msg : new String[]{
					"/teampvp addarena <name>",
					"/teampvp remarena <name>",
					"/teampvp arenalist",
					"/teampvp lobby <arena>",
					"/teampvp team1spawn <arena>",
					"/teampvp team2spawn <arena>",
					"/teampvp addtoqueue <arena> <player1> <player2>"
				}){
					dp.s(msg);
				}
				
			break;
			
			case "arenalist":
				
				if (root.toDivGame().contains("Arenas")){
					for (String game : root.toDivGame().getConfigurationSection("Arenas").getKeys(false)){
						dp.s(game);
					}
				} else {
					dp.err("No arenas!");
				}
				
			break;
			
			case "addarena":
				
				if (args.length == 2 && !dg.contains("Arenas." + args[1])){
					dg.set("Arenas." + args[1] + ".Name", args[1]);
					dp.s("Created the arena &6" + args[1] + "&b!");
				} else {
					dp.err("Invalid args or that arena already exists.");
				}
				
			break;
			
			case "remarena":
				
				if (args.length == 2 && dg.contains("Arenas." + args[1])){
					dg.set("Arenas." + args[1], null);
					dp.s("Removed the arena &6" + args[1] + "&b!");
				} else {
					dp.err("Invalid args or that arena does not exist.");
				}
				
			break;
			
			case "team1spawn": case "team2spawn": case "lobby":
				
				Location l = p.getLocation();
				
				if (args.length == 2 && dg.contains("Arenas." + args[1])){
					dg.set("Arenas." + args[1] + "." + args[0], l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
					dp.s("Adjusted the spawn point or lobby for &6" + args[1] + "&b!");
				} else {
					dp.err("Invalid args or that arena does not exist.");
				}
				
			break;
			
			case "addtoqueue":
				
				if (args.length == 4 && dg.contains("Arenas." + args[1])){
					TeamPVPGame game = root.getGame(args[1]);
					if (!game.isInProgress() && main.api.isOnline(args[2]) && main.api.isOnline(args[3]) && !game.hasPlayer(args[2]) && !game.hasPlayer(args[3])){
						game.addPlayer(root.createPlayer(args[2]));
						game.addPlayer(root.createPlayer(args[3]));
						dp.s("Added both to a team!");
					} else {
						dp.err("Those players are not online.");
					}
				} else {
					dp.err("No game found or not enough args.");
				}
				
			break;
		}
	}
}*/