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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

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
	private int timeLeft = 0;
	
	@Getter @Setter
	private boolean gameStarted = false;

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
			arenas.put(arena, new GotchaArena(arena));
			arenas.get(arena).load();
		}
		
		if (arenas.size() > 0){
			setChosenArena(arenas.get(new Random().nextInt(arenas.size())));
		}
		
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "server_boot_complete");
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
		
		if (arenas.size() > 0){
			
			p.teleport(getChosenArena().toLocation(getChosenArena().getLobby()));
			p.getInventory().clear();
			
			ItemStack i = new ItemStack(Material.DIAMOND_HOE);
			ItemMeta im = i.getItemMeta();
			im.setDisplayName(Utils.AS("&a&oReady!"));
			im.setLore(Arrays.asList(Utils.AS("&d&oGotcha! Laser Weapon")));
			i.setItemMeta(im);
			p.getInventory().addItem(i);
			
			if (!isGameStarted() && Bukkit.getOnlinePlayers().size() >= 3){
				
				APIScheduler.DELAY.start(getApi(), "gotcha_delay", 20*60L, new Runnable(){
					public void run(){
						if (Bukkit.getOnlinePlayers().size() >= 3){
							start();
						} else {
							Utils.bc("&c&oToo many players left! Waiting for players...");
							setGameStarted(false);
						}
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
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "game_in_progress");	
	}
	
	public void updateBoardAll(){
		for (Player p : Bukkit.getOnlinePlayers()){
			GamePlayer<GotchaPlayer> player = getApi().getGamePlayer(p.getUniqueId());
			updateBoard(player);
		}
	}
	
	public void updateBoard(GamePlayer<GotchaPlayer> gp){
		
		List<Integer> scores = new ArrayList<Integer>();
		Map<GamePlayer<GotchaPlayer>, Integer> mappedScores = new HashMap<>();
		List<String> finals = new ArrayList<String>();
		
		for (Player p : Bukkit.getOnlinePlayers()){
			GamePlayer<GotchaPlayer> player = getApi().getGamePlayer(p.getUniqueId());
			scores.add(player.getType().getScore());
			mappedScores.put(player, player.getType().getScore());
		}
		
		Collections.sort(scores);
		Collections.reverse(scores);
		
		for (int i : scores){
			for (GamePlayer<GotchaPlayer> g : mappedScores.keySet()){
				if (g.getType().getScore() == i){
					finals.add(g.getName() + " " + i);
				}
			}
		}
		
		getApi().updateScoreBoard(gp, "&aGOTCHA!",
			finals.toArray(new String[finals.size()])
		);
	}
	
	@Override
	public void onPlayerQuit(Player p){
		
		GamePlayer<GotchaPlayer> player = getApi().getGamePlayer(p.getUniqueId());
		//TODO handle score sends later
	}
	
	@Override
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}
}