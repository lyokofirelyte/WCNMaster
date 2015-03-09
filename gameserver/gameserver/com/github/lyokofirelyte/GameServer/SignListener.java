package com.github.lyokofirelyte.GameServer;

import java.util.List;

import lombok.Getter;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class SignListener implements AutoRegister<SignListener>, Listener {

	@Getter
	private GameServer main;
	
	@Getter
	private SignListener type = this;
	
	@Getter
	private List<String> game;
	
	public SignListener(GameServer i){
		main = i;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e){
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
		
			if (e.getClickedBlock() instanceof Sign || e.getClickedBlock().getState() instanceof Sign){
				Sign sign = (Sign) e.getClickedBlock().getState();
				if (sign.getLine(3) != null && sign.getLine(3).contains("[ JOIN ]")){
					
					String serverName = ChatColor.stripColor(sign.getLine(0));
					
					for (String player : main.getServerDeployQueue().values()){
						if (player.equals(e.getPlayer().getName())){
							Utils.s(Bukkit.getPlayer(player), "&c&oYou're already in queue to join a server!");
							return;
						}
					}
					
					if (!main.getServerDeployQueue().containsKey(serverName) && sign.getLine(2).equals(Utils.AS("&e&oPre-Lobby"))){
						main.getServerDeployQueue().put(serverName, e.getPlayer().getName());
						main.getApi().deployServer(serverName);
						sign.setLine(2, Utils.AS("&e&oStarting..."));
						Utils.s(e.getPlayer(), "&ePlease wait while we create a fresh lobby...");
					} else {
						if (!sign.getLine(2).equals(Utils.AS("&a&oLobby"))){
							Utils.s(e.getPlayer(), "&c&oThat server is not ready yet!");
						} else {
							main.getApi().sendToServer(e.getPlayer().getName(), serverName);
						}
					}
					
					sign.update();
				}
			}
		}
	}
	
	@EventHandler
	public void onSignCreate(SignChangeEvent e){
		
		if (e.getPlayer().isOp()){
			if (e.getLine(0).equals("game") && e.getLine(1) != null && !e.getLine(1).equals("")){
				Sign sign = ((Sign) e.getBlock().getState());
				GameSign gs = new GameSign(main, e.getLine(1), "&e&oPre-Lobby", sign.getBlock().getX(), sign.getBlock().getY(), sign.getBlock().getZ(), sign.getWorld().getName(), sign);
				main.getSigns().put(gs.getFullName(), gs);
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		
		if (e.getBlock() instanceof Sign || e.getBlock().getState() instanceof Sign){
			Location l = e.getBlock().getState().getLocation();
			String serverName = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
			if (main.getSigns().containsKey(serverName)){
				main.getSigns().get(serverName).delete();
				main.getSigns().remove(serverName);
				Utils.s(e.getPlayer(), "&c&oDeleted game sign from file!");
			}
		}
	}
}