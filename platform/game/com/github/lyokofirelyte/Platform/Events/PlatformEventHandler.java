package com.github.lyokofirelyte.Platform.Events;

import java.util.List;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Platform.Platform;
import com.github.lyokofirelyte.Platform.PlatformPlayer;
import com.github.lyokofirelyte.Platform.Rounds.Round;
import com.github.lyokofirelyte.Empyreal.APIScheduler;

public class PlatformEventHandler implements AutoRegister<PlatformEventHandler>, Listener {

	public Platform main;
	
	@Getter
	private PlatformEventHandler type = this;
	
	public PlatformEventHandler(Platform i) {
		main = i;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		
		if (main.gameData.getCenter() != null && e.getPlayer().getWorld().equals(main.gameData.getCenter().getWorld())){
			double fallY = main.gameData.getCenter().getY();
			if (e.getPlayer().getLocation().getY() <= (fallY-2)){
				Bukkit.getPluginManager().callEvent(new PlatformFallEvent(e.getPlayer()));
			}
		}
	}
	
	@EventHandler
	public void onFall(PlatformFallEvent e){
		
		if (e.isCancelled()){
			return;
		}
		
		if (main.gameData.getArenaFullGrid().size() <= 0){
			
			for (Location l : main.gameData.getOuterRing()){
				if (!l.getBlock().getType().equals(Material.AIR)){
					e.getPlayer().teleport(new Location(l.getWorld(), l.getX(), l.getY()+2, l.getZ()));
					Utils.lowerEffects(l);
					break;
				}
			}
			
		} else {
			
			List<Location> locs = main.gameData.getArenaFullGrid();
		
			for (int x = 0; x < locs.size(); x++){
				Location l = locs.get((locs.size()-1)-x);
				if (!l.getBlock().getType().equals(Material.AIR)){
					e.getPlayer().teleport(new Location(l.getWorld(), l.getX(), l.getY()+2, l.getZ()));
					Utils.lowerEffects(l);
					break;
				}
			}
		}
		
		if (!main.gameData.isActive()){
			main.msg(e.getPlayer().getName(), "&7&oWatch out!");
			return;
		}
		
		PlatformPlayer pp = main.getApi().getGamePlayer(e.getPlayer().getUniqueId(), PlatformPlayer.class).getType();
		
		if (pp.getLives() <= 0){
			main.msg(pp.getName(), "&cYou're out of lives. You can still play, but with no point rewards.");
		} else {
			pp.setLives(pp.getLives() - 1);
			pp.setCombo(1);
			main.msg(pp.getName(), "&cYou've lost a life. You now have &e" + pp.getLives() + " &cremaining.");
		}
	}
	
	@EventHandler
	public void onEnd(final RoundEndEvent e){
		main.data.getRound(Round.ZERO).end();
		if (main.data.getRounds().containsKey(Integer.parseInt(e.getRound().toString()))){
			APIScheduler.DELAY.start(main.getApi(), "delay", 160L, new Runnable(){
				public void run(){
					main.data.getRound(e.getRound()).start();
				}
			});
		}
	}
}