package com.github.lyokofirelyte.Empyreal.Listener;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Events.SocketMessageEvent;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;
import com.github.lyokofirelyte.GameServer.Listener.SQLQueue;

public class SocketMessageListener implements AutoRegister<SocketMessageListener>, Listener { 

	private Empyreal main;
	
	@Getter
	private SocketMessageListener type = this;
	
	public SocketMessageListener(Empyreal i){
		main = i;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSocketMessage(SocketMessageEvent e){
		
		Handler h = Handler.valueOf(e.getReason().toUpperCase());
		SocketHandler handler = e.getHandler() == null ? h.getHandler() : e.getHandler();
		
		if (handler != null && handler.checkConditions(main)){
			h.run(e);
		}
	}
	
	@SuppressWarnings({"deprecation"})
	public enum Handler {
		
		SAVE_OBJECT_TO_SQL(new ObjectSocketHandler(){
			
			private Empyreal main;
			
			public void start(SocketObject obj){
				
				switch (SocketObjectType.valueOf(obj.getClazz())){
				
					case DIVINITY_STORAGE:
						
						DivinityStorageModule dp = (DivinityStorageModule) obj.getObj();
						main.getInstance(EmpyrealSQL.class).getType().saveMapToDatabase(dp.getTable(), dp);
						
					break;
				}
			}
			
			public boolean checkConditions(Empyreal main){
				this.main = main;
				return true;
			}
		}),
		
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
		
		/**
		 * Adds data to write to the SQL database.
		 */
		SQL_WRITE(new StringSocketHandler(){
			
			private Empyreal main;
			
			public void start(String data){
				main.getInstance(SQLQueue.class).getType().getQueue().add(data);
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
				getHandler().getClass().getMethod("start").invoke(getHandler(), event.getMessage());
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
	}
	
	public enum SocketObjectType {
		
		DIVINITY_STORAGE(DivinityStorageModule.class);
		
		SocketObjectType(Class<?> clazz){
			this.clazz = clazz;
		}
		
		Class<?> clazz;
		
		public static SocketObjectType valueOf(Class<?> clazz){
			
			for (SocketObjectType sot : values()){
				if (sot.clazz.equals(clazz)){
					return sot;
				}
			}
			
			return null;
		}
	}
	
	public interface SocketHandler {
		public boolean checkConditions(Empyreal main);
	}
	
	public interface StringSocketHandler extends SocketHandler {
		public void start(String msg);
	}
	
	public interface ObjectSocketHandler extends SocketHandler {
		public boolean checkConditions(Empyreal main);
		public void start(SocketObject obj);
	}
}