package com.github.lyokofirelyte.Empyreal.Listener;

import java.io.BufferedReader;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Utils;

import lombok.Getter;

public class EmpyrealSocketListener implements Runnable {

	@Getter
	private BufferedReader in;
	
	public EmpyrealSocketListener(BufferedReader in){
		this.in = in;
	}
	
	@Override
	public void run(){
		
		try {
			
			String text = "";
			String serverName = in.readLine();
			
			while ((text = in.readLine()) != null){
				
				switch (text){
				
					case "o":
						
						String msg = in.readLine();
						
						for (Player p : Bukkit.getOnlinePlayers()){
							if (p.isOp()){
								p.sendMessage(Utils.AS("&4\u273B " + msg));
							}
						}
						
					break;
					
					case "chat":
						
						Bukkit.broadcastMessage(Utils.AS("&e\u26A1 " + in.readLine()));
						
					break;
					
					case "END": break;
				}
			}
			
			in.close();
			
		} catch (Exception e){}
	}
}