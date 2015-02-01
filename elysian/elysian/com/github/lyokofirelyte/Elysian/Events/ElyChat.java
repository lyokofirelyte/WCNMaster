package com.github.lyokofirelyte.Elysian.Events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityPluginMessageEvent;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.Manager.JSONManager.JSONClickType;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyChannel;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityStorage;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;
import com.google.common.collect.ImmutableMap;

public class ElyChat implements Listener, AutoRegister {
	
	private Elysian main;
	
	public ElyChat(Elysian i){
		main = i;
		fillMap();
	}
	
	private Map<String, String[]> qc = new THashMap<String, String[]>();
	
	private void fillMap(){
		qc.put("duels", s("I have won % duels!", DPI.DUEL_WINS.s()));
		qc.put("coords", s("My current coords are: %", DPI.GV1.s()));
		qc.put("exp", s("I currently have % exp.", DPI.EXP.s()));
		qc.put("money", s("I currently have % shinies.", DPI.BALANCE.s()));
		qc.put("mobs", s("A", "B"));
	}
	
	private String[] s(String s1, String s2){
		return new String[]{s1, s2};
	}
	
	@DivCommand(name = "PM", aliases = {"tell", "pm", "msg", "message", "t", "r"}, desc = "Private Message Command", help = "/tell <player> <message>", min = 1, player = false)
	public void onPrivateMessage(CommandSender cs, String[] args, String cmd){

		DivinityStorage dp = cs instanceof Player ? main.divinity.api.divManager.getStorage(DivinityManager.dir, ((Player)cs).getUniqueId().toString()) : main.divinity.api.divManager.getStorage(DivinityManager.sysDir, "system");
		String sendTo = !cmd.equals("r") ? args[0] : dp.getStr(DPI.PREVIOUS_PM);
		String message = !cmd.equals("r") ? args[1] : args[0];
		int start = !cmd.equals("r") ? 2 : 1;
		
		for (int i = start; i < args.length; i++){
			message = message + " " + args[i];
		}
		
		if (main.api.doesPartialPlayerExist(sendTo) || sendTo.equals("console")){
			if (sendTo.toLowerCase().equals("console") || main.api.isOnline(sendTo)){

				if (!sendTo.equals("console")){	
					
					String sendToMessage = main.AS("&3<- " + dp.getStr(DPI.DISPLAY_NAME) + "&f: " + main.api.getDivPlayer(sendTo).getStr(DPI.PM_COLOR) + message);
					String sendMeMessage = main.AS(("&3-> " + main.api.getDivPlayer(sendTo).getStr(DPI.DISPLAY_NAME)) + "&f: " + dp.getStr(DPI.PM_COLOR) + message);
					
					main.divinity.api.createJSON("", ImmutableMap.of(		
						sendToMessage, ImmutableMap.of(
							JSONClickType.CLICK_SUGGEST, new String[]{
								"/tell " + cs.getName() + " ",
								"&7&oClick to message this person back!"
							}
						)
					)).sendToPlayer(main.api.getPlayer(sendTo));
					
					if(cs instanceof Player){
						main.divinity.api.createJSON("", ImmutableMap.of(		
								sendMeMessage, ImmutableMap.of(
									JSONClickType.CLICK_SUGGEST, new String[]{
										"/tell " + main.api.getPlayer(sendTo).getName() + " ",
										"&7&oClick to message this person back!"
									}
								)
							)).sendToPlayer((Player)cs);
						
					}else{
						cs.sendMessage(sendMeMessage);
					}

					
					main.api.getDivPlayer(sendTo).set(DPI.PREVIOUS_PM, dp.name());
					
				} else {
					Bukkit.getConsoleSender().sendMessage(main.AS(("&3<- " + dp.getStr(DPI.DISPLAY_NAME) + "&f: " + message)));
					cs.sendMessage(main.AS(("&3-> " + "&6Console" + "&f: " + dp.getStr(DPI.PM_COLOR) + message)));
					main.api.getDivSystem().set(DPI.PREVIOUS_PM, dp.name());
					dp.set(DPI.PREVIOUS_PM, "console");
				}
				
				dp.set(DPI.PREVIOUS_PM, sendTo);
				
			} else {
				main.s(cs, "&c&oThat player is not online.");
			}
			
		} else {
			main.s(cs, "playerNotFound");
		}
	}
	
