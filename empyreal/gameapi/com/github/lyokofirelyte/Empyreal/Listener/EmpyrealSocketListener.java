package com.github.lyokofirelyte.Empyreal.Listener;

import java.io.BufferedReader;





import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
			String serverName = "";
			
			while ((text = in.readLine()) != null){
				
				if (serverName.equals("")){
					serverName = new String(text);
				}
				
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
					
					case "wcn_cmd":
						
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), in.readLine());
						
					break;
					
					case "chat":
						
						msg = in.readLine();
						
						for (Player p : Bukkit.getOnlinePlayers()){
							p.sendMessage(Utils.AS("&e\u26A1 " + msg));
						}
						
						main.getOut().println(ChatColor.stripColor(Utils.AS("&e\u26A1 " + msg)));
						
					break;
					
					case "globalcast":
						
						msg = Utils.AS("&e\u26A1 &b&1" + in.readLine());
						Bukkit.broadcastMessage(msg);
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title '" + msg + "'");
						
					break;
					
					case "wcn_say":
						
						msg = in.readLine();
						String user = in.readLine();
						
						for (Player p : Bukkit.getOnlinePlayers()){
							p.sendMessage(Utils.AS("&e\u26A1 &7" + user + "&f: " + msg));
						}
						
						main.getOut().println( user + ": " + msg);

					break;
					
					case "END": break;
				}
			}
			
			in.close();
			
		} catch (Exception e){}
	}
}