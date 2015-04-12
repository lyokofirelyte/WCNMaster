package com.github.lyokofirelyte.Deviance;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class CommandDeviance implements AutoRegister<CommandDeviance> {

	@Getter
	private CommandDeviance type = this;
	
	@Getter @Setter
	private Deviance main;
	
	public CommandDeviance(Deviance i){
		setMain(i);
	}
	
	@GameCommand(aliases = { "deviance", "dev" }, help = "/dev ?", desc = "Deviance Main Command", perm = "gameserver.staff", player = true, min = 1)
	public void onDev(Player p, GamePlayer<?> gp, String[] args){
		
		switch (args[0]){
		
			case "?": case "help":
				
				gp.s("Deviance. An environmental changing minigame by Hugs.");
				gp.s("/dev spawn");
				
			break;
			
			case "spawn":
				
				p.teleport(p.getWorld().getSpawnLocation());
				
			break;
		}
	}
}