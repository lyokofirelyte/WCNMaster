package com.github.lyokofirelyte.Empyreal.Listener;

import java.io.BufferedReader;
import java.io.PrintWriter;

import lombok.Getter;
import lombok.SneakyThrows;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Events.SocketMessageEvent;

public class EmpyrealSocketListener implements Runnable {

	@Getter
	private BufferedReader in;
	
	@Getter
	private PrintWriter out;
	
	private Empyreal main;
	
	public EmpyrealSocketListener(Empyreal i, BufferedReader in, PrintWriter out){
		this.in = in;
		main = i;
	}
	
	@Override
	public void run(){
		string();
	}

	public void string(){
		
		try {
			
			System.out.println("Starting Empyreal Socket String Reader");
			String text = "";
			String serverName = "";
			
			while ((text = in.readLine()) != null){
				serverName = serverName.equals("") ? new String(text) : serverName;
				if (Handler.containsValue(text)){
					new SocketMessageEvent(serverName, main.getServerName(), text, in.readLine(), in).fire();
				}
			}
			
		} catch (Exception e){
			System.out.println("Shutting down socket reader thread for " + main.getServerName() + " - connection lost");
			main.setReconnectInProgress(true);
		} finally {
			try {
				out.close();
				in.close();
			} catch (Exception e){}
		}
	}
}