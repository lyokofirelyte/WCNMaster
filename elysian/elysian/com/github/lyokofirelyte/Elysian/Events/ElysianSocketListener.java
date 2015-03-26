package com.github.lyokofirelyte.Elysian.Events;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class ElysianSocketListener implements AutoRegister, Runnable {

	private Elysian main;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	int port = 24001;
	
	@SneakyThrows
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
			String user = "";
				
			while ((inText = in.readLine()) != null){
					
				if (serverName == ""){
					serverName = new String(inText);
				}
					
				if (inText != null && !inText.equals("END")){
					
					switch (inText){
					
						case "chat":
							
							String msg = in.readLine();
							
							for (Player p : Bukkit.getOnlinePlayers()){
								p.sendMessage(DivinityUtilsModule.AS("&e\u26A1 " + msg));
							}
							
							main.getDefaultOut().println(ChatColor.stripColor(main.AS(msg)));
							
						break;
						
						case "setop":
							
							Bukkit.getOfflinePlayer(in.readLine()).setOp(true);
							
						break;
						
						case "globalcast":
							
							msg = DivinityUtilsModule.AS(in.readLine());
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
						
						case "wcnconsole_user":

							user = in.readLine();
							
							File file = new File("./plugins/Divinity/users/" + Bukkit.getOfflinePlayer(user).getUniqueId().toString() + ".yml");
							
							if (file.exists()){

								YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
								
								if (!yaml.contains("WCN_CONSOLE_2") || yaml.getString("WCN_CONSOLE_2").equals("none")){
									if (Bukkit.getPlayer(user) != null){
										String id = UUID.randomUUID().toString().substring(0, 5);
										yaml.set("WCN_CONSOLE_2", id);
										DivinityUtilsModule.s(Bukkit.getPlayer(user), "Your WCNConsole ID is " + id);
										main.divinity.api.sendToSocket(main.divinity.api.getServerSockets().get("GameServer"), "wcnconsole_continue_user", user);
										yaml.save(file);
									} else {
										main.divinity.api.sendToSocket(main.divinity.api.getServerSockets().get("GameServer"), "wcnconsole_invalid_notonline", user);
									}
								} else {
									main.divinity.api.sendToSocket(main.divinity.api.getServerSockets().get("GameServer"), "wcnconsole_continue_user", user);
								}
							} else {
								main.divinity.api.sendToSocket(main.divinity.api.getServerSockets().get("GameServer"), "wcnconsole_invalid_notonline", user);
							}

						break;
						
						case "wcnconsole_pass":
							
							String pass = in.readLine();
							out = new PrintWriter(socket.getOutputStream(), true);
							
							file = new File("./plugins/Divinity/users/" + Bukkit.getOfflinePlayer(user).getUniqueId().toString() + ".yml");
							YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
							
							if (yaml.contains("WCN_CONSOLE_2") && yaml.getString("WCN_CONSOLE_2").equals(pass)){
								main.divinity.api.sendToSocket(main.divinity.api.getServerSockets().get("GameServer"), "wcnconsole_accepted_pass", user);
							} else {
								main.divinity.api.sendToSocket(main.divinity.api.getServerSockets().get("GameServer"), "wcnconsole_denied_pass", user);
							}
							
						break;
						
						case "wcn_say":
							
							msg = in.readLine();
							
							for (Player p : Bukkit.getOnlinePlayers()){
								p.sendMessage(DivinityUtilsModule.AS("&e\u26A1 &7" + user + "&f: " + msg));
							}
							
							main.getDefaultOut().println(user + ": " + msg);
							
						break;
						
						case "wcn_cmd":
							
							String command = in.readLine();
							boolean perms = false;
							
							Command cmd = main.getCommand(command);
							
							if (cmd != null){
								
								if (Bukkit.getPlayer(user) != null){
									perms = main.api.getDivPlayer(Bukkit.getPlayer(user)).getStr(DPI.PERMS).contains(cmd.getPermission());
								} else {
									file = new File("./plugins/Divinity/users/" + Bukkit.getOfflinePlayer(user).getUniqueId().toString() + ".yml");
									yaml = YamlConfiguration.loadConfiguration(file);
									perms = yaml.contains("PERMS") && yaml.getStringList("PERMS").contains(cmd.getPermission());
								}
								
								if (perms){
									Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
								} else {
									out.println("wcn_no_perms");
								}
								
							} else {
								Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
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