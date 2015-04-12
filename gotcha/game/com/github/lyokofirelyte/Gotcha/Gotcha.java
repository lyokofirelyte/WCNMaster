package com.github.lyokofirelyte.Gotcha;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.ChatColor;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Listener.SocketMessageListener.Handler;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class Gotcha extends JavaPlugin implements GameModule {
	
	@Getter @Setter
	private Empyreal api;
	
	@Getter @Setter
	private String packageName = "Gotcha";
	
	@Getter @Setter
	private String jarName = "Gotcha-1.0";
	
	@Getter
	private Map<String, GotchaArena> arenas = new HashMap<String, GotchaArena>();
	
	@Getter @Setter
	private GotchaArena chosenArena;
	
	@Getter @Setter
	private int secondsLeft = 0;
	
	@Getter @Setter
	private boolean gameStarted = false;
	
	@Getter @Setter
	private boolean canShoot = false;
	
	@Getter @Setter
	private GamePlayer<?> currentWinner;

	@Override
	public void onEnable(){
		setApi((Empyreal) Bukkit.getPluginManager().getPlugin("Empyreal"));
		getApi().registerModule(this);
	}
	
	@Override
	public void onDisable(){
		getApi().unregisterModule(this);
	}
	
	@Override
	public void onRegister(){
		
		File file = new File(new GotchaArena("init").getFolderPath());
		file.mkdirs();
		
		for (String arena : file.list()){
			arenas.put(arena.replace(".json", ""), new GotchaArena(arena.replace(".json", "")));
			arenas.get(arena.replace(".json", "")).load();
		}
		
		if (arenas.size() > 0){
			setChosenArena((GotchaArena) arenas.values().toArray()[new Random().nextInt(arenas.size())]);
		}
		
		getApi().sendToSocket("GameServer", Handler.SERVER_BOOT_COMPLETE);
	}
	
	@Override
	public void closing(){
		
		for (GotchaArena arena : arenas.values()){
			arena.save();
		}
	}
	
	@Override
	public void onPlayerChat(GamePlayer<?> gp, String msg){
		Utils.bc("&7" + gp.getPlayer().getDisplayName() + "&f: " + msg);
	}
	
	@Override
	public void onPlayerJoin(Player p){
		
		GamePlayer<GotchaPlayer> player = new GotchaPlayer(p);
		getApi().registerPlayer(player);
		
		if (p.isOp()){
			player.getPerms().add("gameserver.staff");
			p.setOp(false);
		}
		
		p.setGameMode(GameMode.SURVIVAL);
		p.setWalkSpeed(0.2f);
		p.setFlying(false);
		p.setAllowFlight(false);
		
		updateList();
		
		if (arenas.size() > 0){
			
			p.teleport(getChosenArena().toLocation(getChosenArena().getLobby()));
			p.getInventory().clear();
			
			ItemStack i = new ItemStack(Material.DIAMOND_HOE);
			ItemMeta im = i.getItemMeta();
			im.setDisplayName(Utils.AS("&a&oReady!"));
			im.setLore(Arrays.asList(Utils.AS("&d&oGotcha! Laser Weapon")));
			i.setItemMeta(im);
			p.setItemInHand(i);
			
			if (!isGameStarted() && Bukkit.getOnlinePlayers().size() >= 2){
				
				APIScheduler.DELAY.start(getApi(), "gotcha_delay", 20*60L, new Runnable(){
					public void run(){
						
						if (Bukkit.getOnlinePlayers().size() >= 2){
							start();
						} else {
							Utils.bc("&c&oToo many players left! Waiting for players...");
							setGameStarted(false);
							APIScheduler.REPEAT.stop("gotcha_delay_2");
							secondsLeft = 60;
							updateBoardAll();
						}
					}
				});
				
				secondsLeft = 60;
				
				APIScheduler.REPEAT.start(getApi(), "gotcha_delay_2", 0L, 20L, new Runnable(){
					public void run(){
						updateBoardAll();
						secondsLeft--;
					}
				});
				
				Utils.bc("The game is starting in one minute! Players can still join before then.");
				setGameStarted(true);
				
			} else {
				player.s("Waiting for players...");
			}
			
		} else {
			player.s("&c&oNo arenas present - please alert staff of this issue!");
		}
	}
	
	public void start(){
		
		updateList();
		setCanShoot(true);
		secondsLeft = 600;
		
		getApi().sendToSocket("GameServer", Handler.GAME_IN_PROGRESS);
		
		for (Player p : Bukkit.getOnlinePlayers()){
			p.setWalkSpeed(0.4f);
			spawnPlayer(p);
		}
		
		APIScheduler.REPEAT.start(api, "fall_catcher", 100L,  100L, new Runnable(){
			public void run(){
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.getLocation().getBlockY() <= 0){
						spawnPlayer(p);
						Utils.bc("&6" + p.getName() + " &7&ofell into the void");
					}
				}
			}
		});
	}
	
	public void endGame(){
		
		Utils.bc("Moving to lobby in 10 seconds...");
		setCanShoot(false);
		secondsLeft = 10;
		updateBoardAll();
		
		for (Player p : Bukkit.getOnlinePlayers()){
			p.getInventory().clear();
			p.updateInventory();
			p.setWalkSpeed(0.2f);
		}
		
		gameStarted = false;
	}
	
	public void spawnPlayer(Player p){
		p.teleport(chosenArena.toLocation(chosenArena.getSpawnPoints().get(new Random().nextInt(chosenArena.getSpawnPoints().size()))));
	}
	
	public void updateList(){
		
		String[] str = new String[Bukkit.getOnlinePlayers().size()];
		int ii = 0;
		
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
			str[ii] = onlinePlayer.getDisplayName();
			ii++;
		}
		
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
			getApi().updateScoreBoard(getApi().getGamePlayer(onlinePlayer.getUniqueId()), "GOTCHA! " + getTimeLeft(), str);
		}
	}
	
	public void updateBoardAll(){
		
		List<Integer> scores = new ArrayList<Integer>();
		Map<GamePlayer<GotchaPlayer>, Integer> mappedScores = new HashMap<>();
		List<String> finals = new ArrayList<String>();
		
		for (Player p : Bukkit.getOnlinePlayers()){
			GamePlayer<GotchaPlayer> player = getApi().getGamePlayer(p.getUniqueId());
			scores.add(player.getType().getScore());
			mappedScores.put(player, player.getType().getScore());
			p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(Utils.AS("&aGOTCHA! " + getTimeLeft()));
		}
		
		for (Player p : Bukkit.getOnlinePlayers()){
			for (GamePlayer<GotchaPlayer> gg : mappedScores.keySet()){
				p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(gg.getName()).setScore(gg.getType().getScore());	
			}
		}
		
		if (secondsLeft <= 0){
			
			if (canShoot){
				Collections.sort(scores);
				Collections.reverse(scores);
	
				for (int i : scores){
					for (GamePlayer<GotchaPlayer> g : mappedScores.keySet()){
						if (g.getType().getScore() == i){
							finals.add(g.getName());
						}
					}
				}
				
				Utils.bc("&7" + finals.get(0) + " &awas victorious!");
				endGame();
				
			} else {
				APIScheduler.REPEAT.stop("gotcha_delay_2");
				getApi().sendAllToServer("GameServer");
			}
		}
	}
	
	public String getTimeLeft(){
		String time = Math.round(secondsLeft/60) + ":" + (secondsLeft % 60);
		return time.split(":")[1].length() == 1 ? time.replace(":", ":0") : time;
	}
	
	@Override
	public void onPlayerQuit(Player p){
		
		GamePlayer<GotchaPlayer> player = getApi().getGamePlayer(p.getUniqueId());
		
		if (gameStarted){
			Utils.bc("&e" + player.getName() + " &6<-> &econnection lost");
		}
		//TODO handle score sends later
	}
	
	@Override
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}
}