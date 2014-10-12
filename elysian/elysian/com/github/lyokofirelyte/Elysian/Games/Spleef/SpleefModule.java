package com.github.lyokofirelyte.Elysian.Games.Spleef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefGame;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefPlayer;

public class SpleefModule {
	
	private Spleef main;
	public static Map<String, SpleefStorage> data = new THashMap<String, SpleefStorage>();

	public SpleefModule(Spleef i){
		main = i;
	}
	
	public static void s(CommandSender cs, String message){
		cs.sendMessage(AS("&bSpleef &f// &b" + message));
	}

	public static String AS(String message){
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public static boolean doesPlayerExist(UUID uuid){
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.getUniqueId().equals(uuid)){
				return doesPlayerExist(p.getName());
			}
		}
		return false;
	}
	
	public static boolean doesPlayerExist(String name){
		return matchSpleefPlayer(name) != null;
	}
	
	public static boolean doesGameExist(String name){
		return data.containsKey(SpleefDataType.GAME.s() + " " + name);
	}
	
	public static SpleefPlayer matchSpleefPlayer(String name){
		for (SpleefStorage s : data.values()){
			if (s.type().equals(SpleefDataType.PLAYER)){
				if (s.name().contains(name)){
					return s.toPlayer();
				}
			}
		}
		return null;
	}
	
	public static SpleefPlayer getSpleefPlayer(UUID uuid){
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.getUniqueId().equals(uuid)){
				return matchSpleefPlayer(p.getName());
			}
		}
		
		return null;
	}
	
	public static SpleefGame getSpleefGame(String name){
		return data.get(SpleefDataType.GAME.s() + " " + name).toGame();
	}
	
	public static List<SpleefGame> getAllGames(){
		
		List<SpleefGame> list = new ArrayList<SpleefGame>();
		
		for (SpleefStorage s : data.values()){
			if (s.type().equals(SpleefDataType.GAME)){
				list.add(s.toGame());
			}
		}
		
		return list;
	}
	
	public static List<SpleefPlayer> getAllUsers(){
		
		List<SpleefPlayer> list = new ArrayList<SpleefPlayer>();
		
		for (SpleefStorage s : data.values()){
			if (s.type().equals(SpleefDataType.PLAYER)){
				list.add(s.toPlayer());
			}
		}
		
		return list;
	}
}