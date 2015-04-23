package com.github.lyokofirelyte.Elysian;

import gnu.trove.map.hash.THashMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.lyokofirelyte.Elysian.Commands.ElyProxy;
import com.github.lyokofirelyte.Elysian.Events.DivinityPluginMessageEvent;
import com.github.lyokofirelyte.Elysian.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Gui.GuiCloset;
import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Database.DAI;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityAlliance;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinitySystem;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.Elysian.ElyTask;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.FireworkShenans;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class Elysian extends JavaPlugin implements GameModule {
	
	public Empyreal api;
	public WorldEditPlugin we;
	public ElySetup setup;
	public Empyreal gameAPI;
	
	@Getter
	private String packageName = "Elysian";
	
	@Getter
	private String jarName = "Elysian-1.0.jar";

	public Map<ElyTask, Integer> tasks = new THashMap<ElyTask, Integer>();
	public Map<Location, List<List<String>>> queue = new THashMap<Location, List<List<String>>>();
	public Map<Integer, GuiCloset> closets = new THashMap<>();
	public List<String> numerals = new ArrayList<String>();
	public List<String> discussion = new ArrayList<String>();
	public Map<Object, String> spellTasks = new THashMap<Object, String>();
	public boolean hasSunDayBeenPerformedBefore = false;
	
	@Getter @Setter
	private DivinitySystem divSystem;
	
	@Override @SneakyThrows
	public void onEnable(){
		
		gameAPI = (Empyreal) Bukkit.getPluginManager().getPlugin("Empyreal");
		api = gameAPI;
		gameAPI.registerModule(this);
		
		EmpyrealSQL sql = api.getInstance(EmpyrealSQL.class).getType();
		sql.write("create table if not exists users (uuid VARCHAR(255), name VARCHAR(255));");
		sql.write("create table if not exists alliances (uuid VARCHAR(255), name VARCHAR(255));");
		sql.write("create table if not exists rings (uuid VARCHAR(255), name VARCHAR(255));");
		sql.write("create table if not exists regions (uuid VARCHAR(255), name VARCHAR(255));");
		sql.write("create table if not exists system (uuid VARCHAR(255), name VARCHAR(255));");
		
		setup = new ElySetup(this);
		setup.start();
		getServer().getMessenger().registerIncomingPluginChannel(api, "BungeeCord", (ElyProxy) api.getInstance(ElyProxy.class));
		getServer().getMessenger().registerOutgoingPluginChannel(api, "BungeeCord");
		
		setDivSystem(api.getDivSystem());
		getDivSystem().set(DPI.REBOOT_INIT, false);
	}
	
	@Override
	public void onDisable(){
		
		long currTime = new Long(System.currentTimeMillis());
		int i = 1;
		
		for (DivinityStorageModule dp : api.getOnlineModules().values()){
			if (dp.getTable().equals("users")){
				dp.set(DPI.DIS_ENTITY, "none");
				dp.set(DPI.IS_DIS, false);
			}
			dp.save();
			System.out.println("SAVE [" + dp.getTable() + "] " + dp.getName() + " (" + i + "/" + api.getOnlineModules().size() + ")");
			i++;
		}
		
		System.out.println("Save Complete @ " + ((System.currentTimeMillis() - currTime)/1000) + " seconds");
		Bukkit.getScheduler().cancelTasks(this);
	}
	
	@Override
	public void onRegister(){
		
	}
	
	@Override
	public void closing(){
		
	}
	
	@Override
	public void onPlayerJoin(Player p){
		
	}
	
	@Override
	public void onPlayerQuit(Player p){
		
	}
	
	@Override
	public void onPlayerChat(GamePlayer<?> gp, String msg){
		
	}
	
	@Override
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}
	
	public void cancelTask(ElyTask task){
		if (tasks.containsKey(task)){
			Bukkit.getScheduler().cancelTask(tasks.get(task));
			tasks.remove(task);
		}
	}
	
	public String AS(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public String coloredAllianceName(String alliance){
		String name = api.getDivAlliance(alliance).getName();
		String p1 = api.getDivAlliance(alliance).getStr(DAI.COLOR_1);
		String p2 = api.getDivAlliance(alliance).getStr(DAI.COLOR_2);
		return p1 + name.substring(0, name.length()/2) + p2 + name.substring(name.length()/2);
	}
	
	public String coloredAllianceName(DivinityAlliance alliance){
		String p1 = alliance.getStr(DAI.COLOR_1);
		String p2 = alliance.getStr(DAI.COLOR_2);
		return p1 + alliance.getName().substring(0, alliance.getName().length()/2) + p2 + alliance.getName().substring(alliance.getName().length()/2);
	}
	
	public String help(String alias, Object o){
		for (Method method : o.getClass().getMethods()) {
			if (method.getAnnotation(GameCommand.class) != null){
				GameCommand anno = method.getAnnotation(GameCommand.class);
				if (anno.aliases()[0].equals(alias)){
					return anno.help();
				}
			}
		}
		return "No help found for this command";
	}
	
	public void s(Player s, JSONChatMessage msg){
		api.event(new DivinityPluginMessageEvent(s, msg));
	}

	public void s(CommandSender s, String type){
		api.event(new DivinityPluginMessageEvent(s, type));
	}

	public void s(CommandSender s, List<String> type){
		for(String str : type){
			api.event(new DivinityPluginMessageEvent(s, str));
		}
	}
	
	public void s(CommandSender s, String type, String message){
		api.event(new DivinityPluginMessageEvent(s, type, new String[]{message}));
	}
	
	public void s(CommandSender s, String type, String[] message){
		api.event(new DivinityPluginMessageEvent(s, type, message));
	}

	public void fw(World w, Location l, Type type, Color color){
		try {
			api.getInstance(FireworkShenans.class).getType().playFirework(w, l, FireworkEffect.builder().with(type).withColor(color).build());
		} catch (Exception e){}
	}
	
	public void afkCheck(Player p){

		DivinitySystem system = api.getDivSystem();
		api.getDivPlayer(p).set(DPI.AFK_TIME_INIT, 0);
		
		if (system.getList(DPI.AFK_PLAYERS).contains(p.getName())){
			system.getList(DPI.AFK_PLAYERS).remove(p.getName());
			DivinityUtilsModule.bc(p.getDisplayName() + " &3&ois no longer away. &6&o(" + (Math.round(((System.currentTimeMillis() - api.getDivPlayer(p).getLong(DPI.AFK_TIME)) / 1000) / 60)+3) + " minutes)");
			api.event(new ScoreboardUpdateEvent(p));
			p.setPlayerListName(AS(p.getDisplayName()));
		}
	}
}