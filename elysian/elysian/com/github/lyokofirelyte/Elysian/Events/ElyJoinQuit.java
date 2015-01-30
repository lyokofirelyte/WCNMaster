package com.github.lyokofirelyte.Elysian.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class ElyJoinQuit implements Listener, AutoRegister {
	
	private Elysian main;
	
	public ElyJoinQuit(Elysian i){
		main = i;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		
		e.setJoinMessage(null);
		final Player pl = e.getPlayer();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
			
			public void run(){
				
				DivinityPlayer p = main.api.getDivPlayer(pl);
				pl.removePotionEffect(PotionEffectType.FAST_DIGGING);
				
				p.set(DPI.AFK_TIME_INIT, 0);
				
				if (main.api.getDivSystem().getList(DPI.AFK_PLAYERS).contains(pl.getName())){
					main.api.getDivSystem().getList(DPI.AFK_PLAYERS).remove(pl.getName());
				}
				
				p.set(DPI.LAST_LOGIN, DivinityUtilsModule.getTimeFull());
				pl.setPlayerListName(main.AS(p.getStr(DPI.DISPLAY_NAME)));
				pl.setDisplayName(p.getStr(DPI.DISPLAY_NAME));
				
				defaultCheck(p);
				
				if (p.getBool(MMO.IS_SOUL_SPLITTING)){
					((ElyMMO) main.api.getInstance(ElyMMO.class)).soulSplit.stop(pl, p);
				}
				
				List<String> users = new ArrayList<String>(main.api.getDivSystem().getStringList("PRE_APPROVED"));
				if(users.contains(pl.getName())){
					System.out.println(users.contains(pl.getName()));
					main.api.getDivPlayer(pl.getName()).getList(DPI.PERMS).add("wa.member");
					main.api.getPlayer(pl.getName()).performCommand("rankup");

					p.s("You have been pre-approved by WA Staff! Enjoy!");
					
					users.remove(pl.getName());
					main.api.getDivSystem().set("PRE_APPROVED", users);
				 }
				 
				 if (!p.getStr(DPI.RING_LOC).equals("none")){
					 main.api.event(new DivinityTeleportEvent(pl, p.getLoc(DPI.RING_LOC)));
					 p.set(DPI.RING_LOC, "none");
					 p.set(DPI.DISABLED, false);
					 pl.setFlySpeed(0.2f);
					 p.err("You logged out during flight. *slaps*");
				 }
				
				if (!p.getBool(DPI.PVP_CHOICE) && pl.hasPlayedBefore()){
					main.s(pl, "PVP POLICY HAS CHANGED! You can turn pvp on or off at spawn!");
					main.s(pl, "&6&lPlease type /pvp on or /pvp off before playing!");
					main.s(pl, "If you die during pvp, you won't get a death chest.");
					main.s(pl, "You can turn pvp on/off at spawn at anytime.");
					p.set(DPI.DISABLED, true);
					return;
				}
				
				p.set(DPI.PVP_CHOICE, true);
				
				DivinityUtilsModule.customBC("&2(\\__/) " + pl.getDisplayName());
				DivinityUtilsModule.customBC("&2(=^.^=)" + " &e&o" + p.getStr(DPI.JOIN_MESSAGE) + "&e&o");
				pl.sendMessage("");
				
				p.s("&3Welcome back! We're running Elysian & Divinity v2.0");
				p.s(p.getList(DPI.MAIL).size() > 0 ? "Mail time! /mail read or /mail clear." : "&7&oNo new messages.");

			}}, 5L);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		
		ElyMMO mmo = (ElyMMO) main.api.getInstance(ElyMMO.class);
		e.setQuitMessage(null);
		
		Player pl = e.getPlayer();
		DivinityPlayer p = main.api.getDivPlayer(e.getPlayer());
		p.set(DPI.LAST_LOGOUT, DivinityUtilsModule.getTimeFull());
		p.set(DPI.LOGOUT_LOCATION, pl.getLocation());
		p.set(DPI.DISPLAY_NAME,  pl.getDisplayName());
		p.set(DPI.SPECTATING, false);
		
		main.api.cancelTask("rings_task_" + e.getPlayer().getName());
		
		if (!p.getStr(DPI.SPECTATE_TARGET).equals("none")){
			main.api.getDivPlayer(p.getStr(DPI.SPECTATE_TARGET)).set(DPI.SPECTATE_TARGET, "none");
			main.api.getDivPlayer(p.getStr(DPI.SPECTATE_TARGET)).set(DPI.SPECTATING, false);
			p.set(DPI.SPECTATE_TARGET, "none");
		}
		
		try {
			if (mmo.patrols.doesPatrolExistWithPlayer(pl)){
				mmo.patrols.getPatrolWithPlayer(pl).getMembers().remove(pl.getName());
			}
		} catch (Exception ee){}
		
		DivinityUtilsModule.customBC("&4(\\__/) " + pl.getDisplayName());
		DivinityUtilsModule.customBC("&4(=-.-=)" + " &e&o" + p.getStr(DPI.QUIT_MESSAGE) + "&e&o");
		p.clearEffects();
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e){
		e.setLeaveMessage(null);
	}
	
	private void defaultCheck(DivinityPlayer p){
		
		for (ElySkill s : ElySkill.values()){
			if (p.getStr(s).equals("none")){
				p.set(s, "0 0 100");
			}
		}
		
		if (p.getStr(DPI.XP_DISP_NAME_TOGGLE).equals("none")){
			p.set(DPI.XP_DISP_NAME_TOGGLE, true);
		}
	}
}