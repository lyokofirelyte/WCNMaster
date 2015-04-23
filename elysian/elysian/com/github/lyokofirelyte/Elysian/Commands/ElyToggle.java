package com.github.lyokofirelyte.Elysian.Commands;

import lombok.Getter;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class ElyToggle implements AutoRegister<ElyToggle> {

	 Elysian main;
	 
	 @Getter
	 private ElyToggle type = this;
	 
	 public ElyToggle(Elysian i){
		 main = i;
	 }
	 
	 @GameCommand(aliases = {"toggle", "tog"}, desc = "Toggle various settings", help = "/toggle help", player = true)
	 public void onToggle(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 
		 if (args.length == 0){
			 help(p, dp, true);
		 } else {
			 switch (args[0]){
			 
			 	case "help": case "list":
			 		
			 		help(p, dp, true);
			 		
			 	break;
			 	
			 	default:
			 		
			 		try {
			 			
			 			if (args.length == 1){
			 				dp.set(DPI.valueOf(args[0].toUpperCase() + "_TOGGLE"), !dp.getBool(DPI.valueOf(args[0].toUpperCase() + "_TOGGLE")));
			 			} else {
			 				dp.set(DPI.valueOf(args[0].toUpperCase()), DivinityUtilsModule.createString(args, 1));
			 			}
			 			
			 			main.s(p, "none", "&o" + args[0] + " updated");
			 			help(p, dp, false);
			 			
			 		} catch (Exception e){
			 			help(p, dp, true);
			 		}
			 		
			 	break;
			 }
		 }
	 }
	 
	 private void help(Player p, DivinityPlayer dp, boolean showHelp){
		 
		 main.s(p, "none", "Click the [toggle] to toggle.");
		 
		 String[] msgs = new String[]{
			"ALLIANCE", "SCOREBOARD", "POKES", "FIREWORKS",
			"DEATH LOCS", "PARTICLES", "DISPLAY NAME OVERRIDE",
			"CHAT FILTER", "REGION POPUPS"
		 };
		 
		 DPI[] enums = new DPI[]{
			DPI.ALLIANCE_TOGGLE, DPI.SCOREBOARD_TOGGLE, DPI.POKES_TOGGLE, DPI.FIREWORKS_TOGGLE,
			DPI.DEATHLOCS_TOGGLE, DPI.PARTICLES_TOGGLE, DPI.XP_DISP_NAME_TOGGLE,
			DPI.CHAT_FILTER_TOGGLE, DPI.REGION_TOGGLE
		 };
		 
		 JSONChatMessage jsonMessage = new JSONChatMessage("", null, null);
		 int x = 0;
		 
		 for (int i = 0; i < msgs.length; i++){
			 JSONChatExtra extra = new JSONChatExtra(Utils.AS("&6[" + (dp.getBool(enums[i]) + "").replace("true", "&a").replace("false", "&c") + msgs[i] + "&6] "), null, null);
			 extra.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/toggle " + enums[i].toString().replace("_TOGGLE", ""));
			 extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, Utils.AS("&6&oToggle this setting!"));
			 jsonMessage.addExtra(extra);
			 if (x == 3 || i == msgs.length-1){
				 x = 0;
				 jsonMessage.sendToPlayer(p);
				 jsonMessage = new JSONChatMessage("", null, null);
			 }
			 x++;
		 }
		 
		 if (showHelp){
			 Utils.s(p, "&7&ojoin_message, quit_message, global_color, pm_color, and alliance_color must be changed via /toggle <toggle> <message>");
		 }
	 }
}