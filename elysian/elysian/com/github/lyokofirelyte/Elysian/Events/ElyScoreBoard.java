package com.github.lyokofirelyte.Elysian.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Commands.ElyProtect;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;

public class ElyScoreBoard implements Listener, AutoRegister {
	
	private Elysian main;
	
	public ElyScoreBoard(Elysian i){
		main = i;
	}
	
	@EventHandler
	public void onScoreBoard(ScoreboardUpdateEvent e){
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinitySystem system = main.api.getDivSystem();
		
		if (dp.getBool(DPI.IN_GAME)){
			return;
		}
		
		if (e.isCancelled() || (!dp.getBool(DPI.SCOREBOARD_TOGGLE) && !e.getReason().equals("required"))){
			if (!dp.getBool(DPI.SCOREBOARD_TOGGLE)){
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
			return;
		}
		
		if (e.getReason().contains("game")){
			return;
		}
		
		ElySkill skill = dp.getStr(DPI.LAST_ELYSS_SKILL).equals("none") ? null : ElySkill.valueOf(dp.getStr(DPI.LAST_ELYSS_SKILL).toUpperCase());
		String[] str = skill != null ? dp.getStr(skill).split(" ") : new String[]{};
		long needed = skill != null ? Math.round(Double.parseDouble(str[2]) - Double.parseDouble(str[1])) : 0;
		int mobs = 0;

		String[] rounds = new String[]{
				dp.getInt(DPI.BALANCE) + "",
				dp.getInt(DPI.EXP) + "",
				needed + ""
		};
		
		for (int i = 0; i < rounds.length; i++){
			switch (rounds[i].length()){
				case 6:
					rounds[i] = rounds[i].substring(0, 3) + "k"; break;
				case 7:
					rounds[i] = rounds[i].substring(0, 1) + "." + rounds[i].substring(1, 2) + "m"; break;
				case 8:
					rounds[i] = rounds[i].substring(0, 2) + "." + rounds[i].substring(2, 3) + "m"; break;
				case 9: case 10: case 11: case 12: case 13: case 14:
					rounds[i] = "LOTS!"; break;
			}
		}
		
		for (String mob : dp.getList(DPI.MOB_COUNTS)){
			if (mob.split(" ")[0].equalsIgnoreCase(dp.getStr(DPI.LAST_ELYSS_KILL))){
				mobs = Integer.parseInt(mob.split(" ")[1]);
				break;
			}
		}
		
		String[] scoreNames = new String[]{
				
			" ",
			"&b" + getLoc(p),
			"  ",
			"&bS: &c" + rounds[0],
			"&eE: &c" + rounds[1],
			"&4B: &c" + dp.getInt(MMO.VAMP_BAR) + "%",
			"&9P: &c" + dp.getInt(DPI.PARAGONS),
			"   ",
			"&3Online: &c" + Bukkit.getOnlinePlayers().length,
			"&3RG: &c" + ((ElyProtect) main.api.getInstance(ElyProtect.class)).isInAnyRegion(p.getLocation()),
			"    ",
			"&2TR: &c" + dp.getStr(DPI.LAST_ELYSS_SKILL),
			"&2XL: &c" + rounds[2],
			"&2FI: &c" + dp.getStr(DPI.LAST_ELYSS_KILL),
			"&2TK: &c" + mobs,
			"     "
		};
		
		List<Boolean> diff = new ArrayList<Boolean>();
		
		for (int i = 0; i < scoreNames.length; i++){
			
			scoreNames[i] = scoreNames[i].length() > 16 ? scoreNames[i].substring(0, 15) : scoreNames[i];
			
			if (dp.getList(DPI.PREVIOUS_BOARD_INFO).size() > i){
				diff.add(!scoreNames[i].equals(dp.getList(DPI.PREVIOUS_BOARD_INFO).get(i)));
			}
		}
		
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective first = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		List<String> old = new ArrayList<String>(dp.getList(DPI.PREVIOUS_BOARD_INFO));
		List<String> newOld = new ArrayList<String>();
		
		if (first == null || old.size() == 0){
			
			first = board.registerNewObjective("wa", "dummy");
			first.setDisplaySlot(DisplaySlot.SIDEBAR);
			old = new ArrayList<String>();
			
			for (int x = 0; x < scoreNames.length; x++){
				Score s = first.getScore(main.AS(scoreNames[x]));
				s.setScore(scoreNames.length - (x+1));
				old.add(scoreNames[x]);
			}
			
			dp.set(DPI.PREVIOUS_BOARD_INFO, old);
			p.setScoreboard(board);
			
		} else {
			
			for (int x = 0; x < scoreNames.length; x++){
				if (diff.size() > x && diff.get(x)){
					p.getScoreboard().resetScores(main.AS(old.get(x)));
					Score s = first.getScore(main.AS(scoreNames[x]));
					s.setScore(scoreNames.length - (x+1));
				}
				newOld.add(scoreNames[x]);
			}
			
			dp.set(DPI.PREVIOUS_BOARD_INFO, newOld);
		}
		
		if (system.getList(DPI.AFK_PLAYERS).contains(p.getName())){
			first.setDisplayName("§7[ afk " + getMinutes(Long.parseLong(dp.getStr(DPI.AFK_TIME))) + " ]");
		} else {
			first.setDisplayName("§6" + getShortDate());
		}
	}
	
	private String getShortDate(){
		String[] full = DivinityUtilsModule.getTimeFull().split(" ");
		return full[0].substring(0, 3) + ", " + full[1].substring(0, 3) + " " + full[2] + " " + full[3]; 
	}
	
	private String getLoc(Player p){
		
		String loc = Math.round(p.getLocation().toVector().getX()) + " " + Math.round(p.getLocation().toVector().getY()) + " " + Math.round(p.getLocation().toVector().getZ());
		
		if (loc.length() > 16){
			loc = loc.substring(0, 15);
		}
		
		return loc;
	}
	
	private String getMinutes(Long l){
		Long time = System.currentTimeMillis() - l;
		Long seconds = time/1000;
		return ((seconds/60)+3) + "";
	}
}