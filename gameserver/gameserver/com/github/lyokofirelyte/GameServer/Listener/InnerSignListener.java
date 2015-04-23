package com.github.lyokofirelyte.GameServer.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.Getter;
import lombok.SneakyThrows;

import com.github.lyokofirelyte.Empyreal.Events.SocketMessageEvent;
import com.github.lyokofirelyte.Empyreal.Listener.Handler;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.GameServer.GameServer;

/**
 * Socket listener for when we can't use Bungee's plugin listener channel.
 * To use Bungee's channel, someone has to be online - and that's not always possible.
 */
public class InnerSignListener implements AutoRegister<InnerSignListener>, Runnable {

	private GameServer main;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	int port = 24000;
	private boolean yes = false;
	
	@Getter
	private InnerSignListener type = this;
		
	public InnerSignListener(GameServer i){
		main = i;
	}
	
	public void start(){
		new Thread(new Runnable(){
			public void run(){
				try {
					
					ServerSocket listeningSocket = new ServerSocket(port);
						
					while (true){
						Socket incomingConnection = listeningSocket.accept();
						Runnable runnable = new InnerSignListener(main, incomingConnection);
					    new Thread(runnable).start();
					}
						
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@SneakyThrows	
	public void run(){
		
		String serverName = "";
		
		try {
		    	
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
				
			String inText = "";
				
			while ((inText = in.readLine()) != null){
					
				serverName = serverName.equals("") ? new String(inText) : serverName;
				if (Handler.containsValue(inText)){
					new SocketMessageEvent(serverName, "GameServer", inText, in.readLine(), in).fire();
				} else if (inText.equalsIgnoreCase("assign_socket")){
					main.getApi().getServerSockets().put(in.readLine(), socket);
				}
			}
				
		} catch (Exception e){
			System.out.println(serverName + " is now offline.");
		} finally {
			try {
				in.close();
				out.close();
				socket.close();
			}  catch (IOException e){}
		}
	}
	
	public InnerSignListener(GameServer main, Socket s){
		this.main = main;
		this.socket = s;
	}
}