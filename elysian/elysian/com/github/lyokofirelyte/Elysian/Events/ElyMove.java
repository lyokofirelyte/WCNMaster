package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class ElyMove implements Listener, AutoRegister {
	
	private Elysian main;
	
	public ElyMove(Elysian i){
		main = i;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (dp.getBool(DPI.DISABLED) || borderCheck(e.getPlayer(), e.getTo().toVector())){
			e.getPlayer().teleport(e.getFrom());
		}
		
		main.afkCheck(e.getPlayer());
		main.api.event(new ScoreboardUpdateEvent(e.getPlayer(), "move"));
		
		if (!dp.getStr(DPI.SPECTATE_TARGET).equals("none") && !dp.getBool(DPI.SPECTATING)){
			Player you = main.api.getPlayer(dp.getStr(DPI.SPECTATE_TARGET));
			you.setAllowFlight(true); you.setFlying(true);
			Vector themV = e.getPlayer().getLocation().toVector();
			you.setVelocity(themV.subtract(you.getLocation().toVector()).normalize());
		}
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (e.getAction() == Action.LEFT_CLICK_AIR){
			if (dp.getBool(DPI.IS_DIS) && !((LivingEntity)dp.getRawInfo(DPI.DIS_ENTITY)).isDead()){
				((LivingEntity)dp.getRawInfo(DPI.DIS_ENTITY)).setVelocity(e.getPlayer().getLocation().getDirection().normalize().multiply(0.6).setY(0));
			}
		} else if (e.getAction() == Action.RIGHT_CLICK_AIR){
			if (dp.getBool(DPI.IS_DIS) && !((LivingEntity)dp.getRawInfo(DPI.DIS_ENTITY)).isDead()){
				((LivingEntity)dp.getRawInfo(DPI.DIS_ENTITY)).setVelocity(new Vector(0, 1, 0));
			}
		}
	}
	
	private boolean borderCheck(Player p, Vector v){
		
		if (p.getWorld().getName().equals("world") && (v.getBlockX() > 20000 || v.getBlockX() < -20000 || v.getBlockZ() > 20000 || v.getBlockZ() < -20000)){
			main.s(p, "&c&oBorder reached!");
			return true;
		}
		
		return false;
	}
}