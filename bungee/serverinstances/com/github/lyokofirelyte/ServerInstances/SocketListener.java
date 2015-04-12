package com.github.lyokofirelyte.ServerInstances;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.BungeeServerInfo;

public class SocketListener implements Runnable {

	@Getter
	private BufferedReader in;
	
	@Getter
	private PrintWriter out;
	
	@Getter
	private Socket socket;
	
	@Getter
	private ServerInstances main;
	
	public SocketListener(ServerInstances main, BufferedReader in, PrintWriter out, Socket s){
		this.in = in;
		this.out = out;
		this.socket = s;
		this.main = main;
	}
	
	public SocketListener(ServerInstances main){
		this.main = main;
	}
	
	@Override @SneakyThrows
	public void run(){
		
		try {
			
			String text = "";
			
			while ((text = in.readLine()) != null){
				
				switch (text){
				
					case "add_server":
						
						String serverName = in.readLine();
						main.getProxy().getServers().put(serverName, new BungeeServerInfo(serverName, new InetSocketAddress(main.nextPort()), "none", true));
						
					break;
					
					case "rem_server":
						
						serverName = in.readLine();
						main.getProxy().getServers().remove(serverName);
						
					break;
				}
			}
			
			in.close();
			
		} catch (Exception e){
			System.out.println("Shutting down socket reader thread for ServerInstances - connection lost");
		} finally {
			socket.close();
		}
	}
}