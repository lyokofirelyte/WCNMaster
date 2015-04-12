package com.github.lyokofirelyte.Empyreal.Listener;

import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import lombok.Getter;
import lombok.SneakyThrows;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Events.SocketMessageEvent;
import com.github.lyokofirelyte.Empyreal.Listener.SocketMessageListener.Handler;

public class EmpyrealSocketListener implements Runnable {

	@Getter
	private BufferedReader in;
	
	@Getter
	private PrintWriter out;
	
	@Getter
	private ObjectInputStream ois;
	
	@Getter
	private String type;
	
	private Empyreal main;
	
	public EmpyrealSocketListener(Empyreal i, BufferedReader in, PrintWriter out){
		this.in = in;
		main = i;
		type = "string";
	}
	
	public EmpyrealSocketListener(Empyreal i, ObjectInputStream in){
		ois = in;
		main = i;
		type = "object";
	}
	
	@Override @SneakyThrows
	public void run(){
		getClass().getMethod(type).invoke(this);
	}
	
	public void string(){
		
		try {
			
			String text = "";
			String serverName = "";
			
			while ((text = in.readLine()) != null){
				serverName = serverName.equals("") ? new String(text) : serverName;
				if (Handler.containsValue(text)){
					new SocketMessageEvent(serverName, main.getServerName(), text, in.readLine(), in).fire();
				}
			}
			
			in.close();
			
		} catch (Exception e){
			System.out.println("Shutting down socket reader thread for " + main.getServerName() + " - connection lost");
		}
	}
	
	public void object(){
		
		try {
			
			Object obj;
			
			while ((obj = ois.readObject()) != null){
				SocketObject so = (SocketObject) obj;
				new SocketMessageEvent(so).fire();
			}
			
			in.close();
			
		} catch (Exception e){
			System.out.println("Shutting down socket reader thread for " + main.getServerName() + " - connection lost");
		}
	}
}