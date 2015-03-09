package com.github.lyokofirelyte.Empyreal.Command;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

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
	
	@GameCommand(aliases = { "o" }, help = "/o <msg>", desc = "Staff Chat", player = true)
	public void onO(Player p, GamePlayer<?> gp, String[] args){
		
		if (p.isOp()){
			
			if (!main.getServerName().equals("GameServer")){
				main.sendToSocket(main.getServerSockets().get("GameServer"), "forward", "o", "&c" + p.getDisplayName() + "&f: &c&o" + Utils.createString(args, 0));
			} else {
				for (Player player : Bukkit.getOnlinePlayers()){
					if (player.isOp()){
						player.sendMessage(Utils.AS("&4\u273B &c" + p.getDisplayName() + "&f: &c&o" + Utils.createString(args, 0)));
					}
				}
				main.sendToAllServerSockets("o", "&c" + p.getDisplayName() + "&f: &c&o" + Utils.createString(args, 0));
			}
			
		} else {
			gp.s("&c&oSorry, only staff can use staff chat!");
		}
	}
}