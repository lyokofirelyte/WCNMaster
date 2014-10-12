package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.command.CommandSender;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class ElyPerms implements AutoRegister {

	 Elysian main;
	 
	 public ElyPerms(Elysian i){
		 main = i;
		 fillMap();
	 }
	 
	 public List<String> memberGroups = Arrays.asList(
		"member",
		"dweller",
		"settler",
		"villager",
		"townsman",
		"citizen",
		"districtman",
		"statesman",
		"regional",
		"national",
		"continental",
		"emperor",
		"immortal"
	 );
	 
	 public List<String> staffGroups = Arrays.asList(
		"owner",
		"admin",
		"mod2",
		"mod",
		"intern"
	 );
	 
	 public Map<String, String> rankNames = new THashMap<String, String>();
	 
	 private void fillMap(){
		 rankNames.put("member", "&7 % App % 2 % Build, Community Access, /sethome, /bal top");
		 rankNames.put("dweller", "&1 % 5k % 2.25 % /notepad, /seen, /suicide, /wealth");
		 rankNames.put("settler", "&e % 10k % 2.5 % chat formatting, sign color");
		 rankNames.put("villager", "&6 % 15k % 2.75 % disposal sign, /hat, /chestplate, /leggings, /boots");
		 rankNames.put("townsman", "&5 % 30k % 3 % 2nd home, /ci, mine n dash");
		 rankNames.put("citizen", "&d % 50k % 3.25 % blink access, /workbench, /home during combat");
		 rankNames.put("districtman", "&9 % 100k % 3.5 % /near, double mob money, /pt (powertools)");
		 rankNames.put("statesman", "&c % 175k % 3.75 % /tpa, /firework");
		 rankNames.put("regional", "&a % 275k % 4 % 3rd home, /rainoff (limited)");
		 rankNames.put("national", "&b % 425k % 4.25 % /feed, /flare");
		 rankNames.put("continental", "&2 % 600k % 4.5 % multiple power tools, /heal");
		 rankNames.put("emperor", "&3 % 800k % 4.74 % triple mob money, /tpahere");
		 rankNames.put("immortal", "&4 % 1m % 5 % amplified world, 4th home, /soar (limited)");
	 }
	 
	 public void deRank(DivinityPlayer dp){
		 
		 for (int i = memberGroups.size()-1; i >= 0; i--){
			 if (dp.getList(DPI.PERMS).contains("wa.rank." + memberGroups.get(i))){
				 if (!memberGroups.get(i).equals("member")){
					 dp.getList(DPI.PERMS).remove("wa.rank." + memberGroups.get(i));
					 String[] rank = ((ElyPerms) main.api.getInstance(ElyPerms.class)).rankNames.get(memberGroups.get(i)).split(" % ");
					 dp.set(DPI.RANK_COLOR, rank[0]);
					 dp.set(DPI.RANK_DESC, rank[0] + memberGroups.get(i).substring(0, 1).toUpperCase() + memberGroups.get(i).substring(1) + "\n" + "&6" + rank[3].replace(", ", "&7, &6"));
					 dp.set(DPI.RANK_NAME, !dp.getList(DPI.PERMS).contains("wa.staff.intern") ? memberGroups.get(i).substring(0, 1).toUpperCase() : dp.getStr(DPI.RANK_NAME));
				 } else {
					 dp.err("Could not derank you lower than member.");
				 }
				 break;
			 }
		 }
	 }
	 
	 @DivCommand(aliases = {"perms"}, desc = "Modify Permissions and Groups", help = "/perms <add, groupadd, remove, groupremove, view, list> <player> <perm>, /perms view <player> all", player = false, min = 1)
	 public void onPerms(CommandSender cs, String[] args){
		 
		 if (cs.isOp()){
			 
			 if (args.length == 3){
				 
				 if (main.api.doesPartialPlayerExist(args[1])){
					 
					 DivinityPlayer dp = main.api.getDivPlayer(args[1]);
			 
					 switch (args[0].toLowerCase()){
					 
					 	case "derank":
					 		
					 		deRank(dp);
					 		
					 	break;
					 
					 	case "groupadd": case "groupremove":
					 		
					 		if (staffGroups.contains(args[2].toLowerCase())){
					 			for (int i = 0; i < staffGroups.size(); i++){
					 				if (staffGroups.get(i).equalsIgnoreCase(args[2]) || staffGroups.indexOf(args[2]) < i){
					 					if (args[0].equalsIgnoreCase("groupadd")){
						 					if (!dp.getList(DPI.PERMS).contains("wa.staff." + staffGroups.get(i))){
						 						dp.getList(DPI.PERMS).add("wa.staff." + staffGroups.get(i));
						 						main.s(cs, "Group added.");
						 					}
					 					} else {
						 					if (dp.getList(DPI.PERMS).contains("wa.staff." + staffGroups.get(i))){
						 						dp.getList(DPI.PERMS).remove("wa.staff." + staffGroups.get(i));
						 						main.s(cs, "Group removed.");
						 					}
					 					}
					 				}
					 			}
					 		} else if (memberGroups.contains(args[2].toLowerCase())){
					 			List<String> mg = new ArrayList<String>(memberGroups);
					 			Collections.reverse(mg);
					 			for (int i = 0; i < mg.size(); i++){
					 				if (mg.get(i).equalsIgnoreCase(args[2]) || mg.indexOf(args[2]) < i){
					 					if (args[0].equalsIgnoreCase("groupadd")){
						 					if (!dp.getList(DPI.PERMS).contains("wa.rank." + mg.get(i))){
						 						if (mg.get(i).equals("member") && !dp.getList(DPI.PERMS).contains("wa.member")){
						 							dp.getList(DPI.PERMS).add("wa." + mg.get(i));
						 						} else {
						 							dp.getList(DPI.PERMS).add("wa.rank." + mg.get(i));
						 						}
						 						main.s(cs, "Group added.");
						 					}
					 					} else {
						 					if (dp.getList(DPI.PERMS).contains("wa.rank." + mg.get(i))){
						 						dp.getList(DPI.PERMS).remove("wa.rank." + mg.get(i));
						 						main.s(cs, "Group removed.");
						 					}
					 					}
					 				}
					 			}
					 		} else {
					 			main.s(cs, "none", "&cInvalid group, see /perms list.");
					 		}
					 		
					 	break;
					 
					 	case "add":
					 		
				 			if (!dp.getList(DPI.PERMS).contains(args[2])){
				 				dp.getList(DPI.PERMS).add(args[2]);
				 				main.s(cs, "none", "Permission &6" + args[2] + " &badded!");
				 			} else {
				 				main.s(cs, "none", "&cThat player already has that permission.");
				 			}
					 		
					 	break;
					 	
					 	case "remove":

					 		if (dp.getList(DPI.PERMS).contains(args[2])){
					 			dp.getList(DPI.PERMS).remove(args[2]);
					 			main.s(cs, "none", "Permission &6" + args[2] + " &bremoved!");
					 		} else {
					 			main.s(cs, "none", "&cThat player does not have that permission.");
					 		}

					 	break;
					 	
					 	case "view":
					 		
					 		List<String> perms = new ArrayList<String>();

					 		for (String s : dp.getList(DPI.PERMS)){
					 			if (s.startsWith(args[2]) || args[2].equals("all")){
					 				perms.add(s);
					 			}
					 		}

					 		for (String s : perms){
					 			main.s(cs, "none", "&6" + s);
					 		}
					 		
					 	break;
		
					 	default:
					 		
					 		main.s(cs, "none", main.help("perms", this));
					 		
					 	break;
					 }
			 
				 } else {
					 main.s(cs, "playerNotFound");
				 }
				 
			 } else if (args.length == 1 && args[0].equals("list")){
				 
				 main.s(cs, "none", "Groups (includes all permissions related)");
				 
				 for (String s : staffGroups){
					 main.s(cs, "none", "&6" + s);
				 }
				 
				 for (String s : memberGroups){
					 main.s(cs, "none", "&6" + s);
				 }
			 }
		 } else {
			 main.s(cs, "noPerms");
		 }
	 }
}