	@DivCommand(perm = "wa.staff.mod2", aliases = {"filter"}, desc = "Chat & Command Filter Command", help = "/filter <word> <replacement>. If it already has a filter it will be removed.", player = false, min = 2)
	public void onFilter(CommandSender cs, String[] args){
		
		DivinitySystem system = main.api.getDivSystem();
		List<String> toRemove = new ArrayList<String>();
		String dispName = cs instanceof Player ? ((Player)cs).getDisplayName() : "&6Console";
		
		for (String filter : system.getList(DPI.FILTER)){
			if (filter.split(" % ")[0].equalsIgnoreCase(args[0])){
				toRemove.add(filter);
			}
		}
		
		if (toRemove.size() <= 0){
			system.getList(DPI.FILTER).add(args[0].toLowerCase() + " % " + args[1].toLowerCase());
			main.s(cs, "Added &6" + args[0].toLowerCase() + " &4-> &6" + args[1].toLowerCase());
			ElyChannel.STAFF.send("&6System", dispName + " filtered &6" + args[0].toLowerCase() + " &4-> &6" + args[1].toLowerCase() + "&c!", main.api);
		} else {
			for (String s : toRemove){
				system.getList(DPI.FILTER).remove(s);
				main.s(cs, "Removed &6" + s.split(" % ")[0] + " &4-> &6" + s.split(" % ")[1]);
				ElyChannel.STAFF.send("&6System", dispName + " un-filtered &6" + s.split(" % ")[0] + " &4-> &6" + s.split(" % ")[1] + "&c!", main.api);
			}
		}
	}
	
