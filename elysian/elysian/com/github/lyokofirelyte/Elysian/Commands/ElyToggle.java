package com.github.lyokofirelyte.Elysian.Commands;

import lombok.Getter;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElyToggle implements AutoRegister<ElyToggle> {

	 Elysian main;
	 
	 @Getter
	 private ElyToggle type = this;
	 
	 public ElyToggle(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(aliases = {"toggle", "tog"}, desc = "Toggle various settings", help = "/toggle help", player = true)
	 public void onToggle(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 
		 if (args.length == 0){
			 help(p, dp);
		 } else {
			 switch (args[0]){
			 
			 	case "help": case "list":
			 		
			 		help(p, dp);
			 		
			 	break;
			 	
			 	default:
			 		
			 		try {
			 			
			 			if (args.length == 1){
			 				dp.set(DPI.valueOf(args[0].toUpperCase() + "_TOGGLE"), !dp.getBool(DPI.valueOf(args[0].toUpperCase() + "_TOGGLE")));
			 			} else {
			 				dp.set(DPI.valueOf(args[0].toUpperCase()), DivinityUtilsModule.createString(args, 1));
			 			}
			 			
			 			main.s(p, "none", "&o" + args[0] + " updated");
			 			
			 		} catch (Exception e){
			 			help(p, dp);
			 		}
			 		
			 	break;
			 }
		 }
	 }
	 
	 private void help(Player p, DivinityPlayer dp){
		 
		 main.s(p, "none", "Click the [toggle] to toggle.");
		 
		 String[] messages = new String[]{
			cc(dp, "alliance") + "alliance (join or leave chat)",
			cc(dp, "scoreboard") + "scoreboard (visibility of scoreboard)",
			cc(dp, "pokes") + "pokes (visible poke messages)",
			cc(dp, "fireworks") + "fireworks (on various events)",
			cc(dp, "deathLocs") + "deathLocs (show location of your death)",
			cc(dp, "particles") + "particles (teleport particles)",
			cc(dp, "xp_disp_name") + "xp_disp_name (tool name change on XP gain)",
			cc(dp, "chat_filter") + "chat_filter (toggle swear word filtering)",
			"&6join_message (" + dp.getStr(DPI.JOIN_MESSAGE) + ")",
			"&6quit_message (" + dp.getStr(DPI.QUIT_MESSAGE) + ")",
			cc(dp, "alliance_color") + "alliance_color <color> (alliance chat color)",
			cc(dp, "global_color") + "global_color <color> (global chat color)",
			cc(dp, "pm_color") + "pm_color <color> (private chat color)",
			cc(dp, "pvp") + "pvp (fight people)"
		 };

		 for (String s : messages){
			 JSONChatMessage msg = new JSONChatMessage(main.AS("&7\u2744 &b" + s.substring(2, s.indexOf("(")) + "&6 " + s.substring(s.indexOf("("))), null, null);
			 JSONChatExtra extra = null;
			 
			 if (s.startsWith("&2")){
				 extra = new JSONChatExtra(" " + main.AS("&2[toggle]"), null, null);
			 } else if (s.startsWith("&c")){
				 extra = new JSONChatExtra(" " + main.AS("&c[toggle]"), null, null);
			 } else {
				 extra = new JSONChatExtra(" " + main.AS(s.substring(0, 2) + "[current]"), null, null);
			 }
			 
			 extra.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/toggle " + s.split(" ")[0].substring(2));
			 extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&oToggle " + s.split(" ")[0].substring(2)));
			 msg.addExtra(extra);
			 msg.sendToPlayer(p);
		 }
	 }
	 
	 private String cc(DivinityPlayer dp, String type){
		 
		 String bool = "false";
		 
		 if (type.equals("alliance_color") || type.equals("global_color") || type.equals("pm_color")){
			 bool = dp.getStr(DPI.valueOf(type.toUpperCase()));
		 } else {
			 bool = dp.getStr(DPI.valueOf(type.toUpperCase() + "_TOGGLE"));
		 }
		 
		 if (bool.equals("true")){
			 return "&2";
		 } else if (bool.equals("false") || bool.equals("none")){
			 return "&c";
		 } else {
			 return bool;
		 }
	 }
}