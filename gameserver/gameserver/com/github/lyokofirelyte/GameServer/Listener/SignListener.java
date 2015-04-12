package com.github.lyokofirelyte.GameServer.Listener;

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

import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;
import com.github.lyokofirelyte.GameServer.GameServer;
import com.github.lyokofirelyte.GameServer.GameSign;

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
					
				} else if (sign.getLine(0).contains("TO SERVER")){
					main.getApi().sendToServer(e.getPlayer().getName(), ChatColor.stripColor(sign.getLine(1)));
				} else if (sign.getLine(0).contains("> WARP <")){
					for (String name : main.getWarps().keySet()){
						if (sign.getLine(1).substring(2).equals(name)){
							main.getWarps().get(name).teleport(e.getPlayer());
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onSignCreate(SignChangeEvent e){
		
		if (e.getPlayer().isOp()){
			
			Sign sign = ((Sign) e.getBlock().getState());
			
			if (e.getLine(0) != null && !e.getLine(0).equals("")){
				
				switch (e.getLine(0)){
				
					case "game":
						
						GameSign gs = new GameSign(main, e.getLine(1), "&e&oPre-Lobby", sign.getBlock().getX(), sign.getBlock().getY(), sign.getBlock().getZ(), sign.getWorld().getName(), sign);
						main.getSigns().put(gs.getFullName(), gs);
						
					break;
					
					case "warp":
						
						e.setLine(0, Utils.AS("&e> WARP <"));
						e.setLine(1, Utils.AS("&f" + e.getLine(1)));
						e.setLine(3, Utils.AS("&a[ PRESS ]"));
						
					break;
				}
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