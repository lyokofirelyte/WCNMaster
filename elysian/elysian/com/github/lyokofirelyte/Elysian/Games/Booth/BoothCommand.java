package com.github.lyokofirelyte.Elysian.Games.Booth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class BoothCommand {

	private Elysian main;
	private Booth root;
	private Map<String, Integer> votes = new THashMap<String, Integer>();
	private List<String> hasVoted = new ArrayList<String>();
	
	public BoothCommand(Booth i){
		root = i;
		main = root.main;
	}
	
	@DivCommand(aliases = {"booth"}, desc = "Booth Game Command", help = "/booth help", player = true, min = 1)
	public void onBooth(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinityGame dg = root.toDivGame();
		ConfigurationSection f = dg.getConfigurationSection("Booths.");

		switch(args[0]){
		
		case "help": default:
			
			for(String s : new String[]{
					"help",
					"create <name> (set a booth location)",
					"remove <name>",
					"home (goes to your booth)",
					"reset",
					"vote <player1> <player2> <player3>",
					"boothlist",
					"result (shows the result of the vote)",
					"toggle (toggles the game enabled or disabled)"
					
			}){
				dp.s("/booth " + s);
			}
			
			break;
			
		case "create":
			if(args.length == 2){
				if(!main.api.perms(p, "wa.staff.mod2", false)) return;

				dg.set("Booths." + args[1] + ".location", p.getLocation().getWorld().getName() + " " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ() + " " + p.getLocation().getYaw() + " " + p.getLocation().getPitch());
				dp.s("Booth " + args[1] + " set!");
			}
			break;
		
			
		case "remove":
			if(!main.api.perms(p, "wa.staff.mod2", false)) return;

			if(args.length == 2){
				dg.set("Booths." + args[1], null);
				dp.s("Booth " + args[1] + " removed!");
			}
			break;
		
		case "home":
			if(!root.isActive(p)) return;
			boolean hasHome = false;
			
			for(String s : f.getKeys(false)){
				System.out.println("Checking " + s);
				if(dg.getString("Booths." + s + ".owner") != null && dg.getString("Booths." + s + ".owner").equals(p.getUniqueId().toString())){
					System.out.println("already has");
					p.teleport(root.locFromConfig("Booths." + s + ".location"));
					hasHome = true;
					return;
				}
			}
			
			if(hasHome == false){
				for(String s : f.getKeys(false)){
					if(dg.getString("Booths." + s + ".owner") == null || dg.getString("Booths." + s + ".owner") == ""){
						System.out.println("Is equal to nothing " + (dg.getString("Booths." + s + ".owner") == ""));
						System.out.println("is null " + (dg.getString("Booths." + s + ".owner") == null));
						System.out.println(dg.getString("Booths." + s + ".owner"));
						dg.set("Booths." + s + ".owner", p.getUniqueId().toString());
						p.teleport(root.locFromConfig("Booths." + s + ".location"));
						return;
					}
				}	
			}

			break;
		
		case "reset":
			if(!main.api.perms(p, "wa.staff.mod2", false)) return;

			for(String s : f.getKeys(false)){
				dg.set("Booths." + s + ".owner", null);
			}
			dp.s("Booths have been reset!");
			break;
			
		case "vote":
			if(!root.isActive(p)) return;

			if(args.length == 4 && !hasVoted.contains(p.getName())){
				if(votes.get(args[1]) == null){
					votes.put(args[1], 3);
				}else{
					votes.put(args[1], votes.get(args[1]) + 3);
				}
				
				if(votes.get(args[2]) == null){
					votes.put(args[2], 2);
				}else{
					votes.put(args[2], votes.get(args[2]) + 2);
				}
				
				if(votes.get(args[3]) == null){
					votes.put(args[3], 1);
				}else{
					votes.put(args[3], votes.get(args[3]) + 1);
				}
				
				hasVoted.add(p.getName());
			}else{
				dp.s("Not enough arguments or you have already voted!");
			}
			break;
			
		case "boothlist":
			StringBuilder sb = new StringBuilder();
			for(String s : f.getKeys(false)){
				sb.append(s + ", ");
			}
			dp.s(sb.toString());
			break;
			
		case "result":
			if(!main.api.perms(p, "wa.staff.mod2", false)) return;
			if(!root.isActive(p)) return;
				
			List<Integer> temp = new ArrayList<Integer>();
			for(Integer i : votes.values()){
				temp.add(i);
			}
			
		   Collections.sort(temp);
		   Collections.reverse(temp);

		   for(Integer i : temp){
			   for (Map.Entry<String, Integer> entry : votes.entrySet()) {
				    String key = entry.getKey();
				    Object value = entry.getValue();
				    
				    if(value == i){
					   DivinityUtilsModule.bc(key + ": " + i + " points!");
				   }
			   }
		   }
		   
		   
		   break;
			
		case "toggle":
			if(!main.api.perms(p, "wa.staff.mod2", false)) return;
			
			if(dg.getString("active") == null || dg.getString("active") == ""){
				dg.set("active", true);
			}else{
				dg.set("active", !dg.getBoolean("active"));
			}
			dp.s("The booths have been toggled to " + dg.getBoolean("active") + " (true = active, false = inactive)");
			break;
		
		
		
		
		}
		
	}
		
}
