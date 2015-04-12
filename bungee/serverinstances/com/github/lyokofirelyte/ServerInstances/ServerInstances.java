package com.github.lyokofirelyte.ServerInstances;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;

public class ServerInstances extends Plugin {
	
	private int currPort = 10000;

	@Override @SneakyThrows
	public void onEnable(){
		
		final ServerSocket ss = new ServerSocket(10000);
		final SocketListener l = new SocketListener(this);
		
		try {
			
			new Thread(new Runnable(){
				@SneakyThrows public void run(){
					try {
						while (true){
							Socket inc = ss.accept();
							new Thread(new SocketListener(l.getMain(), new BufferedReader(new InputStreamReader(inc.getInputStream())), new PrintWriter(inc.getOutputStream()), inc)).start();
						}
					} catch (Exception ee){} finally {
						ss.close();
					}
				}
			}).start();
			
		} catch (Exception e){}
	}
	
	@Override
	public void onDisable(){}
	
	public int nextPort(){
		currPort += 1;
		return currPort;
	}
}