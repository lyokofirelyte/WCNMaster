package com.github.lyokofirelyte.Platform;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.FireworkShenans;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Platform.Data.PlatformData;
import com.github.lyokofirelyte.Platform.Data.PlatformGameData;
import com.github.lyokofirelyte.Platform.Rounds.PR0;
import com.github.lyokofirelyte.Platform.Rounds.PR1;
import com.github.lyokofirelyte.Platform.Rounds.PR10;
import com.github.lyokofirelyte.Platform.Rounds.PR2;
import com.github.lyokofirelyte.Platform.Rounds.PR3;
import com.github.lyokofirelyte.Platform.Rounds.PR4;
import com.github.lyokofirelyte.Platform.Rounds.PR5;
import com.github.lyokofirelyte.Platform.Rounds.PR6;
import com.github.lyokofirelyte.Platform.Rounds.PR7;
import com.github.lyokofirelyte.Platform.Rounds.PR8;
import com.github.lyokofirelyte.Platform.Rounds.PR9;
import com.github.lyokofirelyte.Platform.Rounds.PlatformRound;

/**
 * This is an old project that I've converted to be compatible with the GameAPI. It's pretty messy.
 */
public class Platform extends JavaPlugin implements GameModule {
	
	@Getter @Setter
	private String jarName = "Platform-1.0";
	
	@Getter @Setter
	private String packageName = "Platform";
	
	public PlatformData data;
	public PlatformGameData gameData;
	public PlatformActionHandler pah;
	
	@Getter @Setter
	private boolean gameStarted = false;
	
	@Getter @Setter
	private int secondsLeft = 60;
	
	@Getter @Setter
	private Empyreal api;

	@Override
	public void onEnable(){
		
		setApi((Empyreal) Bukkit.getPluginManager().getPlugin("Empyreal"));
		getApi().registerModule(this);

		data = api.getInstance(PlatformData.class).getType();
		gameData = api.getInstance(PlatformGameData.class).getType();
		pah = api.getInstance(PlatformActionHandler.class).getType();
		
		List<PlatformRound> rounds = Arrays.asList(new PR0(this), new PR1(this), new PR2(this), new PR3(this), new PR4(this), new PR5(this), new PR6(this), new PR7(this), new PR8(this), new PR9(this), new PR10(this));
		int x = 0;
		
		for (PlatformRound r : rounds){
			data.getRounds().put(x, r);
			x++;
		}
	}
	
	@Override
	public void onDisable(){
		getApi().unregisterModule(this);
	}
	
	@Override
	public void onRegister(){
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "server_boot_complete");
	}
	
	@Override
	public void onPlayerJoin(Player p){
		
		PlatformPlayer gp = new PlatformPlayer(p);
		getApi().registerPlayer(gp);
		
		updateList();
		
		if (p.isOp()){
			gp.getPerms().add("gameserver.staff");
			p.setOp(false);
		}
		
		p.setGameMode(GameMode.SURVIVAL);
		p.setWalkSpeed(0.2f);
		p.setFlying(false);
		p.setAllowFlight(false);
		
		p.teleport(new Location(Bukkit.getWorld("world"), gameData.getCenter().getBlockX(), gameData.getCenter().getBlockY() + 100, gameData.getCenter().getBlockZ()));
		
		if (!isGameStarted() && Bukkit.getOnlinePlayers().size() >= 2){
			
			APIScheduler.DELAY.start(getApi(), "platform_delay", 20*5L, new Runnable(){
				public void run(){
					
					if (Bukkit.getOnlinePlayers().size() >= 2){
						start();
					} else {
						Utils.bc("&c&oToo many players left! Waiting for players...");
						setGameStarted(false);
						APIScheduler.REPEAT.stop("platform_delay_2");
						secondsLeft = 60;
						updateScores();
					}
				}
			});
			
			secondsLeft = 60;
			updateScores();
			
			APIScheduler.REPEAT.start(getApi(), "platform_delay_2", 0L, 20L, new Runnable(){
				public void run(){
					
					if (!gameData.isActive()){
						secondsLeft--;
					} else {
						secondsLeft++;
					}
					
					for (Player p : Bukkit.getOnlinePlayers()){
						p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(Utils.AS("&apLatform " + api.getTimeLeft(secondsLeft)));
					}
				}
			});
			
			Utils.bc("The game is starting in one minute! Players can still join before then.");
			setGameStarted(true);
			
		} else {
			Utils.s(p, "Waiting for players...");
		}
	}
	
	public void start(){
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "game_in_progress");
		pah.formArena(gameData.getCenter());
	}
	
	public void updateScores(){
		for (Player p : Bukkit.getOnlinePlayers()){
			p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(p.getName()).setScore(api.getGamePlayer(p.getUniqueId(), PlatformPlayer.class).getType().getScore());
		}
	}
	
	public void updateList(){
		
		String[] str = new String[Bukkit.getOnlinePlayers().size()];
		int ii = 0;
		
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
			str[ii] = onlinePlayer.getDisplayName();
			ii++;
		}
		
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
			getApi().updateScoreBoard(getApi().getGamePlayer(onlinePlayer.getUniqueId()), "pLatform " + api.getTimeLeft(secondsLeft), str);
		}
	}

	@Override
	public void closing(){
		
		
	}

	@Override
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}

	@Override
	public void onPlayerQuit(Player p){
		api.unregisterPlayer(api.getGamePlayer(p.getUniqueId()));
	}

	@Override
	public void onPlayerChat(GamePlayer<?> gp, String msg) {
		Utils.bc("&7" + gp.getPlayer().getDisplayName() + "&f: " + msg);
	}
	
	public void playFirework(Location l, Type t, Color c){
		
		try {
			api.getInstance(FireworkShenans.class).getType().playFirework(l.getWorld(), l, FireworkEffect.builder().with(t).withColor(c).build());
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	public void gMsg(String m){
		for (Player p : Bukkit.getOnlinePlayers()){
			Utils.s(p, m);
		}
	}
	
	public void msg(String s, String m){
		Utils.s(Bukkit.getPlayer(s), m);
	}
}