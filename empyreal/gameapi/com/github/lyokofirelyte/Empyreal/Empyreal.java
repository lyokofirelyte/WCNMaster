package com.github.lyokofirelyte.Empyreal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.github.lyokofirelyte.Empyreal.Command.CommandRegistry;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityAlliance;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityRegion;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityRing;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinitySystem;
import com.github.lyokofirelyte.Empyreal.Events.CountdownEndEvent;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Empyreal.JSON.JSONManager;
import com.github.lyokofirelyte.Empyreal.JSON.JSONManager.JSONClickType;
import com.github.lyokofirelyte.Empyreal.Listener.BungeeListener;
import com.github.lyokofirelyte.Empyreal.Listener.EmpyrealSocketListener;
import com.github.lyokofirelyte.Empyreal.Listener.Handler;
import com.github.lyokofirelyte.Empyreal.Listener.SQLQueue;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.ConsolePlayer;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;
import com.google.common.collect.ImmutableMap;
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
	private Map<Object, Object> tempVars = new HashMap<Object, Object>();
	
	@Getter @Setter
	private Map<String, Integer> activeTasks = new HashMap<String, Integer>();
	
	@Getter @Setter
	private CommandRegistry commandRegistry;
	
	static @Getter @Setter
	private String serverName = "";
	
	@Getter @Setter
	private int onlinePlayercount = 0;
	
	@Getter @Setter
	private boolean reconnectInProgress = false;
	
	@Getter @Setter
	private Map<String, DivinityStorageModule> onlineModules = new HashMap<String, DivinityStorageModule>();
	
	@Getter @Setter
	private Map<String, DivinityPlayer> lastCheckedUser = new HashMap<String, DivinityPlayer>();

	@Override
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
				if (!getServerName().equals("GameServer") && !getServerName().equals("") && !getServerName().equals("Creative") && !getServerName().equals("wa")){
					if (Bukkit.getOnlinePlayers().size() <= 0){
						Bukkit.getServer().shutdown();
					}
				}
			}
		});
		
		if (getServerName().equals("GameServer")){
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, getInstance(SQLQueue.class).getType(), 0L, 20L);
		} else {
			APIScheduler.REPEAT.start(this, "gameserver_keep_alive", 200L, 100L, new Runnable(){
				public void run(){
					if (reconnectInProgress){
						System.out.println("&c&oLost connection to GameServer & Chat Module, attempting to reconnect...");
						registerSockets();
					}
				}
			});
		}
	}
	
	private void registerSockets(){
		
		try {
		
			System.out.println("Auto-registering sockets for " + getServerName() + "...");
			
			if (!serverName.equals("GameServer")){
				serverSockets.put("GameServer", new Socket("127.0.0.1", 24000));
				assignSocket();
			}
			
			System.out.println("Sockets registered!");
			reconnectInProgress = false;
			
		} catch (Exception e){
			System.out.println("Failed to register sockets. Retrying in 5 seconds...");
			APIScheduler.DELAY.start(this, "init", 100L, new Runnable(){
				public void run(){
					registerSockets();
				}
			});
		}
	}

	@Override @SneakyThrows
	public void onDisable(){
		
		List<GameModule> toUnregister = new ArrayList<GameModule>();
		
		for (GameModule module : gameModules){
			toUnregister.add(module);
		}
		
		for (GameModule m : toUnregister){
			unregisterModule(m);
		}
		
		if (!serverName.equals("GameServer")){
			sendToSocket("GameServer", Handler.SERVER_SHUTDOWN);
			sendToSocket("GameServer", Handler.REMOVE_SOCKET);
		}
	}
	
	public boolean perms(CommandSender p, String perm){
		return perms(p, perm, true);
	}
	
	public boolean perms(CommandSender p, String perm, boolean silent){
		boolean result = p.isOp() || p instanceof Player == false || (getOnlineModules().containsKey(((Player) p).getUniqueId().toString()) && getDivPlayer((Player) p).getList(DPI.PERMS).contains(perm)) || (getPlayers().containsKey(((Player) p).getUniqueId()) && getGamePlayer(((Player) p).getUniqueId()).getPerms().contains(perm));
		if (!silent){
			Utils.s(p, "&c&oNo permissions!");
		}
		return result;
	}
	
	public void event(Event e){
		Bukkit.getPluginManager().callEvent(e);
	}
	
	public void cancelTask(String name){
		if (activeTasks.containsKey(name)){
			Bukkit.getScheduler().cancelTask(activeTasks.get(name));
		}
	}
	
	public void schedule(Object clazz, String method, long delay, String taskName, Object... args){
		
		for (Method m : clazz.getClass().getMethods()){
			if (m.getName().equals(method)){
				activeTasks.put(taskName, Bukkit.getScheduler().scheduleSyncDelayedTask(this, args != null ? new DivinityScheduler(clazz, m, args) : new DivinityScheduler(clazz, m), delay));
				return;
			}
		}
	}
	
	public void repeat(Object clazz, String method, long delay, long period, String taskName, Object... args){
		
		for (Method m : clazz.getClass().getMethods()){
			if (m.getName().equals(method)){
				activeTasks.put(taskName, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, args != null ? new DivinityScheduler(clazz, m, args) : new DivinityScheduler(clazz, m), delay, period));
				return;
			}
		}
	}
	
	public static void saveJSON(String completePath, JSONMap map){
		
		JSONObject obj = new JSONObject();
		
		for (Object key : map.keySet()){
			obj.put(key, map.get(key));
		}
		
		saveJSON(completePath, obj);
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
	
	public String getTimeLeft(int secondsLeft){
		String time = Math.round(secondsLeft/60) + ":" + (secondsLeft % 60);
		return time.split(":")[1].length() == 1 ? time.replace(":", ":0") : time;
	}
	
	public void deployServer(String scriptName){
		
		String ext = System.getProperty("os.name").contains("Windows") ? ".bat" : ".sh";
		
		try {
			Runtime.getRuntime().exec("../" + scriptName + ext);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public <T> AutoRegister<T> getInstance(Class<T> clazz){
		return (AutoRegister<T>) clazzez.get(clazz.toString());
	}
	
	public <T> GamePlayer<T> getGamePlayer(UUID uuid){
		return (GamePlayer<T>) players.get(uuid);
	}
	
	public <T> GamePlayer<T> getGamePlayer(UUID uuid, Class<T> clazz){
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

	public void sendToSocket(String server, Handler handler, String... msgs){
		String[] strs = new String[msgs.length+1];
		strs[0] = handler.toString();
		for (int i = 0; i < msgs.length; i++){
			strs[i+1] = msgs[i];
		}
		sendToSocket(getServerSockets().get(server), strs);
	}
	
	private void sendToSocket(Socket socket, String... msgs){
		
		try {
		
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.println(getServerName());
			
			for (String msg : msgs){
				pw.println(msg);
			}
			
		} catch (Exception e){
			for (String name : getServerSockets().keySet()){
				if (getServerSockets().get(name).getPort() == (socket.getPort())){
					System.out.println("Could not send socket data from " + getServerName() + " to " + name + "!");
					break;
				}
			}
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
	
	public void requestPlayerCount(){
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("PlayerCount");
				out.writeUTF("ALL");
				Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(this, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
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
				GamePlayer<?> gp = getPlayers().get(Bukkit.getPlayer(player).getUniqueId());
				gp.getPerms().add("server.transfer." + server);
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
	
	public void startCountdown(final int initialSeconds, final int secondsReducedPerSecond, final String displayNameBeforeCounter, final String countdownName){
		
		tempVars.put(countdownName, initialSeconds);
		
		APIScheduler.REPEAT.start(this, countdownName, 0L, new Runnable(){
			public void run(){
				
				int timeLeft = (int) tempVars.get(countdownName);
				
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.getScoreboard() != null){
						p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(Utils.AS(displayNameBeforeCounter + " " + getTimeLeft(timeLeft)));
					}
				}
				
				if (timeLeft <= 0){
					APIScheduler.REPEAT.stop(countdownName);
					tempVars.remove(countdownName);
					Bukkit.getPluginManager().callEvent(new CountdownEndEvent(countdownName));
				} else {
					timeLeft -= secondsReducedPerSecond;
					tempVars.put(countdownName, timeLeft);
				}
			}
		});
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
	
	private void assignSocket(){
		try {
			sendToSocket(getServerSockets().get("GameServer"), "assign_socket", getServerName());
			BufferedReader in = new BufferedReader(new InputStreamReader(getServerSockets().get("GameServer").getInputStream()));
			new Thread(new EmpyrealSocketListener(this, in, new PrintWriter(getServerSockets().get("GameServer").getOutputStream(), true))).start();
		} catch (Exception e){}
	}
	
	public DivinityPlayer getDivPlayer(String name){
		return searchForPlayer(name);
	}
	
	public DivinityPlayer getDivPlayer(Player p){
		return getDivPlayer(p.getUniqueId());
	}
	
	@SneakyThrows
	public DivinityPlayer getDivPlayer(UUID uuid){
		
		if (getOnlineModules().containsKey(uuid.toString())){
			if (!getPlayers().containsKey(uuid)){
				getPlayers().put(uuid, (DivinityPlayer) getOnlineModules().get(uuid.toString()));
			}
			return (DivinityPlayer) getOnlineModules().get(uuid.toString());
		}
		
		DivinityPlayer newPlayer = new DivinityPlayer(uuid, this);
		
		if (getInstance(EmpyrealSQL.class).getType().getResult("users", "uuid", "uuid='" + uuid.toString() + "';").next()){
			newPlayer.fill(getInstance(EmpyrealSQL.class).getType().getMapFromDatabase("users", uuid.toString()));
		} else {
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.getResource("newUser.yml"));
			for (String key : yaml.getKeys(false)){
				newPlayer.set(key, yaml.get(key));
			}
			Utils.bc("Welcome " + Bukkit.getPlayer(uuid).getName() + " to WA!");
			Bukkit.getPlayer(uuid).setDisplayName("&7" + Bukkit.getPlayer(uuid).getName());
		}
		
		getOnlineModules().put(uuid.toString(), newPlayer);
		
		if (!getPlayers().containsKey(uuid)){
			getPlayers().put(uuid, (DivinityPlayer) getOnlineModules().get(uuid.toString()));
		}
		
		return newPlayer;
	}
	
	public DivinityAlliance getDivAlliance(String name){
		
		if (getOnlineModules().containsKey("ALLIANCE_" + name.toLowerCase())){
			return (DivinityAlliance) getOnlineModules().get("ALLIANCE_" + name.toLowerCase());
		}
		
		return null;
	}
	
	public DivinityRegion getDivRegion(String name){
		
		if (getOnlineModules().containsKey("REGION_" + name)){
			return (DivinityRegion) getOnlineModules().get("REGION_" + name);
		}
		
		DivinityRegion rg = new DivinityRegion(name, this);
		getOnlineModules().put("REGION_" + name, rg);
		return rg;
	}
	
	public DivinityRing getDivRing(String name){
		
		if (getOnlineModules().containsKey("RING_" + name)){
			return (DivinityRing) getOnlineModules().get("RING_" + name);
		}
		
		DivinityRing ring = new DivinityRing(name, this);
		getOnlineModules().put("RING_" + name, ring);
		return ring;
	}
	
	public DivinitySystem getDivSystem(){
		
		if (getOnlineModules().containsKey("SYSTEM")){
			return (DivinitySystem) getOnlineModules().get("SYSTEM");
		}
		
		DivinitySystem sys = new DivinitySystem(this, "system");
		
		if (getInstance(EmpyrealSQL.class).getType().hasTable("system")){
			sys.fill(getInstance(EmpyrealSQL.class).getType().getMapFromDatabase("system", "system"));
		}
		
		getOnlineModules().put("SYSTEM", sys);
		
		return sys;
	}
	
	public Player getPlayer(String name){
		return Bukkit.getPlayer(searchForPlayer(name).getUuid());
	}
	
	public DivinityPlayer searchForPlayer(String name){
		
		DivinityPlayer ret = null;
		
		if (getLastCheckedUser().containsKey(name) || doesPartialPlayerExist(name)){
			ret = getLastCheckedUser().get(name);
			getLastCheckedUser().remove(name);
		}
		
		return ret;
	}
	
	public boolean doesPartialPlayerExist(String name){
		
		for (DivinityStorageModule m : getOnlineModules().values()){
			if (m.getTable().equals("users")){
				if (m.getName().toLowerCase().contains(name.toLowerCase())){
					getLastCheckedUser().put(name, (DivinityPlayer) m);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean doesRegionExist(String name){
		
		for (DivinityStorageModule m : getOnlineModules().values()){
			if (m.getTable().equals("regions") && m.getName().equals(name)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean doesRingExist(String name){
			
		for (DivinityStorageModule m : getOnlineModules().values()){
			if (m.getTable().equals("rings") && m.getName().equals(name)){
				return true;
			}
		}
			
		return false;
	}
	
	@SneakyThrows
	public void loadAllDivinityPlayers(){
		
		ResultSet rs = getInstance(EmpyrealSQL.class).getType().getResult("users", "uuid", "ALL");
		int i = 0;
		
		while (rs.next()){
			UUID uuid = UUID.fromString("" + rs.getObject("uuid"));
			DivinityPlayer dp = new DivinityPlayer(uuid, this);
			dp.fill(getInstance(EmpyrealSQL.class).getType().getMapFromDatabase("users", uuid.toString()));
			getOnlineModules().put(uuid.toString(), dp);
			i++;
		}
		
		System.out.println("Loaded " + i + " users!");
	}
	
	@SneakyThrows
	public void loadAllDivinityModules(String table){
		
		if (table.equals("users")){
			loadAllDivinityPlayers();
		} else {
			ResultSet rs = getInstance(EmpyrealSQL.class).getType().getResult(table, "name", "ALL");
			int i = 0;
			
			while (rs.next()){
				String name = table.equals("alliances") ? rs.getString("name").toLowerCase() : rs.getString("name");
				DivinityStorageModule dp = null;
				switch (table){
					case "alliances": dp = new DivinityAlliance(name, this); break;
					case "rings": dp = new DivinityRing(name, this); break;
					case "regions": dp = new DivinityRegion(name, this); break;
					case "system": dp = new DivinitySystem(this, name); break;
				}
				dp.fill(getInstance(EmpyrealSQL.class).getType().getMapFromDatabase(table, name));
				getOnlineModules().put(table.substring(0, table.length()-1).toUpperCase() + "_" + name, dp);
				i++;
			}
			
			System.out.println("Loaded " + i + " " + table + "s!");
		}
	}
	
	public void loadAllDivinityModules(){
		for (String table : new String[]{ "users", "alliances", "rings", "regions", "system" }){
			loadAllDivinityModules(table);
		}
	}
	
	public boolean isOnline(Player p){
		return p.isOnline();
	}
	
	public boolean isOnline(String p){
		return Bukkit.getPlayer(p) != null;
	}
	
	public JSONChatMessage createJSON(String message, ImmutableMap<String, ImmutableMap<JSONClickType, String[]>> jsonValue){
		return getInstance(JSONManager.class).getType().create(message, jsonValue);
	}
	
	public String AS(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}