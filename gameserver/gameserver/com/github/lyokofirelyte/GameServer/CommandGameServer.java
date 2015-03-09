package com.github.lyokofirelyte.GameServer;

import org.bukkit.command.CommandSender;

import lombok.Getter;

import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class CommandGameServer implements AutoRegister<CommandGameServer> {

	@Getter
	private CommandGameServer type = this;
	
	private GameServer main;
	
	public CommandGameServer(GameServer i){
		main = i;
	}
	
	@GameCommand(aliases = { "gs", "gameserver" }, desc = "GameServer Command", help = "/gs")
	public void onGameServer(CommandSender cs, GamePlayer<?> gp, String[] args){
		if (cs.isOp()){
			for (String s : main.getApi().getServerSockets().keySet()){
				gp.s(s + " isClosed(): " + main.getApi().getServerSockets().get(s).isClosed() + ", getPort(): " + main.getApi().getServerSockets().get(s).getPort());
			}
		} else {
			gp.s("&c&oSorry, this command is super seekrit!");
		}
	}
}