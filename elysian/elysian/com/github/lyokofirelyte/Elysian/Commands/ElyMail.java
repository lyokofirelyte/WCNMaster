package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElyMail implements AutoRegister<ElyMail> {

	 Elysian main;
	 
	 @Getter
	 private ElyMail type = this;
	 
	 public ElyMail(Elysian i){
		 main = i;
	 }
	 
	 private String[] messages = new String[]{
		"/mail send <player> <message>",
		"/mail send staff <message>",
		"/mail send alliance <message>",
		"/mail send all <messsage>",
		"/mail read",
		"/mail clear"
	 };
	 
	 @DivCommand(aliases = {"mail"}, desc = "Elysian Mail Command", help = "/mail help", player = true, min = 1)
	 public void onMail(Player p, String[] args){
		 
		 
		 String msg = args.length >= 3 ? args[2] : "";
		 String perm = "wa.member";
		 boolean send = true;
		 
		 for (int i = 3; i < args.length; i++){
			 msg = msg + " " + args[i];
		 }
		 
		  switch (args[0].toLowerCase()){
		  
		  	  case "read":
		  		  
		  		  checkMail(p);
		  		  
		  	  break;
		  	  
		  	  case "clear":
		  		  
		  		  DivinityStorageModule clearing = null;
		  		  
		  		  if (p instanceof Player){
		  			clearing = main.api.getDivPlayer((Player)p);
		  		  } else {
		  			clearing = main.api.getDivSystem();
		  		  }
		  		  
		  		  if (clearing.getList(DPI.MAIL).size() > 0){
		  			  clearing.set(DPI.MAIL, new ArrayList<String>());
		  			  main.s(p, "none", "Cleared!");
		  		  } else {
		  			  main.s(p, "none", "&c&oYou have no mail.");
		  		  }
		  		  
		  	  break;

			  case "help":
				  
				  for (String s : messages){
					  main.s(p, "none", s);
				  }
				  
			  break;
			  
			  case "send":
				  
				  if (args.length < 3){
					  main.help("mail", this);
				  }
				  
				  switch (args[1]){
				  
					  case "staff":
						  
						  if (main.api.perms(p, "wa.staff.intern", false)){
							  perm = "wa.staff.intern";
						  } else {
							  send = false;
						  }
						  
					  break;
					  
					  case "alliance":
						  
						  if (p instanceof Player){
							  DivinityPlayer dp = main.api.getDivPlayer((Player)p);
							  if (!dp.getStr(DPI.ALLIANCE_NAME).equals("none")){
								  perm = "wa.alliance." + dp.getStr(DPI.ALLIANCE_NAME);
							  } else {
								  main.s(p, "none", "&c&oYou are not in an alliance.");
								  send = false;
							  }
						  } else {
							  main.s(p, "none", "&c&oConsole can not send mail to alliances.");
							  send = false;
						  }
						  
					  break;
					  
					  case "all":
						  
						  if (!main.api.perms(p, "wa.staff.intern", false)){
							  send = false;
						  }
						  
					  break;
					  
					  default:
						  
						  if (main.api.doesPartialPlayerExist(args[1])){
							  send = false;
							  main.api.getDivPlayer(args[1]).getList(DPI.MAIL).add("personal" + "%SPLIT%" + p.getName() + "%SPLIT%" + msg);
							  if (Bukkit.getPlayer(main.api.getDivPlayer(args[1]).getUuid()) != null){
								  main.s(Bukkit.getPlayer(main.api.getDivPlayer(args[1]).getUuid()), "none", "You've recieved a mail! /mail read");
							  }
							  main.s(p, "Mail sent!");
						  } else {
							  main.s(p, "playerNotFound");
							  send = false;
						  }
						  
					  break;
				  
				  }
				  
				  if (send){
					  for (DivinityStorageModule dp : main.api.getOnlineModules().values()){
						  if (dp.getTable().equals("users") && dp.getList(DPI.PERMS).contains(perm)){
							  dp.getList(DPI.MAIL).add(perm + "%SPLIT%" + p.getName() + "%SPLIT%" + msg);
							  if (Bukkit.getPlayer(dp.getUuid()) != null){
								  main.s(Bukkit.getPlayer(dp.getUuid()), "none", "You've recieved a mail! /mail read");
							  }
						  }
					  }
					  main.s(p, "Mail sent!");
				  }
				  
			  break;
		  }
	 }
	 
	 public void checkMail(Player p){
		 
		 DivinityPlayer reading = main.api.getDivPlayer(p);
 		  
		 if (reading.getList(DPI.MAIL).size() > 0){
 			  
			 main.s(p, "none", "Reading mail &6(" + reading.getList(DPI.MAIL).size() + ")");

			 for (String s : reading.getList(DPI.MAIL)){
 				  
				 String[] split = s.split("%SPLIT%");
				 String newLine = "";
 				  
				 switch (split[0]){
 				  
					 case "personal":
		  					  
						 newLine = "&6" + split[1] + " &7-> &6you&f: &7" + split[2];
		  					  
					 break;
		  				  
					 case "wa.staff.intern":
		  					  
						 newLine = "&6" + split[1] + " &7-> &cstaff&f: &7" + split[2];
		  					  
					 break;
		  				  
					 case "wa.member":
		  					  
						 newLine = "&6" + split[1] + " &7-> &2global&f: &7" + split[2];
		  					  
					 break;
		  				  
					 default:
		  					  
						 if (s.startsWith("wa.alliance")){
							 newLine = "&6" + split[1] + " &7-> &3alliance&f: &7" + split[2];
						 }
		  					  
					 break;
				 }
				 
				 main.s(p, newLine);
			 }
		 } else {
			 main.s(p, "none", "&c&oYou have no mail.");
		 }
	 }
}	