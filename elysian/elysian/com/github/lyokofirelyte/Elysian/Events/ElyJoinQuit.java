package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
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
		
		Player pl = e.getPlayer();
		DivinityPlayer p = main.api.getDivPlayer(pl);
		p.set(DPI.AFK_TIME_INIT, 0);
		
		if (main.api.getDivSystem().getList(DPI.AFK_PLAYERS).contains(pl.getName())){
			main.api.getDivSystem().getList(DPI.AFK_PLAYERS).remove(pl.getName());
		}
		
		p.set(DPI.LAST_LOGIN, DivinityUtilsModule.getTimeFull());
		pl.setPlayerListName(main.AS(p.getStr(DPI.DISPLAY_NAME)));
		pl.setDisplayName(p.getStr(DPI.DISPLAY_NAME));
		
		DivinityUtilsModule.customBC("&2(\\__/) " + pl.getDisplayName());
		DivinityUtilsModule.customBC("&2(=^.^=)" + " &e&o" + p.getStr(DPI.JOIN_MESSAGE) + "&e&o");
		pl.sendMessage("");
		
		p.s("&3Welcome back! We're running Elysian & Divinity v2.0");
		p.s(p.getList(DPI.MAIL).size() > 0 ? "Mail time! /mail read or /mail clear." : "&7&oNo new messages.");
		
		defaultCheck(p);
		
		if (p.getBool(MMO.IS_SOUL_SPLITTING)){
			((ElyMMO) main.api.getInstance(ElyMMO.class)).soulSplit.stop(pl, p);
		}
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