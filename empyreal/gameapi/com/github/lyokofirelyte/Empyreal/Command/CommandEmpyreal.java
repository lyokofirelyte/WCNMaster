package com.github.lyokofirelyte.Empyreal.Command;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.command.CommandSender;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.GamePlayer;

public class CommandEmpyreal implements AutoRegister<CommandEmpyreal> {
	
	private Empyreal main;
	
	@Getter
	private CommandEmpyreal type = this;
	
	@Setter
	private boolean shutdownInProgress = false;
	
	public CommandEmpyreal(Empyreal i){
		main = i;
	}
	
	@GameCommand(min = 1, aliases = { "empyreal", "emp" }, help = "/emp ?", desc = "Empyreal (GAME API) root command")
	public void onEmpyreal(CommandSender sender, GamePlayer<?> gp, String[] args){
		
		switch (args[0]){
		
			case "rebootall":
				
				if (sender.isOp() && !shutdownInProgress){
					
					shutdownInProgress = true;
					
					main.sendPluginMessageAll("broadcast_reboot", "ALL MINIGAME SERVERS ARE REBOOTING IN 5 MINUTES!");
					
					APIScheduler.DELAY.start(main, "shutdown", 6000L, new Runnable(){
						public void run(){
							main.sendPluginMessageAll("shutdown", "ALL");
						}
					});
					
					

				} else {
					gp.s("&c&oYou either don't have perms or a reboot is already in progress!");
				}
				
			break;
		}
		
	}
}