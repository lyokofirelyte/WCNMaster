package com.github.lyokofirelyte.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.Getter;

import org.bukkit.Bukkit;

import com.github.lyokofirelyte.Empyreal.Command.AutoRegister;

/**
 * Socket listener for when we can't use Bungee's plugin listener channel.
 * To use Bungee's channel, someone has to be online - and that's not always possible.
 */
public class InnerSignListener implements AutoRegister<InnerSignListener>, Runnable {

	private GameServer main;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	int port = 20000;
	
	@Getter
	private InnerSignListener type = this;
		
	public InnerSignListener(GameServer i){
		
		main = i;
		
		new Thread(new Runnable(){
			public void run(){
				try {
					
					ServerSocket listeningSocket = new ServerSocket(port);
						
					while (true){
						Socket incomingConnection = listeningSocket.accept();
						Runnable runnable = new InnerSignListener(main, incomingConnection);
					    Thread thread = new Thread(runnable);
					    thread.start();
					}
						
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
		
	public InnerSignListener(GameServer main, Socket s){
		this.main = main;
		this.socket = s;
	}
		
	public void run(){
			
		try {
		    	
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
				
			String serverName = "";
			String inText = "";
				
			while ((inText = in.readLine()) != null){
					
				if (serverName == ""){
					serverName = new String(inText);
				}
					
				if (inText != null && !inText.equals("END")){
					
					switch (inText){
						
						case "server_boot_complete":
									
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
							}
						    		
							System.out.println("We couldn't find anyone to send to " + serverName + "!");
									
						break;
						
				    	case "server_shutdown":

				    		main.updateAllSigns(serverName, 1, "&f0 Players");
				    		main.updateAllSigns(serverName, 2, "&e&oPre-Lobby");
				    		
				    	break;
				    	
				    	case "game_in_progress":

				    		main.updateAllSigns(serverName, 2, "&c&oIn Progress");
				    		
				    	break;
					}
					
				} else {
					break;
				}
			}
				
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				socket.close();
			}  catch (IOException e){}
		}
	}
}