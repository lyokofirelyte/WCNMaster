package com.github.lyokofirelyte.Empyreal.Listener;

import java.lang.reflect.Method;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Events.SocketMessageEvent;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public enum Handler {

	/**
	 * Received on all servers if the player is staff.
	 */
	O_CHAT(new StringSocketHandler(){
		
		private Empyreal main;
		
		public void start(String msg){
			for (Player p : Bukkit.getOnlinePlayers()){
				if (p.isOp() || main.getGamePlayer(p.getUniqueId()).getPerms().contains("gameserver.staff")){
					p.sendMessage(Utils.AS("&4\u273B " + msg));
				}
			}
		}
		
		public boolean checkConditions(Empyreal main){
			this.main = main;
			return true;
		}
	}),
	
	/**
	 * Attempts to make the player OP, even if offline.
	 */
	SET_OP(new StringSocketHandler(){

		public void start(String player){
			Bukkit.getOfflinePlayer(player).setOp(true);
		}
		
		public boolean checkConditions(Empyreal main){
			return true;
		}
	}),
	
	/**
	 * Global chat sent to GameServer, Creative, and WA.
	 */
	GLOBAL_CHAT(new StringSocketHandler(){
		
		public void start(String message){
			Utils.bc("&e\u26A1 " + message);
		}
		
		public boolean checkConditions(Empyreal main){
			return main.getServerName().equals("wa") || main.getServerName().equals("GameServer") || main.getServerName().equals("Creative");
		}
	}),
	
	/**
	 * Broadcast message to all servers.
	 */
	GLOBAL_BROADCAST(new StringSocketHandler(){
		
		public void start(String message){
			Utils.bc("&e\u26A1 " + message);
		}
		
		public boolean checkConditions(Empyreal main){
			return true;
		}
	}),
	
	SQL_WRITE(new StringSocketHandler(){
		
		private Empyreal main;
		
		public void start(String msg){
			main.getInstance(EmpyrealSQL.class).getType().write(msg);
		}
		
		public boolean checkConditions(Empyreal main){
			this.main = main;
			return main.getServerName().equals("GameServer");
		}
	}),
	
	/**
	 * Below are GameServer specific handlers that are inside of the GameServerSocketMessageListener.
	 * These have to be listed so that you can type Handler.<?> for accessing api.sendToSocket(..);
	 */
	
	SERVER_BOOT_COMPLETE(),
	FORWARD(),
	FORWARD_EXCLUDE(),
	SERVER_SHUTDOWN(),
	GAME_IN_PROGRESS(),
	REMOVE_SOCKET();
	
	Handler(SocketHandler m){
		handler = m;
	}
	
	Handler(){}
	
	@Getter
	SocketHandler handler;
	
	static @Getter
	SocketMessageEvent e;

	public void run(SocketMessageEvent event){
		
		e = event;
		
		try {
			for (Method m : getHandler().getClass().getMethods()){
				if (m.getName().equals("start")){
					m.invoke(getHandler(), event.getMessage());
					break;
				}
			}
		} catch (Exception e){
			System.out.println("Socket Handler " + name() + " was unable to pass!");
			e.printStackTrace();
		}
	}
	
	public static boolean containsValue(String value){
		
		for (Handler h : Handler.values()){
			if (h.toString().equalsIgnoreCase(value)){
				return true;
			}
		}
		
		return false;
	}
	
	public interface SocketHandler {
		public boolean checkConditions(Empyreal main);
	}

	public interface StringSocketHandler extends SocketHandler {
		public void start(String msg);
	}
}