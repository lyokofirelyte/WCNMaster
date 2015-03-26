package com.github.lyokofirelyte.GameServer.Listener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.GameServer.GameServer;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class WCNConsoleListener implements Runnable {
	
	@Getter @Setter
	private BufferedReader in;
	
	@Getter @Setter
	private PrintWriter out;
	
	@Getter @Setter
	private Socket socket;

	private GameServer main;

	@SneakyThrows
	public WCNConsoleListener(GameServer i){
		
		main = i;
		
		new Thread(new Runnable(){
			public void run(){
				
				try {
					
					ServerSocket ss = new ServerSocket(24500);
					
					while (true){
						Socket incoming = ss.accept();
						Runnable runnable = new WCNConsoleListener(main, incoming);
					    Thread thread = new Thread(runnable);
					    thread.start();
					}
					
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@SneakyThrows
	public WCNConsoleListener(GameServer main, Socket s){
		setSocket(s);
		setIn(new BufferedReader(new InputStreamReader(getSocket().getInputStream())));
		setOut(new PrintWriter(getSocket().getOutputStream(), true));
		this.main = main;
	}

	@Override @SneakyThrows
	public void run(){
		
		String inText = "";
		String user = "";
		
		try {
			
			while ((inText = in.readLine()) != null){
				switch (inText){
				
					case "wcn_login":
						
						if (user.equals("")){
							
							user = in.readLine();
							
							if (!main.getApi().getServerSockets().containsKey("WCNConsole_" + user)){
								main.getApi().getServerSockets().put("WCNConsole_" + user, getSocket());
							} else {
								out.println("bad_username");
							}
							
						}
						
						main.getApi().sendToSocket(main.getApi().getServerSockets().get("wa"), "wcnconsole_user", user);
						System.out.println("Sending WCNConsole (" + user + ") auth request to WA...");
						
					break;
					
					case "wcn_pass":
						
						main.getApi().sendToSocket(main.getApi().getServerSockets().get("wa"), "wcnconsole_pass", in.readLine());
						
					break;
					
					case "wcn_say":
						
						String server = in.readLine();
						String message = "";
						
						while (true){
							
							String m = in.readLine();
							
							if (m.equals("END")){
								break;
							}
							
							message += message.equals("") ? m : " " + m;
						}
						
						if (main.getApi().getServerSockets().containsKey(server)){
							main.getApi().sendToSocket(main.getApi().getServerSockets().get(server), "wcn_say", message + "\n" + user);
						} else if (server.equals("ALL")){
							main.getApi().sendToAllServerSockets("wcn_say", message + "\n" + user, "WCNConsole_" + user);
							main.getApi().getOut().println(user + ": " + message);
						} else if (server.equals("GameServer")){
							main.getApi().getOut().println(user + ": " + message);
						} else {
							out.println("wcnconsole_server_not_found");
						}
						
						if (server.equals("ALL") || server.equals("GameServer")){
							for (Player p : Bukkit.getOnlinePlayers()){
								p.sendMessage(Utils.AS("&e\u26A1 &7" + user + "&f: " + message));
							}
						}
						
						out.println("wcn_say");
						out.println("&7" + user + "&f: " + message);
						
					break;
					
					case "wcn_serverlist":
						
						String result = "&aGameServer&7, ";
						
						for (String socket : main.getApi().getServerSockets().keySet()){
							result += result.equals("&aGameServer&7, ") ? "&a" + socket : "&7, &a" + socket;
						}
						
						out.println("wcn_serverlist");
						out.println(result);
						
					break;
					
					case "wcn_cmd":
						
						server = in.readLine();
						String command = "";
						
						while (true){
							
							String cmd = in.readLine();
							
							if (cmd.equals("END")){
								break;
							}
							
							command += cmd + " ";
						}
						
						if (main.getApi().getServerSockets().containsKey(server) || server.equals("GameServer")){
							if (!server.equals("GameServer")){
								if (!main.getApi().getServerSockets().get(server).isClosed()){
									main.getApi().sendToSocket(main.getApi().getServerSockets().get(server), "wcn_cmd", command);
								} else {
									out.println("wcnconsole_server_socket_closed");
								}
							} else {
								Command cmd = main.getCommand(command.split(" ")[0]);
								if (cmd != null){
									
								}
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
							}
						} else {
							out.println("wcnconsole_server_not_found");
						}
						
					break;
				}
			}
			
		} catch (Exception e){
			System.out.println("Dev Console (" + user + ") is now offline.");
			main.getApi().getServerSockets().remove("WCNConsole_" + user);
		} finally {
			in.close();
			out.close();
			socket.close();
		}
	}
}