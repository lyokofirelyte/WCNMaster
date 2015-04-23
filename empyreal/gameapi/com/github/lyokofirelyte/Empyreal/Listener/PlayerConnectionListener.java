package com.github.lyokofirelyte.Empyreal.Listener;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;
import com.google.common.collect.Iterables;

public class PlayerConnectionListener implements AutoRegister<PlayerConnectionListener>, Listener {

	private Empyreal main;
	
	@Getter
	private PlayerConnectionListener type = this;
	
	public PlayerConnectionListener(Empyreal i){
		main = i;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		
		Player p = e.getPlayer();
		e.setJoinMessage(null);
		
		for (GameModule m : main.getGameModules()){
			m.onPlayerJoin(p);
		}
		
		if (main.getOnlineModules().containsKey(p.getUniqueId().toString())){
			main.getPlayers().put(p.getUniqueId(), main.getDivPlayer(p));
			for (String perm : main.getPlayers().get(p.getUniqueId()).getPerms()){
				if (perm.startsWith("server.transfer")){
					main.getPlayers().get(p.getUniqueId()).getPerms().remove(perm);
				}
			}
		}
		
		if (!main.getServerName().equals("GameServer") && !main.getServerName().equals("Creative") && !main.getServerName().equals("wa") && !main.getServerName().startsWith("SI-")){
			Utils.s(p, "This chat is not connected to other servers.");
		}
		
		if (main.getOnlineModules().containsKey(p.getUniqueId().toString())){
			DivinityPlayer dp = main.getDivPlayer(p);
			Utils.customBC("&7" + p.getDisplayName() + " &6<-> &aconnect &7(&e&o" + dp.getStr(DPI.JOIN_MESSAGE) + "&7)");
		} else {
			Utils.customBC("&7" + p.getDisplayName() + " &6<-> &aconnect");
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		
		final Player p = e.getPlayer();
		e.setQuitMessage(null);
		
		boolean msg = true;
		
		for (String perm : main.getPlayers().get(p.getUniqueId()).getPerms()){
			if (perm.startsWith("server.transfer")){
				Utils.bc("&7" + p.getDisplayName() + " &6<-> &e" + main.getServerName() + " &6-> &e" + perm.split("\\.")[2]);
				main.getPlayers().get(p.getUniqueId()).getPerms().remove(perm);
				msg = false;
				break;
			}
		}
		
		if (msg && main.getOnlineModules().containsKey(p.getUniqueId().toString())){
			DivinityPlayer dp = main.getDivPlayer(p);
			Utils.customBC("&7" + p.getDisplayName() + " &6<-> &cdisconnect &7(&e&o" + dp.getStr(DPI.QUIT_MESSAGE) + "&7)");
		} else if (msg){
			Utils.customBC("&7" + p.getDisplayName() + " &6<-> &cdisconnect");
		}
		
		if (main.getPlayers().containsKey(p.getUniqueId())){
			main.getPlayers().remove(p.getUniqueId());
		}
		
		if (main.getPlayers().containsKey(p.getUniqueId().toString()) && main.getGamePlayer(p.getUniqueId()).getPerms().contains("gameserver.staff")){
			p.setOp(true);
		}
		
		for (GameModule m : main.getGameModules()){
			m.onPlayerQuit(p);
		}
		
		if (!main.getServerName().equals("GameServer") && !main.getServerName().equals("Creative") && !main.getServerName().equals("wa")){
			APIScheduler.DELAY.start(main, "quit " + p.getName(), 5L, new Runnable(){
				public void run(){
					if (Bukkit.getServer().getOnlinePlayers().size() <= 0 || (Bukkit.getServer().getOnlinePlayers().size() == 1 && Iterables.getFirst(Bukkit.getOnlinePlayers(), null).equals(p))){
						Bukkit.getServer().shutdown();
					}
				}
			});
		}
	}
}