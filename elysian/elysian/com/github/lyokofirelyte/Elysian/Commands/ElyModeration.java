package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class ElyModeration implements AutoRegister {

	 private Elysian main;
	 
	 public ElyModeration(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(perm = "wa.rank.dweller", aliases = {"suicide"}, desc = "Goodbye, World!", help = "/suicide", player = true)
	 public void onSuicide(Player p, String[] args){
		 p.setHealth(0);
	 }
	 
	 @DivCommand(name = "Mute", perm = "wa.staff.mod", aliases = {"mute", "kik", "disable"}, desc = "Mute someone!", help = "/mute <player> <duration in minutes, default = 5> or /kick <player> <reason>", player = false, min = 1)
	 public void onMute(CommandSender cs, String[] args, String cmd){
		 
		 DivinityPlayer who = main.api.doesPartialPlayerExist(args[0]) ? main.api.getDivPlayer(args[0]) : null;
		 Long time = args.length == 2 && DivinityUtilsModule.isInteger(args[1]) ? Integer.parseInt(args[1])*60L*1000L : (5L*60L) * 1000L;
		 String muter = cs instanceof Player ? ((Player)cs).getDisplayName() : "Console";
		 DPI effectType = cmd.equals("mute") ? DPI.MUTED : DPI.DISABLED;
		 DPI effectDelay = cmd.equals("mute") ? DPI.MUTE_TIME: DPI.DISABLE_TIME;
		 String type = effectType.equals(DPI.MUTED) ? "mute" : "disable";
		 
		 if (cmd.equals("kik") && who != null){
			 kick(cs, args, muter, who);
		 } else {
			 
			 if (who != null){
				 
				 if (who.getBool(effectType)){
					 who.set(effectType, false);
					 DivinityUtilsModule.bc(muter + " &4&ohas released the " + type + " from " + who.getStr(DPI.DISPLAY_NAME) + "&4.");
				 } else {
					 who.set(effectDelay, System.currentTimeMillis() + time);
					 who.set(effectType, true);
					 DivinityUtilsModule.bc(muter + " &4&ohas placed a " + type + " on " + who.getStr(DPI.DISPLAY_NAME) + " &4&ofor &6&o" + (time/1000)/60 + " &4&ominutes.");
				 }
				 
			 } else {
				 main.s(cs, "playerNotFound");
			 }
		 }
	 }
	 
	 private void kick(CommandSender cd, String[] args, String kicker, DivinityPlayer who){
		 String message = args.length > 1 ? "" : "no reason";
		 for (String s : args){
			 message = message + " " + s;
		 }
		 DivinityUtilsModule.bc(kicker + " &4&ohas kicked " + who.getStr(DPI.DISPLAY_NAME) + " &e&o(&6&o" + message.replace(args[0], "").trim() + "&e&o)");
		 Bukkit.getPlayer(who.uuid()).kickPlayer(main.AS("&e&o(&6&o" + message.replace(args[0], "").trim() + "&e&o)"));
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"kill"}, desc = "Kill someone!", help = "/kill <player>", player = false, min = 1)
	 public void onKill(CommandSender cs, String[] args){
		 
		 String display = cs instanceof Player ? ((Player)cs).getDisplayName() : "&6Console";
		 
		 if (main.api.doesPartialPlayerExist(args[0]) && main.api.isOnline(args[0])){
			 main.api.getPlayer(args[0]).setHealth(0);
			 DivinityUtilsModule.bc(display + " &4has REKT " + main.api.getDivPlayer(args[0]).getStr(DPI.DISPLAY_NAME));
		 } else {
			 main.s(cs, "playerNotFound");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"eban"}, desc = "Elysian Ban Command", help = "/eban <player>", player = true, min = 1)
	 public void onBan(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 
		 if (args.length == 1){
			 
			 if (main.api.doesPartialPlayerExist(args[0])){
				 dp.s("&7&oYour chat will be paused.");
				 dp.set(DPI.BAN_QUEUE, new ArrayList<String>());
				 dp.set(DPI.PAUSED_CHAT, new ArrayList<String>());
				 dp.set(DPI.IS_BANNING, true);
				 dp.getList(DPI.BAN_QUEUE).add("player:" + args[0]);
				 p.performCommand("eban " + args[0] + " #one");
			 } else {
				 main.s(p, "playerNotFound");
			 }
			 
		 } else if (dp.getBool(DPI.IS_BANNING)){
			 
			 JSONChatMessage msg = new JSONChatMessage("", null, null);
			 String snow = "&3\u2744 ";
		  
			 switch (args[1]){
			 
			 	case "#cancel":
			 		
			 		dp.set(DPI.IS_BANNING, false);
			 		dp.set(DPI.BAN_QUEUE, new ArrayList<String>());
			 		
			 		List<String> missedChat = dp.getList(DPI.PAUSED_CHAT);
			 		String chat = missedChat.size() > 0 ? missedChat.get(0) : "&7&oNo missed chat.";
			 		
			 		if (missedChat.size() > 1){
			 			for (String c : missedChat){
			 				chat = chat + "\n" + c;
			 			}
			 		}
			 		
			 		msg.addExtra(em("&7&oBanning module exited. Hover to view missed chat.", chat, ""));
			 		main.s(p, msg);
			 		
			 	break;
			 
			 	case "#one":
			 		
			 		p.sendMessage("");
			 		dp.s("Elysian Ban Module - Select Action");
			 		msg.addExtra(em("&6GLOBAL " + snow, "&7&oA global ban that effects rep.\nPlease follow the gban rules!", "/eban " + args[0] + " #two_global"));
			 		msg.addExtra(em("&6LOCAL " + snow, "&7&oA local ban that does not effect rep.", "/eban " + args[0] + " #two_local"));
			 		msg.addExtra(em("&6TEMP " + snow, "&7&oA temporary that does not effect rep.", "/eban " + args[0] + " #two_temp"));
			 		msg.addExtra(em("&7&oCANCEL ", "&7&oExit the ban module.", "/eban " + args[0] + " #cancel"));
			 		msg.sendToPlayer(p);
			 		p.sendMessage("");
			 		
			 	break;
			 	
			 	case "#two_global":
			 		
			 		dp.getList(DPI.BAN_QUEUE).add("type:gban");
			 		
			 		p.sendMessage("");
			 		dp.s("Select Reason");
			 		msg.addExtra(em("&6XRAY " + snow, "&7&oModified client for finding ores quickly.\nMust have video proof.", "/eban " + args[0] + " #three_xray"));
			 		msg.addExtra(em("&6FLY/JUMP HACK " + snow, "&7&oModified client.", "/eban " + args[0] + " #three_fly/jump%hack"));
			 		msg.addExtra(em("&6SWASTICA " + snow, "&7&oSwastica placement.", "/eban " + args[0] + " #three_swastica%placement"));
			 		msg.addExtra(em("&6GRIEFING " + snow, "&7&oGriefing.", "/eban " + args[0] + " #three_griefing"));
			 		msg.addExtra(em("&6RACIST " + snow, "&7&oRacist remarks directly at a player or in global.", "/eban " + args[0] + " #three_racist%comments"));
			 		msg.sendToPlayer(p);
			 		
			 		msg = new JSONChatMessage("", null, null);
			 		msg.addExtra(em("&6HOMOPHOBIC " + snow, "&7&oHomophobic remarks at a player or in global.", "/eban " + args[0] + " #three_homophobic%comments"));
			 		msg.addExtra(em("&6SHOCK SITE " + snow, "&7&oPosting shock sites intended for scaring.", "/eban " + args[0] + " #three_shock%website"));
			 		msg.addExtra(em("&6PORN " + snow, "&7&oPosting images not suitable for minors.", "/eban " + args[0] + " #three_pornographic%images"));
			 		msg.addExtra(em("&7&oCANCEL ", "&7&oExit the ban module.", "/eban " + args[0] + " #cancel"));
			 		msg.sendToPlayer(p);
			 		p.sendMessage("");
			 		
			 	break;
			 	
			 	case "#two_local": case "#two_temp":
			 		
			 		String ban = args[1].contains("local") ? "local" : "temp";
			 		dp.getList(DPI.BAN_QUEUE).add("type:" + (ban.equals("local") ? "ban" : "tban"));
			 		
			 		p.sendMessage("");
			 		dp.s("This is a " + ban + " ban. Please type a reason in chat.");
			 		p.sendMessage("");
			 		
			 	break;
			 	
			 	case "#three":
			 		
			 		p.sendMessage("");
			 		dp.s("Please type the proof type in chat. Type 'none' if you don't have any.");
			 		p.sendMessage("");
			 		
			 	break;
			 	
			 	case "#four_local":
			 		
			 		if (dp.getList(DPI.BAN_QUEUE).contains("type:tban")){
			 			p.sendMessage("");
				 		dp.s("Please type the number of hours for this ban to last.");
				 		p.sendMessage("");
			 		} else {
				 		onBan(p, new String[]{args[0], "#six"});
			 		}

			 	break;
			 	
			 	case "#four_temp":
			 		
			 		onBan(p, new String[]{args[0], "#six"});
			 		
			 	break;
			 	
			 	case "#three_fly/jump%hack": case "#three_swastica%placement": case "#three_griefing": case "#three_racist%comments": case "#three_homophobic%comments":
			 	case "#three_shock%website": case "#three_pornographic%images": case "#three_xray":
			 		
			 		dp.getList(DPI.BAN_QUEUE).add("reason:" + args[1].split("\\_")[1]);
			 		onBan(p, new String[]{args[0], "#four"});
			 		
			 	break;
			 	
			 	case "#four":
			 		
			 		p.sendMessage("");
			 		dp.s("Select Proof");
			 		msg.addExtra(em("&6VIDEO " + snow, "&7&oVideo proof.", "/eban " + args[0] + " #five_video"));
			 		msg.addExtra(em("&6LOG " + snow, "&7&oLog Proof proof.", "/eban " + args[0] + " #five_log"));
			 		msg.addExtra(em("&6VIDEO + LOG " + snow, "&7&oVideo & Log Proof proof.", "/eban " + args[0] + " #five_video%and%log"));
			 		msg.addExtra(em("&7&oCANCEL ", "&7&oExit the ban module.", "/eban " + args[0] + " #cancel"));
			 		msg.sendToPlayer(p);
			 		p.sendMessage("");
			 		
			 	break;
			 	
			 	case "#five_video": case "#five_log": case "#five_video%and%log":
			 		
			 		dp.getList(DPI.BAN_QUEUE).add("proof:" + args[1].split("\\_")[1] + "%proof");
			 		onBan(p, new String[]{args[0], "#six"});
			 		
			 	break;
			 	
			 	case "#six":
			 		
			 		List<String> banQ = dp.getList(DPI.BAN_QUEUE);
			 		String type = "";
			 		String reason = "";
			 		String proof = "";
			 		String duration = "forever";
			 		DivinityPlayer banned = null;
			 		
			 		for (String t : banQ){
			 			switch (t.split(":")[0]){
			 				case "player": banned = main.api.getDivPlayer(t.split(":")[1]); break;
			 				case "proof": proof = t.split(":")[1]; break;
			 				case "reason": reason = t.split(":")[1]; break;
			 				case "type": type = t.split(":")[1]; break;
			 				case "duration": duration = t.split(":")[1]; break;
			 			}
			 		}
			 		
			 		p.sendMessage("");
			 		dp.s("Banning &6" + banned.getStr(DPI.DISPLAY_NAME));
			 		dp.s("Reason&f: &6" + reason.replace("%", " "));
			 		dp.s("Type&f: &6" + type);
			 		dp.s("Proof&f: &6" + proof.replace("%", " "));
			 		dp.s("Duration&f: &6" + duration + " hours");
			 		p.sendMessage("");
			 		
			 		if (duration.equals("forever")){
			 			msg.addExtra(em("&aCONFIRM BAN " + snow, "&c&oCONFIRM BAN!", "/eban " + args[0] + " #confirm " + banned.name() + " " + type + " " + reason + " " + proof));
			 		} else {
			 			msg.addExtra(em("&aCONFIRM BAN" + snow, "&c&oCONFIRM BAN!", "/eban " + args[0] + " #confirm " + banned.name() + " " + type + " " + reason + " " + proof + " " + duration));
			 		}
			 		
			 		msg.addExtra(em("&7&oCANCEL ", "&7&oExit the ban module.", "/eban " + args[0] + " #cancel"));
			 		msg.sendToPlayer(p);
			 		p.sendMessage("");
			 		
			 	break;
			 	
			 	case "#confirm":
			 		
			 		DivinityPlayer bdp = main.api.getDivPlayer(UUID.fromString(args[2]));
			 		DivinityUtilsModule.bc("&c&oFinal Score For " + bdp.getStr(DPI.DISPLAY_NAME));
			 		
			 		String[] messages = new String[]{
			 			"Shinies: &6" + bdp.getInt(DPI.BALANCE),
			 			"Rank: &6" + bdp.getStr(DPI.RANK_NAME),
			 			"Alliance: &6" + bdp.getStr(DPI.ALLIANCE_NAME),
			 			"Owned Storage Units: &6" + bdp.getList(DPI.OWNED_CHESTS).size()
			 		};
			 		
			 		for (String m : messages){
			 			Bukkit.broadcastMessage(main.AS(snow + "&b" + m));
			 		}
			 		
			 		for (Player player : Bukkit.getOnlinePlayers()){
			 			((ElyEffects) main.api.getInstance(ElyEffects.class)).playCircleFw(player, DivinityUtilsModule.getRandomColor(), Type.BALL_LARGE, 3, 1, 1, true, false);
			 		}
			 		
			 		bdp.set(DPI.OWNED_CHESTS, new ArrayList<String>());
			 		
			 		boolean stopOp = false;
			 		
			 		if (!p.isOp()){
			 			stopOp = true;
			 			p.setOp(true);
			 		}
			 		
			 		if (!dp.getList(DPI.BAN_QUEUE).contains("type:tban")){
			 			p.performCommand(args[3] + " " + args[2] + " " + args[4].replace("%", " ") + (args[5].equals("none") ? "" : " - " + args[5].replace("%", " ")));
			 		} else {
			 			p.performCommand(args[3] + " " + args[2] + " " + args[6] + " h " + args[4].replace("%", " ") + (args[5].equals("none") ? "" : " - " + args[5].replace("%", " ")));
			 		}
			 		
			 		if (stopOp){
			 			p.setOp(false);
			 		}
			 		
			 		onBan(p, new String[]{args[0], "#cancel"});
			 		
			 	break;
			 }
		 } else {
			 dp.err("&c&oYou must type /eban <player>.");
		 }
	 }
	 
	 private JSONChatExtra em(String text, String hover, String click){
		 JSONChatExtra extra = new JSONChatExtra(main.AS(text), null, null);
		 extra.setClickEvent(JSONChatClickEventType.RUN_COMMAND, click);
		 extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(hover));
		 return extra;
	 }
}