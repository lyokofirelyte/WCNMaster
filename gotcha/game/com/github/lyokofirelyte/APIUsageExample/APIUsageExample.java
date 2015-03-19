package com.github.lyokofirelyte.APIUsageExample;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.JSONMap;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Gotcha.GotchaPlayer;

// implement GameModule. I commented out JavaPlugin so it won't run live.
public class APIUsageExample /*extends JavaPlugin*/ implements GameModule {

	@Getter @Setter // Jar name that it will output. Always name + -1.0.jar
	private String jarName = "APIUsageExample-1.0.jar";
	
	@Getter @Setter // Package name - normally the same as the main class
	private String packageName = "APIUsageExample";
	
	@Getter @Setter // reference to API
	private Empyreal api;

	//@Override
	public void onEnable(){
		
		// First we need to grab the API.
		setApi((Empyreal) Bukkit.getPluginManager().getPlugin("Empyreal"));
	
		// Register this plugin to the API. onRegister() will be called automatically.
		getApi().registerModule(this);
		
		// do anything else to set up
	}

	//@Override
	public void onDisable(){
		
		// Release the plugin from the API. Automatically calls the closing() method.
		getApi().unregisterModule(this);
	}

	@Override
	public void onRegister(){
	
		// This will fire after the API registers the plugin. If this stuff happens, everything's OK so far.
		// do more stuff to set up your plugin if needed
		
		// Now, if your plugin is ready to accept people, tell the API to allow people to click on the sign to join.
		// The person who booted up the server will be moved over automatically after this.
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "server_boot_complete");
		
		
		// When the game is stared and you don't want anyone else to be able to join
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "game_in_progress");
		
	}

	@Override
	public void closing(){
	
		// run your save stuff here (or on onDisable, does not really matter.)
	}

	@Override
	public void onPlayerJoin(Player p){
		
		// Create an instance of your custom game player class
		GamePlayer<GotchaPlayer> player = new GotchaPlayer(p);
		// GotchaPlayer is your class that implements GamePlayer.
		
		// Below are examples of accessing the gotcha player from the API.
		
		// If you want the actual GotchaPlayer class later, you can access it via
		GotchaPlayer actualGotchaPlayer = getApi().getGamePlayer(p.getUniqueId(), GotchaPlayer.class).getType();
		
		// If you don't care what kind of GamePlayer it is, you can call it raw via
		GamePlayer<?> gp = getApi().getGamePlayer(p.getUniqueId());
		
		// Or, if you know what kind it is, but want the GamePlayer interface...
		GamePlayer<GotchaPlayer> gp2 = getApi().getGamePlayer(p.getUniqueId());
		
		// You can always getType() and change it into a GotchaPlayer later via
		actualGotchaPlayer = gp2.getType();
		
		
		// note that all of the above examples return the exact same class, just in different ways.
		
		
		// Register the new player with the API
		getApi().registerPlayer(player);
		
		// We don't want OP players online, so we give them staff permissions if they are OP
		// Then we take OP away.
		if (p.isOp()){
			player.getPerms().add("gameserver.staff");
			p.setOp(false);
		}
		// we return OP when they log off in the API, don't worry about that. gameserver.staff is for /o chat.
		// /o chat is in the API - already done for you.
	}
	
	@Override
	public void onPlayerQuit(Player p){
		
		// unregister the player so we don't store it anymore
		getApi().unregisterPlayer(getApi().getGamePlayer(p.getUniqueId()));
	}
	
	@Override
	public void onPlayerChat(GamePlayer<?> gp, String message){
		// generic player chat that looks good
		Utils.bc("&7" + gp.getPlayer().getDisplayName() + "&f: " + message);
	}

	@Override
	public void shutdown(){
		// This is not called when the server shuts down, it's called /to/ shut the server down.
		Bukkit.getServer().shutdown();
	}
	
	public void generalUseExamples(Player p){
		
		// Here are some cool things from the API.
		
		// Schedulers
		APIScheduler.DELAY.start(getApi(), "TASK_NAME", 60L, new Runnable(){
			public void run(){
				
				// This will fire after 3 seconds
			}
		});
		
		APIScheduler.REPEAT.start(getApi(), "TASK_NAME", 60L, 60L, new Runnable(){
			public void run(){
				
				// This will fire after 3 seconds and then repeat every 3 seconds.
				
				
				// This will stop this task.
				APIScheduler.REPEAT.stop("TASK_NAME");
			}
		});
		
		// Quick scoreboard usages
		// This will update their board like in using the items in the string provided, in order.
		
		GamePlayer<GotchaPlayer> gp = getApi().getGamePlayer(p.getUniqueId());
		String[] list = new String[] { p.getName(), "Item 2", "Item 3" };
		getApi().updateScoreBoard(gp, "SCOREBOARD DISPLAY NAME", list);
		
		// Setting actual scores
		p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(p.getName()).setScore(10);
		
		// Get 00:00 format for time from seconds. Example: 60 seconds turns into 1:00
		getApi().getTimeLeft(60);
		
		
		// Send someone to a server
		getApi().sendToServer(p.getName(), "GameServer");
		// or everyone at once
		getApi().sendAllToServer("GameServer");
		
		
		// Send a request to the game server
		getApi().sendToSocket(getApi().getServerSockets().get("GameServer"), "reason", "message");
		// You would then have to go into InnerSignListener from GameServer and add a reason case, and then in.readLine() would be the message.
		
		
		// Get the server name. Will be APIUsageExample-0 through APIUsageExample-16, depending on how many lobbies you have set up
		getApi().getServerName();

	}
	
	// Auto Register examples. Implement AutoRegister<ClassName> and Listener (if it needs a Bukkit Listener)
	public class SomeListener implements AutoRegister<SomeListener>, Listener {
		
		// You need this.
		@Getter @Setter
		private SomeListener type = this;
		
		@Getter @Setter
		private APIUsageExample main;
		
		// This contructor must be present!
		public SomeListener(APIUsageExample i){
			main = i;
		}
		
		@EventHandler
		public void onDeath(PlayerDeathEvent e){
			
			// Event already registered - no setup required!
		}
		
		public void hello(){}
	}
	
	public void test(Player p){
		
		// Examples accessing the class that we just made
		SomeListener listener = getApi().getInstance(SomeListener.class).getType();
		listener.hello();
		
		
		// File loading
		JSONMap<Object, Object> map = Empyreal.loadJSON("./plugins/APIUsageExample/test.json");
		String value = map.getStr("SOME_VALUE");
		int val = map.getInt("SOME_VALUE");
		
		// You can put anything you want into it.
		map.set("SOME_VALUE", new ArrayList<String>());
		
		// File Saving. Remember to mkdirs on the folder if it does not exist yet.
		new File("./plugins/APIUsageExample").mkdirs();
		Empyreal.saveJSON("./plugins/APIUsageExample/test.json", map);
		
		// Lastly, the Utils class has a ton of great stuff (mostly the same as from DivinityUtilsModule)
		
		// Follows the lightning bolt + grey text format like the GameServer and auto AS();
		Utils.s(p, "msg");
		Utils.bc("msg");
		
		// Also
		Utils.AS("msg");
	}
	
	// Lastly, using @Getter, @Setter, @SneakyThrows, and @NonNull
	
	@Getter @Setter
	private String id = "David";
	
	public void examples(){
		
		getId();
		setId("Mark");
		
		// Easy! Now, something that would need a try/catch but we don't want to do anything in the catch
		try {
			ServerSocket ss = new ServerSocket(100);
			ss.accept();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		// It's all upset! HOWEVER, you can..
	}
	
	// Add sneakythrows...
	@SneakyThrows
	public void examples2(){
		
		ServerSocket ss = new ServerSocket(100);
		ss.accept();
		
		// Haha! No try/catch! It will e.printStackTrace() automatically.
	}
	
	// And non null means it can't be null.
	public void notNull(@NonNull String input){
		
		// If input is null, it won't continue and will throw an NPE before it messes up any of the code, so we know where the issue is right away!
	}
	
}