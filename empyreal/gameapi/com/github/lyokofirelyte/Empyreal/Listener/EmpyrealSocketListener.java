package com.github.lyokofirelyte.Empyreal.Listener;

import java.io.BufferedReader;



import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Utils;

import lombok.Getter;

public class EmpyrealSocketListener implements Runnable {

	@Getter
	private BufferedReader in;
	
	private Empyreal main;
	
	public EmpyrealSocketListener(Empyreal i, BufferedReader in){
		this.in = in;
		main = i;
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
							if (p.isOp() || main.getGamePlayer(p.getUniqueId()).getPerms().contains("gameserver.staff")){
								p.sendMessage(Utils.AS("&4\u273B " + msg));
							}
						}
						
					break;
					
					case "setop":
						
						Bukkit.getOfflinePlayer(in.readLine()).setOp(true);
						
					break;
					
					case "chat":
						
						Bukkit.broadcastMessage(Utils.AS("&e\u26A1 " + in.readLine()));
						
					break;
					
					case "globalcast":
						
						msg = Utils.AS(in.readLine());
						Bukkit.broadcastMessage("&e\u26A1 &b&l" + msg);
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title '" + msg + "'");
						
					break;
					
					case "END": break;
				}
			}
			
			in.close();
			
		} catch (Exception e){}
	}
}