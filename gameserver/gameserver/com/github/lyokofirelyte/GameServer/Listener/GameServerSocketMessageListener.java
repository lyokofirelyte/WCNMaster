package com.github.lyokofirelyte.GameServer.Listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.lyokofirelyte.Empyreal.Events.SocketMessageEvent;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.GameServer.GameServer;
import com.github.lyokofirelyte.GameServer.GameSign;

/**
 * Handles GameServer specific Socket events. The similar SocketMessageListener in Empyreal will also fire.
 * If it does not rely on GameServer specific methods, place any other enums in Empyreal instead.
 */
public class GameServerSocketMessageListener implements AutoRegister<GameServerSocketMessageListener>, Listener { 

	private GameServer main;
	
	@Getter
	private GameServerSocketMessageListener type = this;
	
	public GameServerSocketMessageListener(GameServer i){
		main = i;
	}
	
	@EventHandler
	public void onSocketMessage(SocketMessageEvent e){
		
		if (Handler.containsValue(e.getReason().toUpperCase())){
			Handler h = Handler.valueOf(e.getReason().toUpperCase());
			SocketHandler handler = h.getHandler();
			
			if (handler.checkConditions(main)){
				h.run(e);
			}
		}
	}
	
	@SuppressWarnings({"unused", "deprecation"})
	public enum Handler {
		
		/**
		 * Used to indicate that the server can now send people over.
		 * This also updates the sign and sends the first person who clicked over automatically.
		 */
		SERVER_BOOT_COMPLETE(new SocketHandler(){
			
			private GameServer main;
			
			public void start(String data){
				
				String serverName = e.getFromServer();
				
				if (main.getServerDeployQueue().containsKey(serverName)){
	    			
					if (Bukkit.getPlayer(main.getServerDeployQueue().get(serverName)) != null){
						main.getApi().sendToServer(main.getServerDeployQueue().get(serverName), serverName);
					}
			    			
					main.getServerDeployQueue().remove(serverName);
			    			
					for (GameSign sign : main.getSigns().values()){
						if (sign.getServerName().equals(serverName)){
							sign.updateLine(2, "&a&oLobby");
						}
					}
					
				} else {
					System.out.println("We couldn't find anyone to send to " + serverName + "!");
				}
			}
			
			public boolean checkConditions(GameServer main){
				this.main = main;
				return true;
			}
		}),
		
		/**
		 * Allows any server to forward a message to all other servers.
		 */
		FORWARD(new SocketHandler(){
			
			private GameServer main;
			
			@SneakyThrows
			public void start(String data){
				main.getApi().sendToAllServerSockets(data, e.getIn().readLine());
			}
			
			public boolean checkConditions(GameServer main){
				this.main = main;
				return true;
			}
		}),
		
		/**
		 * Same as forward, but does not send to servers listed at the end.
		 */
		FORWARD_EXCLUDE(new SocketHandler(){
			
			private GameServer main;
			
			@SneakyThrows
			public void start(String data){

				String extra = e.getIn().readLine();
				List<String> servers = new ArrayList<String>(Arrays.asList(data, extra));
				
				for (String server : e.getIn().readLine().split(",")){
					servers.add(server);
				}
				
				main.getApi().sendToAllServerSockets(servers.toArray(new String[servers.size()]));
			}
			
			public boolean checkConditions(GameServer main){
				this.main = main;
				return true;
			}
		}),
		
		/**
		 * Called when the server is shutting down. Resets signs.
		 */
		SERVER_SHUTDOWN(new SocketHandler(){
			
			private GameServer main;
			
			public void start(String data){
				main.updateAllSigns(e.getFromServer(), 1, "&f0 Players");
	    		main.updateAllSigns(e.getFromServer(), 2, "&e&oPre-Lobby");
			}
			
			public boolean checkConditions(GameServer main){
				this.main = main;
				return true;
			}
		}),
		
		/**
		 * Alerts the GameServer to block people from coming in and updates the sign.
		 */
		GAME_IN_PROGRESS(new SocketHandler(){
			
			private GameServer main;
			
			public void start(String data){
				main.updateAllSigns(e.getFromServer(), 2, "&c&oIn Progress");
			}
			
			public boolean checkConditions(GameServer main){
				this.main = main;
				return true;
			}
		}),
		
		/**
		 * Removes the server connection completely and closes the socket.
		 */
		REMOVE_SOCKET(new SocketHandler(){
			
			private GameServer main;
			
			@SneakyThrows
			public void start(String data){
				main.getApi().getServerSockets().get(e.getFromServer()).close();
	    		main.getApi().getServerSockets().remove(e.getFromServer());
			}
			
			public boolean checkConditions(GameServer main){
				this.main = main;
				return true;
			}
		});
		
		Handler(SocketHandler m){
			handler = m;
		}
		
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
	}
	
	interface SocketHandler {
		public boolean checkConditions(GameServer main);
	}
}