	@DivCommand(aliases = {"qc", "quickchat"}, desc = "QuickChat Command", help = "/qc <option>, /qc list", player = true)
	public void onQC(Player p, String[] args){
		
		String msg = "";
		Location l = p.getLocation();
		
		if (args.length == 0){
			main.s(p, main.help("qc", this));
		} else if (qc.containsKey(args[0].toLowerCase())){
			
			if (args[0].equalsIgnoreCase("coords")){
				msg = qc.get("coords")[0].replace("%", "&6" + l.getBlockX() + "&7, &6" + l.getBlockY() + "&7, &6" + l.getBlockZ() + "&3.");
			} else if (args[0].equalsIgnoreCase("mobs")){
				
				List<String> things = main.api.getDivPlayer(p).getList(DPI.MOB_COUNTS);
				String printOut = "&3&oI've killed ";
				
				for (String thing : things){
					String[] thingSplit = thing.split(" ");
					printOut = printOut.equals("&3&oI've killed ") ? printOut + thingSplit[0] + " x" + thingSplit[1] : printOut + "&6, &3&o" + thingSplit[0] + " x" + thingSplit[1];
				}
				
				msg = printOut;
				
			} else {
				msg = qc.get(args[0].toLowerCase())[0].replace("%", main.api.getDivPlayer(p).getStr(DPI.valueOf(qc.get(args[0].toLowerCase())[1])));
			}
			
			Bukkit.broadcastMessage(main.AS("&6QC &7\u2744 " + p.getDisplayName() + "&f: &3&o" + msg.replace("none", "0")));
			
		} else {
			for (String s : qc.keySet()){
				main.s(p, s);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(final AsyncPlayerChatEvent e){
		
		if (e.isCancelled()){
			return;
		}
		
		e.setCancelled(true);
		main.afkCheck(e.getPlayer());
		
		final DivinityPlayer p = main.api.getDivPlayer(e.getPlayer());
		
		if (!main.api.perms(e.getPlayer(), "wa.rank.settler", true)){
			e.setMessage(ChatColor.stripColor(main.AS(e.getMessage())));
		}
		
		List<String> list = new ArrayList<String>(p.getList(DPI.BAN_QUEUE));
		List<String> list2 = new ArrayList<String>(p.getList(DPI.BAN_QUEUE));
		
		if (e.getMessage().startsWith("@") && !p.getBool(DPI.MUTED)){
			String name = p.getStr(DPI.DISPLAY_NAME);
			String emote = filter(e.getMessage().split(" ")[0].replace("@", ""));
			List<String> emotelist = main.api.getDivSystem().getList(DPI.EMOTE_LIST);
			
			if(emote.equalsIgnoreCase("list")){
				
				String all = "";
				
				for(String s : emotelist){
					all = all + s + ", ";
				}
				
				p.s(all);
				
				return;
			}
			
			if(emotelist.contains(emote)){
				List<String> emoteaction = main.api.getDivSystem().getList(DPI.EMOTE_ACTION);
				
				if(e.getMessage().split(" ").length == 2 && emoteaction.get(emotelist.indexOf(emote)).split(" %s%").length == 2){
					String name2 = e.getMessage().split(" ")[1];
					
					if(main.api.doesPartialPlayerExist(name2)){
						
						DivinityPlayer p2 = main.api.getDivPlayer(name2);
						DivinityUtilsModule.bc(name + "&a" + emoteaction.get(emotelist.indexOf(emote)).split(" %s%")[1].replace("%s%", name).replace("%a%", p2.getStr(DPI.DISPLAY_NAME) + "&a"));
				
					}else{	
						DivinityUtilsModule.bc(name + "&a " + emoteaction.get(emotelist.indexOf(emote)).split(" %s%")[1].replace("%s%", name).replace("%a%", "&7" + name2 + "&a")    );				
					}
					
				}else{
					DivinityUtilsModule.bc(name + "&a " + emoteaction.get(emotelist.indexOf(emote)).split(" %s%")[0]);
				}
			}else{
				p.err("Emote not found! type @list for all the emotes.");
			}
			
			return;
		}
		
		if (p.getList(DPI.NOTEPAD_SETTING).size() > 0){
			List<String> notepad = p.getList(DPI.NOTEPAD_SETTING);
			if (notepad.contains("add")){
				notepad.remove("add");
				e.getPlayer().performCommand("notepad #edit " + e.getMessage());
				return;
			}
		}
		
		if (p.getBool(DPI.PATROL_INPUT)){
			e.getPlayer().performCommand("patrol #add " + e.getMessage());
			return;
		}
		
		if (p.getBool(DPI.IS_BANNING)){
			if (p.getList(DPI.BAN_QUEUE).contains("type:ban") || p.getList(DPI.BAN_QUEUE).contains("type:tban")){
				for (String s : list){
					if (s.contains("reason")){
						for (String ss : list2){
							if (ss.contains("proof")){
								if (DivinityUtilsModule.isInteger(e.getMessage())){
									p.getList(DPI.BAN_QUEUE).add("duration:" + e.getMessage());
									e.getPlayer().performCommand("eban a " + "#four_temp");
								} else {
									p.err("The number must be an integer.");
								}
								return;
							}
						}
						p.getList(DPI.BAN_QUEUE).add("proof:" + e.getMessage().replace(" ", "%"));
						e.getPlayer().performCommand("eban a " + "#four_local");
						return;
					}
				}
				p.getList(DPI.BAN_QUEUE).add("reason:" + e.getMessage().replace(" ", "%"));
				e.getPlayer().performCommand("eban a " + "#three");
				return;
			}
		}
		
		if (!main.api.getDivPlayer(e.getPlayer()).getBool(DPI.MUTED)){

			new Thread(new Runnable(){ public void run(){
				
				for (Player p : Bukkit.getOnlinePlayers()){
					
					DivinityPlayer sendTo = main.api.getDivPlayer(p);
					DivinityPlayer sentFrom = main.api.getDivPlayer(e.getPlayer());
					String rawMsg = new String(e.getMessage());
					sendTo.set(DPI.ELY, false);
					sentFrom.set(DPI.ELY, false);
					
					if (sendTo.getBool(DPI.CHAT_FILTER_TOGGLE)){
						rawMsg = (filter(rawMsg));
					}
					
					String rankColor = sentFrom.getStr(DPI.RANK_COLOR);
					String rankName = sentFrom.getStr(DPI.RANK_NAME);
					String rankDesc = sentFrom.getStr(DPI.RANK_DESC);
					String staffDesc = sentFrom.getStr(DPI.STAFF_DESC);
					String staffColor = sentFrom.getStr(DPI.STAFF_COLOR);
					String playerDesc = sentFrom.getStr(DPI.PLAYER_DESC);
					String globalColor = sendTo.getStr(DPI.GLOBAL_COLOR);
					
					// CHRISTMAS STUFF
					/*
					String clr = Arrays.asList("&c", "&2", "&4", "&a").get(new Random().nextInt(4));
					
					String rankColor = new Random().nextInt(2) == 0 ? "&6" : "&e";
					String rankName = ChatColor.stripColor(sentFrom.getStr(DPI.RANK_NAME));
					String rankDesc = sentFrom.getStr(DPI.RANK_DESC);
					String staffDesc = sentFrom.getStr(DPI.STAFF_DESC);
					String staffColor = clr;
					String playerDesc = sentFrom.getStr(DPI.PLAYER_DESC);
					*/
					// END CHRISTMAS STUFF
					
					JSONChatMessage msg = new JSONChatMessage("", null, null);
					
					JSONChatExtra extra = new JSONChatExtra(main.AS(staffColor + rankName + " "), null, null);
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(staffDesc));
					msg.addExtra(extra);
					
					extra = new JSONChatExtra(main.AS(rankColor + "❅ "), null, null);
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&6" + rankDesc));
					msg.addExtra(extra);
					
					extra = new JSONChatExtra(main.AS(e.getPlayer().getDisplayName() + "&f:" + globalColor), null, null);
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(playerDesc));
					extra.setClickEvent(JSONChatClickEventType.SUGGEST_COMMAND, "/tell " + e.getPlayer().getName() + " ");
					msg.addExtra(extra);
					
					for (String message : rawMsg.split(" ")){
						if (linkCheck(message)){
							extra = new JSONChatExtra(main.AS(" &6&o" + main.divinity.api.title.getPageTitle(message) + globalColor), null, null);
							extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&oNavigate to URL"));
							extra.setClickEvent(JSONChatClickEventType.OPEN_URL, message);
						} else if (message.startsWith("cmd:")){
							extra = new JSONChatExtra(main.AS(" &6&o" + message.replace("cmd:", "").replace("_", " ") + globalColor), null, null);
							extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&oRun Command /" + message.replace("cmd:", "").replace("_", " ")));
							extra.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/" + message.replace("cmd:", "").replace("_", " "));
						} else {
							extra = new JSONChatExtra(main.AS(" " + globalColor + message), null, null);
							extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&oSearch google for " + message));
							extra.setClickEvent(JSONChatClickEventType.OPEN_URL, "https://www.google.com/search?q=" + message);
						}
						msg.addExtra(extra);
					}
					main.s(p, msg);
					main.api.event(new DivinityPluginMessageEvent(p, "globalChat", new String[]{"&7" + e.getPlayer().getDisplayName() + "&f: &7&o" + e.getMessage()}));
				}
				
				Map<String, Object> map = new THashMap<String, Object>();
				map.put("user", ChatColor.stripColor(main.AS(p.getStr(DPI.DISPLAY_NAME))));
				map.put("message", ChatColor.stripColor(main.AS(e.getMessage())));
				map.put("type", "minecraft_insert");
				/*String msg = (String) main.divinity.api.web.sendPost("/api/chat", map).get("message");
				main.divinity.api.web.messages.add(msg);*/
				Bukkit.getConsoleSender().sendMessage(main.AS(e.getPlayer().getDisplayName() + "&f: " + e.getMessage()));
				
			}}).start();
		} else {
			main.s(e.getPlayer(), "muted");
		}
	}
	
	private String filter(String msg){

		msg = msg.replace("place", "pLace").replace("&k", "");

    	for (String filter : main.api.getDivSystem().getList(DPI.FILTER)){
    		if (ChatColor.stripColor(DivinityUtilsModule.AS(msg.toLowerCase())).contains(filter.split(" % ")[0].toLowerCase())){
    			msg = msg.replace(filter.split(" % ")[0], filter.split(" % ")[1]);
    		}
    	}
    	
		return msg;
	}
	
	private boolean linkCheck(String msg){
	  	if ((msg.contains("http://") || msg.contains("https://")) && !msg.contains("tinyurl") && !msg.contains("bit.ly")){	
	  		return true;
	  	}
	  	return false;
	}
}