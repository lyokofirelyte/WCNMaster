/*package com.github.lyokofirelyte.Elysian.Games.Hellwave;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.api.DivinityGame;
import com.github.lyokofirelyte.Elysian.api.DivinityPlayer;

public class HellwaveCommands {

	Hellwave root;
	Elysian main;
	
	public HellwaveCommands(Hellwave i){
		root = i;
		main = i.main;
	}
	
	@DivCommand(aliases = {"hellwave"}, desc = "Hellwave Game Command", help = "/booth help", player = true, min = 1)
	public void onBooth(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinityGame dg = root.toDivGame();
		
		
	}
	
}*/