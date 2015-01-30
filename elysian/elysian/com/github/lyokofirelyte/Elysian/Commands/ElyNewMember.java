package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class ElyNewMember implements AutoRegister {

	 Elysian main;
	 
	 public ElyNewMember(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"newmember"}, desc = "Add a new member", help = "/newmember <user>", player = false, min = 1)
	 public void onNewMember(CommandSender p, String[] args){

		 if (main.api.doesPartialPlayerExist(args[0])){
			 main.api.getDivPlayer(args[0]).getList(DPI.PERMS).add("wa.member");
			 main.s(p, "Added permissions!");
			 
			 if (main.api.isOnline(args[0])){
				 main.api.getPlayer(args[0]).performCommand("rankup");
			 }
			 
		 } else {
			 List<String> users = new ArrayList<String>(main.api.getDivSystem().getList(DPI.PRE_APPROVED));
			 users.add(args[0]);
			 main.api.getDivSystem().set(DPI.PRE_APPROVED, users);
			 
			 main.s(p, "The player hasn't logged in yet, therefore he's added to the pre-approved list. He will automatically become member if he logs in.");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"member"}, desc = "Send the forum link", help = "/member", player = false, min = 0)
	 public void onMember(CommandSender p, String[] args){
		 
		 DivinityUtilsModule.bc("--------------------------------------------");
		 
		 JSONChatMessage msg = new JSONChatMessage("", null, null);
		 JSONChatExtra extra = new JSONChatExtra(main.AS("&aClick here to sign up!"), null, null);
		 extra.setClickEvent(JSONChatClickEventType.OPEN_URL, "http://www.minecraftforum.net/forums/servers/pc-servers/hybrid-servers/773300-worlds-apart-1-7-survival-vanilla-creative-ranks");
		 msg.addExtra(extra);
		 msg.sendToAllPlayers();
		 
		 DivinityUtilsModule.bc("--------------------------------------------");
	 }
}