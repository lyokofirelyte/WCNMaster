package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityStorage;


public class ElyEconomy implements AutoRegister {

	 Elysian main;
	 
	 public ElyEconomy(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(aliases = {"pay"}, desc = "Pay someone money!", help = "/pay <player> <amount>", player = true, min = 2)
	 public void onPay(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 int bal = 0;
		 DivinityPlayer who = null;
		 
		 if (main.api.doesPartialPlayerExist(args[0])){
			 
			 if (DivinityUtilsModule.isInteger(args[1]) && dp.getInt(DPI.BALANCE) > (bal = Integer.parseInt(args[1]))){
					 
				if(bal > 0){
						
					 who = main.api.getDivPlayer(args[0]);
					 who.set(DPI.BALANCE, who.getInt(DPI.BALANCE)+bal);
					 dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE)-bal);
					 
					 main.s(p, "none", "You sent &6" + bal + " &bto " + who.getStr(DPI.DISPLAY_NAME) + "&b.");
					 
					 if (Bukkit.getPlayer(who.uuid()) != null){
			 			main.s(Bukkit.getPlayer(who.uuid()), "none", "You were paid &6" + bal + " &bby " + p.getDisplayName() + "&b.");
					 }
					 
				 }else{
					 main.s(p, "That number is negative!");
				 }
					
			 } else {
				 main.s(p, "invalidNumber");
			 }
			 
		 } else {
			 main.s(p, "playerNotFound");
		 }
	 }
	 
	 @DivCommand(aliases = {"balance", "bal"}, desc = "Ely Bal Command", help = "/balance <set, take, give, top> [player] [amount]", player = true, min = 0, max = 3)
	 public void onBal(Player p, String[] args){
		 
		 if (args.length == 0){
			 main.s(p, "balance");
		 } else {
			
			 switch (args[0].toLowerCase()){
			 
			 	default:
			 		
			 		main.s(p, "none", main.help("balance", this));
			 		
			 	break;
			 
			 	case "top":
			 		balTop(p);
				break;
				
			 	case "set": case "take": case "give":
						
			 		if (main.api.perms(p, "wa.staff.admin", false)){
			 			
				 		DivinityPlayer who = null;
				 		int bal = 0;
				 		
			 			if (main.api.doesPartialPlayerExist(args[1])){
			 				
			 				if (DivinityUtilsModule.isInteger(args[2])){
			 						
			 					bal = Integer.parseInt(args[2]);
			 						
			 					if (bal > 1 && bal < 5000000){
			 						who = main.api.getDivPlayer(args[1]);
			 						
						 			if (args[0].toLowerCase().equals("take")){
						 				
						 				who.set(DPI.BALANCE, who.getInt(DPI.BALANCE)-bal);
						 				
						 				if (who.getInt(DPI.BALANCE) < 0){
						 					who.set(DPI.BALANCE, 0);
						 				}
						 				
						 			} else if (args[0].toLowerCase().equals("give")){
						 				who.set(DPI.BALANCE, who.getInt(DPI.BALANCE)+bal);
						 			} else {
						 				who.set(DPI.BALANCE, bal);
						 			}
						 			
						 			main.s(p, "none", "Set the balance of &6" + who.getStr(DPI.DISPLAY_NAME) + " &bto &6" + who.getStr(DPI.BALANCE) + "&b.");
						 			
						 			if (Bukkit.getPlayer(who.uuid()) != null){
						 				main.s(Bukkit.getPlayer(who.uuid()), "none", "Your balance was set to &6" + who.getStr(DPI.BALANCE) + " &bby " + p.getDisplayName() + "&b.");
						 			}
						 			
			 					} else {
			 						main.s(p, "invalidNumber");
			 					}
			 				} else {
			 					main.s(p, "invalidNumber");
			 				}
			 			} else {
				 			main.s(p, "playerNotFound");
				 		}
			 		}
			 	break;
			 }
		 }
	 }
	 
	 private void balTop(Player sendTo){
		 
		 List<Integer> balances = new ArrayList<>();
		 Map<Integer, DivinityPlayer> players = new THashMap<Integer, DivinityPlayer>();
		 int serverTotal = 0;
		 
		 for (DivinityStorage p : main.divinity.api.getAllPlayers()){
			 if (p.getInt(DPI.BALANCE) > 2000){
				 balances.add(p.getInt(DPI.BALANCE));
				 players.put(p.getInt(DPI.BALANCE), (DivinityPlayer)p);
				 serverTotal = serverTotal + p.getInt(DPI.BALANCE);
			 }
		 }
		 
		 Collections.sort(balances);
		 Collections.reverse(balances);
		 
		 main.s(sendTo, "none", "Top Balances");
		 main.s(sendTo, "none", "&6Server Total: &b" + serverTotal);
		 
		 for (int i = 0; i < 10; i++){
			 if (balances.size() > i){
				 main.s(sendTo, players.get(balances.get(i)).getStr(DPI.DISPLAY_NAME) + "&f: &6" + balances.get(i));
			 }
		 }
	 }
}