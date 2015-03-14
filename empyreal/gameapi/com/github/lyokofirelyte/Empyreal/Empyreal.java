package com.github.lyokofirelyte.Empyreal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.github.lyokofirelyte.Empyreal.Command.CommandRegistry;
import com.github.lyokofirelyte.Empyreal.Listener.BungeeListener;
import com.github.lyokofirelyte.Empyreal.Listener.EmpyrealSocketListener;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.ConsolePlayer;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Empyreal extends JavaPlugin {
	
    public Map<List<String>, Object> commandMap = new HashMap<>();
    
    @Getter @Setter
    private Map<UUID, GamePlayer<?>> players = new HashMap<UUID, GamePlayer<?>>();
    
	@Getter @Setter
	private Map<String, AutoRegister<?>> clazzez = new HashMap<String, AutoRegister<?>>();
	
	@Getter @Setter
	private List<GameModule> gameModules = new ArrayList<GameModule>();
	
	@Getter
	private Map<String, Integer> tasks = new HashMap<String, Integer>();
	
	@Getter
	private Map<String, List<String>> previousBoards = new HashMap<String, List<String>>();
	
	@Getter @Setter
	private Map<String, Socket> serverSockets = new HashMap<String, Socket>();

	@Getter @Setter
	private CommandRegistry commandRegistry;
	
	@Getter @Setter
	private String serverName = "";

	@Override @SneakyThrows
	public void onEnable(){
		
		String thisFile = new File("javabestlanguage").getAbsolutePath();
		setServerName(thisFile.split("\\" + File.separator)[thisFile.split("\\" + File.separator).length-2]);
		
		registerSockets();
		
		players.put(new ConsolePlayer().getUUID(), new ConsolePlayer());
		commandRegistry = new CommandRegistry(this);
		commandRegistry.registerAll(this, "Empyreal", "Empyreal-1.0.jar");
		
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", (PluginMessageListener) getInstance(BungeeListener.class));
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		APIScheduler.REPEAT.start(this, "server_check", 1200L, 1200L, new Runnable(){
			public void run(){

				if (!getServerName().equals("GameServer") && !getServerName().equals("") && !getServerName().equals("Creative")){
					if (Bukkit.getOnlinePlayers().size() <= 0){
						Bukkit.getServer().shutdown();
					}
				}
			}
		});
	}
	
	private void registerSockets(){
		
		try {
		
			System.out.println("Registering sockets for " + getServerName() + "...");
			
			if (!serverName.equals("GameServer")){
				serverSockets.put("GameServer", new Socket("127.0.0.1", 24000));
			}

			serverSockets.put("wa", new Socket("127.0.0.1", 24001));
			
			if (!serverName.equals("GameServer")){
				assignSocket();
			}
			
			System.out.println("Sockets registered!");
			
		} catch (Exception e){
			System.out.println("Failed to register sockets. Retrying in 2 seconds...");
			APIScheduler.DELAY.start(this, "init", 40L, new Runnable(){
				public void run(){
					registerSockets();
				}
			});
		}
	}
	
	@Override
	public void onDisable(){
		
		for (GameModule module : gameModules){
			if (gameModules.contains(module)){
				unregisterModule(module);
			}
		}
		
		if (!serverName.equals("GameServer")){
			sendToSocket(getServerSockets().get("GameServer"), "server_shutdown");
			sendToSocket(getServerSockets().get("GameServer"), "remove_socket");
		}
	}
	
	@SneakyThrows
	public static void saveJSON(String completePath, JSONObject obj){
		
		File file = new File(completePath);
		
		if (!file.exists()){
			file.createNewFile();
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(obj.toJSONString());

		FileWriter writer = new FileWriter(completePath);
		writer.write(gson.toJson(je));
		writer.flush();
		writer.close();
	}
	
	public void deployServer(String scriptName){
		
		String ext = System.getProperty("os.name").contains("Windows") ? ".bat" : ".sh";
		
		try {
			Runtime.getRuntime().exec("../" + scriptName + ext);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public <T> AutoRegister<T> getInstance(Class<?> clazz){
		return (AutoRegister<T>) clazzez.get(clazz.toString());
	}
	
	public <T> GamePlayer<T> getGamePlayer(UUID uuid){
		return (GamePlayer<T>) players.get(uuid);
	}
	
	public <T> GamePlayer<T> getConsolePlayer(){
		return (GamePlayer<T>) players.get(new ConsolePlayer().getUUID());
	}
	
	public void registerPlayer(GamePlayer<?> player){
		players.put(player.getUUID(), player);
	}
	
	public void unregisterPlayer(GamePlayer<?> player){
		players.remove(player.getUUID());
	}
	
	public void registerModule(GameModule module){
		gameModules.add(module);
		commandRegistry.registerAll(module, module.getPackageName(), module.getJarName());
		module.onRegister();
	}
	
	public void unregisterModule(GameModule module){
		if (gameModules.contains(module)){
			gameModules.remove(module);
			module.closing();
		}
	}
	
	@SneakyThrows
	public static JSONMap<Object, Object> loadJSON(String filePath){
		
		File file = new File(filePath);
		JSONMap<Object, Object> map = new JSONMap();
		
		if (file.exists()){
			
			JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(file.getPath()));
			
			for (Object key : obj.keySet()){
				map.set(key, obj.get(key));
			}
			
		} else {
			System.out.println("File does not exist: " + filePath + "!");
		}
		
		return map;
	}
	
	/**
	 * Only call this from the GameServer. If you need to use this from somewhere else (ex: Gotcha), send a forward to the game server.
	 * Ex: sendToSocket(getServerSockets().get("GameServer"), "forward", "reason", "message"); <br />
	 * Add any servers you don't want to be sent at the end!
	 */
	@SneakyThrows
	public void sendToAllServerSockets(String... msgs){
		
		List<String> noSend = new ArrayList<String>();
		
		if (msgs.length >= 3){
			for (int i = 2; i < msgs.length; i++){
				noSend.add(msgs[i]);
			}
		}
		
		for (String socket : getServerSockets().keySet()){
			if (getServerSockets().get(socket) != null){
				if (!noSend.contains(socket)){
					sendToSocket(getServerSockets().get(socket), msgs[0], msgs[1]);
				}
			}
		}
	}
	
	@SneakyThrows
	public void sendToSocket(Socket socket, String... msgs){
		
		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
		
		pw.println(getServerName());
		
		for (String msg : msgs){
			pw.println(msg);
		}
	}
	
	public void sendPacketAll(String subchannel, Map<String, String> packet){
		
		String str = "";
		
		for (String packetKey : packet.keySet()){
			str += packetKey + " %keysplit% " + packet.get(packetKey) + " %valsplit% ";
		}
		
		sendPluginMessageAll(subchannel, str);
	}
	
	public Map<String, String> readPacket(String msgin){
		
		Map<String, String> map = new HashMap<String, String>();
		
		for (String key : msgin.split(" %valsplit% ")){
			String[] spl = key.split(" %keysplit% ");
			map.put(spl[0], spl[1]);
		}
		
		return map;
	}
	
	public void requestServerList(String player){
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("GetServers");
				Bukkit.getPlayer(player).sendPluginMessage(this, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void updateServerName(){
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("GetServer");
				Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(this, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void requestIP(String player){
		
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("IP");
				Bukkit.getPlayer(player).sendPluginMessage(this, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void sendAllToServer(final String server){
		for (Player player : Bukkit.getOnlinePlayers()){
			sendToServer(player.getName(), server);
		}
	}
	
	public void sendToServer(String player, String server){
		
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("ConnectOther");
				out.writeUTF(player);
				out.writeUTF(server);
				Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(this, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void sendPluginMessageAll(String subchannel, String message){
		sendPluginMessage("ALL", subchannel, message);
	}
	
	public void sendPluginMessage(String server, String subchannel, String message){
		
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Forward");
				out.writeUTF(server);
				out.writeUTF(subchannel);
		
				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				msgout.writeUTF(message);
				msgout.writeShort(message.getBytes().length);
		
				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());
	            Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(this, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void updateScoreBoard(GamePlayer<?> player, String displayName, String... scoreNames){
		
		displayName = Utils.AS(displayName);
		
		for (int i = 0; i < scoreNames.length; i++){
			scoreNames[i] = Utils.AS(scoreNames[i]);
		}
		
		if (!previousBoards.containsKey(player.getName())){
			previousBoards.put(player.getName(), new ArrayList<String>());
		}
			
		List<Boolean> diff = new ArrayList<Boolean>();
			
		for (int i = 0; i < scoreNames.length; i++){
				
			scoreNames[i] = scoreNames[i].length() > 16 ? scoreNames[i].substring(0, 15) : scoreNames[i];
				
			if (previousBoards.get(player.getName()).size() > i){
				diff.add(!scoreNames[i].equals(previousBoards.get(player.getName()).get(i)));
			}
		}
			
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective first = player.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		List<String> old = new ArrayList<String>(previousBoards.get(player.getName()));
		List<String> newOld = new ArrayList<String>();
			
		if (first == null || old.size() == 0){
				
			first = board.registerNewObjective("clans", "dummy");
			first.setDisplaySlot(DisplaySlot.SIDEBAR);
			old = new ArrayList<String>();
				
			for (int x = 0; x < scoreNames.length; x++){
				Score s = first.getScore(Utils.AS(scoreNames[x]));
				s.setScore(scoreNames.length - (x+1));
				old.add(scoreNames[x]);
			}
				
			getPreviousBoards().put(player.getName(), old);
			player.getPlayer().setScoreboard(board);
				
		} else {
			
			for (int x = 0; x < scoreNames.length; x++){
				if (diff.size() > x && diff.get(x)){
					player.getPlayer().getScoreboard().resetScores(Utils.AS(old.get(x)));
					Score s = first.getScore(Utils.AS(scoreNames[x]));
					s.setScore(scoreNames.length - (x+1));
				}
				newOld.add(scoreNames[x]);
			}
				
			getPreviousBoards().put(player.getName(), newOld);
		}
			
		first.setDisplayName(Utils.AS(displayName));
	}
	
	@SneakyThrows
	private void assignSocket(){
		
		sendToSocket(getServerSockets().get("GameServer"), "assign_socket", getServerName());
		BufferedReader in = new BufferedReader(new InputStreamReader(getServerSockets().get("GameServer").getInputStream()));
		new Thread(new EmpyrealSocketListener(this, in)).start();
	}
}