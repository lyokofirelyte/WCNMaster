package com.github.lyokofirelyte.Elysian.Games.Cranked;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class CrankedCommand {

	Cranked root;
	Elysian main;

	CrankedCommand(Cranked i){
		root = i;
		main = root.main;
	}
	
	@DivCommand(aliases = {"cranked"}, desc = "Main Cranked Command", help = "/cranked help", player = true, min = 0)
	public void onCranked(Player p, String[] args){
		
		DivinityGame dg = root.toDivGame();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if(args.length == 0){
			for(String s : new String[]{
					"help",
					"addarena",
					"remarena",
					"addspawn <arena>",
					"remspawn <arena> <spawnid>",
					"arenalist",
					"spawnlist <arena>"
					
			}){
				main.s(p, "/cranked " + s);
			}
		}
		
		switch(args[0]){
			
			case "addarena":
				if(!dg.contains("Arenas." + args[0])){
					dg.set("Arenas." + args[0] + ".Name", args[0]);
				}else{
					dp.s("&cThat arena has already been set!");
				}
				break;
				
				
			case "remarena":
				if (args.length == 2 && dg.contains("Arenas." + args[1])){
					dg.set("Arenas." + args[1], null);
					dp.s("Removed the arena &6" + args[1] + "&b!");
				} else {
					dp.err("Invalid args or that arena does not exist.");
				}				break;
			
				
			case "addspawn":
				
				break;
			
				
			case "arenalist":
				
				break;
				
				
			case "spawnlist":
				
				break;
			
		}
	}
	
}
