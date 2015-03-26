package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.ElyCrate;
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

			 if(args.length == 2){
				 if(main.api.doesPartialPlayerExist(args[1])){
					 if(main.api.isOnline(args[1])){
						 Bukkit.getPlayer(args[1]).getInventory().addItem(new ElyCrate(main).getKey(512));
						 main.s(p, "Player &6" + args[1] + " &bgiven a key as thanks for signing up&6 " + args[0]);
						 main.s(Bukkit.getPlayer(args[1]), "You were giving a key as thank you for signing up &6" + args[0]);
					 }else{
						 List<String> users = new ArrayList<String>(main.api.getDivSystem().getList(DPI.CRATE_PLAYER_LIST));
						 users.add(args[1]);
						 main.api.getDivSystem().set(DPI.CRATE_PLAYER_LIST, users);
						 main.s(p, "Player &6" + args[1] + " &bis not online, the system will autmatically give him a reward when he logs on.");
					 }
				 }else{
					 main.s(p, "Player not found!");
				 }
			 }

	 }
	 
	 @DivCommand(perm = "wa.guest", aliases = {"member"}, desc = "Send the forum link", help = "/member", player = false, min = 0)
	 public void onMember(CommandSender p, String[] args){
		 
		 DivinityUtilsModule.bc("--------------------------------------------");
		 
		 JSONChatMessage msg = new JSONChatMessage("", null, null);
		 JSONChatExtra extra = new JSONChatExtra(main.AS("&aClick here to sign up!"), null, null);
		 extra.setClickEvent(JSONChatClickEventType.OPEN_URL, "http://www.minecraftforum.net/forums/servers/pc-servers/hybrid-servers/773300-worlds-apart-1-7-survival-vanilla-creative-ranks");
		 msg.addExtra(extra);
		 
		 if(main.api.perms(p, "wa.staff.intern", true)){
			 msg.sendToAllPlayers();
		 }else{
			 msg.sendToPlayer((Player)p);
		 }
		 
		 DivinityUtilsModule.bc("--------------------------------------------");
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"rec"}, desc = "Gives a player a key in case staff forgot to give them one on /newmember.", help = "/rec <user>", player = false, min = 0)
	 public void onForgot(CommandSender p, String[] args){
		 
		 if(main.api.doesPartialPlayerExist(args[0])){
			 if(Bukkit.getPlayer(args[0]).isOnline()){
				 Bukkit.getPlayer(args[0]).getInventory().addItem(new ElyCrate(main).getKey(512));
				 main.s(p, "Player &6" + args[0] + " &bgiven a key as thanks for signing up a new member!");
				 main.s(Bukkit.getPlayer(args[0]), "You were giving a key as thank you for signing up a new member!");
			 }else{
				 List<String> users = new ArrayList<String>(main.api.getDivSystem().getList(DPI.CRATE_PLAYER_LIST));
				 users.add(args[0]);
				 main.api.getDivSystem().set(DPI.CRATE_PLAYER_LIST, users);
				 main.s(p, "Player &6" + args[0] + " &bis not online, the system will autmatically give him a reward when he logs on.");
			 }
		 }else{
			 main.s(p, "Player not found!");
		 }
		 
	 }
	 
	 
}


