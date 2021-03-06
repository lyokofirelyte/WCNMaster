package com.github.lyokofirelyte.Elysian.Events;

import lombok.Getter;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElyMove implements Listener, AutoRegister<ElyMove> {
	
	private Elysian main;
	
	@Getter
	private ElyMove type = this;
	
	public ElyMove(Elysian i){
		main = i;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (dp.getStr(DPI.RING_LOC).equals("none") && dp.getBool(DPI.DISABLED)){
			e.getPlayer().teleport(e.getFrom());
		}
		
		main.afkCheck(e.getPlayer());
		
		if (!dp.getStr(DPI.SPECTATE_TARGET).equals("none") && !dp.getBool(DPI.SPECTATING)){
			Player you = main.api.getPlayer(dp.getStr(DPI.SPECTATE_TARGET));
			you.setAllowFlight(true); you.setFlying(true);
			Vector themV = e.getPlayer().getLocation().toVector();
			you.setVelocity(themV.subtract(you.getLocation().toVector()).normalize());
		}
		
		main.api.event(new ScoreboardUpdateEvent(e.getPlayer(), "move"));
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
}