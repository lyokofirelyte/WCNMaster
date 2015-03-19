package com.github.lyokofirelyte.Elysian.Events;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class ElysianSocketListener implements AutoRegister, Runnable {

	private Elysian main;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	int port = 24001;

	public ElysianSocketListener(Elysian i){
		
		main = i;
		
		new Thread(new Runnable(){
			public void run(){
				try {
					
					ServerSocket listeningSocket = new ServerSocket(port);
						
					while (true){
						Socket incomingConnection = listeningSocket.accept();
						Runnable runnable = new ElysianSocketListener(main, incomingConnection);
					    Thread thread = new Thread(runnable);
					    thread.start();
					}
						
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
		
	public ElysianSocketListener(Elysian main, Socket s){
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
					
						case "chat":
							
							Bukkit.broadcastMessage(DivinityUtilsModule.AS("&e\u26A1 " + in.readLine()));
							
						break;
						
						case "setop":
							
							Bukkit.getOfflinePlayer(in.readLine()).setOp(true);
							
						break;
						
						case "globalcast":
							
							String msg = DivinityUtilsModule.AS(in.readLine());
							Bukkit.broadcastMessage("&e\u26A1 &b&l" + msg);
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title '" + msg + "'");
							
						break;
						
						case "o":
							
							String oMsg = in.readLine();
							
							for (Player p : Bukkit.getOnlinePlayers()){
								if (main.api.perms(p, "wa.staff.intern", true)){
									p.sendMessage(DivinityUtilsModule.AS("&4\u273B " + oMsg));
								}
							}

						break;
					}
					
				}
			}
				
		} catch (Exception e){
			
		} finally {
			try {
				in.close();
				out.close();
				socket.close();
			}  catch (IOException e){}
		}
	}
}