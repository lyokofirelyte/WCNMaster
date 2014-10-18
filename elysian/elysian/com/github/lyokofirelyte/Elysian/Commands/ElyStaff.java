package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Gui.GuiChest;
import com.github.lyokofirelyte.Spectral.DataTypes.DAI;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyChannel;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityStorage;

public class ElyStaff implements Listener, AutoRegister {

	 Elysian main;
	 
	 public ElyStaff(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(perm = "wa.staff.admin", aliases = {"backup"}, desc = "File Backup Command", help = "/backup", player = false)
	 public void onBackup(CommandSender cs, String[] args){
		 main.api.backup();
		 main.s(cs, "Backup Complete!");
	 }
	 
	 @DivCommand(perm = "wa.staff.mod", aliases = {"markkit"}, desc = "Lookup command", help = "/markkit <player> <page>", player = false, min = 2)
	 public void onMarkkit(CommandSender cs, String[] args){
		 if(main.api.doesPartialPlayerExist(args[0])){
			 if(DivinityUtilsModule.isInteger(args[1])){
				 
				 main.s(cs, "&3Looking up player &6" + args[0] + "&3!");
				 
				 int page = Integer.parseInt(args[1]);
				 
				 DivinityPlayer dp = main.api.getDivPlayer(args[0]);
				 
				 List<String> logList = dp.getList(DPI.MARKKIT_LOG);
				 List<String> log = Lists.reverse(logList);
				 List<String> result = new ArrayList<String>();
//				 for(int i = 0; i < 20; i++){
//						 main.s(cs, "&b" + log.get(i));
//				 }
				 if(page != 0){
					 for(int i = 20 * page; i < 20 * page * 2; i++){
						 try{
							 result.add(log.get(i));
						 }catch(Exception e){}
					 }
				 }else{
					 for(int i = 0; i < 20; i++){
						 try{
							 result.add(log.get(i));
						 }catch(Exception e){}					 
					}
				 }

				 main.s(cs, Lists.reverse(result));
			 }else{
				 main.s(cs, "&cThat is not a number!");
			 }
		 }else{
			 main.s(cs, "&cCouldn't find player " + args[0]);
		 }
	 }
	 
	 /*@DivCommand(perm = "wa.staff.admin", aliases = {"ts3auth"}, desc = "Set TS3Auth Info", help = "/ts3auth <user> <pass>", player = false, min = 2)
	 public void onTS3Auth(CommandSender p, String[] args){
		 
		 if (args[0].equals("stop")){
			 main.api.ts3.stop();
		 } else {
			 main.api.getDivSystem().set(DPI.TS3_CREDENTIALS, args[0] + " " + args[1]);
			 main.api.ts3.start();
			 main.s(p, "Updated.");
		 }
	 }*/
	 
	 /*@DivCommand(aliases = {"register"}, desc = "Register on the website!", help = "/register <pass>", player = true, min = 1)
	 public void onRegister(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 Map<String, Object> input = new THashMap<String, Object>();
		 input.put("username", p.getName());
		 input.put("password", args[0]);
		 
		 JSONObject result;
		 
		 if (!dp.getBool(DPI.REGISTERED)){
			 result = main.getWeb().sendPost("/api/register", input);
			 main.s(p, result.get("success").toString().replace("true", "&aSuccess!").replace("false", "&cFailed to create account!"));
			 if ((boolean) result.get("success")){
				 dp.set(DPI.REGISTERED, true);
			 }
		 } else {
			 dp.err("You're already registered.");
		 }
	 }*/
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"invsee"}, desc = "Inventory Spy Command", help = "/invsee <player>", player = true, min = 1)
	 public void onInvSee(Player p, String[] args){
		 
		 if (main.api.isOnline(args[0])){
			 p.openInventory(main.api.getPlayer(args[0]).getInventory());
		 } else {
			 main.s(p, "playerNotFound");
		 }
	 }

	 @DivCommand(perm = "wa.staff.admin", aliases = {"ip"}, desc = "IP & Location Information", help = "/ip <player>", min = 1)
	 public void onIP(CommandSender cs, String[] args){
		 
		 if (main.api.isOnline(args[0])){
			 
			 try {
				 Player p = main.api.getPlayer(args[0]);
				 main.s(cs, "&6Location Overview: " + p.getDisplayName());
				 main.s(cs, "IP: &3" + p.getAddress().getHostName());
				 main.s(cs, "Port: &3" + p.getAddress().getPort());
				 main.s(cs, "Country: &3" + main.divinity.api.playerLocation.getCountry(p));
				 main.s(cs, "City: &3" + main.divinity.api.playerLocation.getCity(p));
				 main.s(cs, "Postal Code: &3" + main.divinity.api.playerLocation.getPostal(p));
			 } catch (Exception e){
				 main.s(cs, "&c&oAn error occured fetching the information.");
			 }
			 
		 } else {
			 main.s(cs, "&c&oThat player is not online.");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"stafftp"}, desc = "Staff Grief Teleport Check Command", help = "/stafftp <player>", player = true)
	 public void onStaffTp(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 
		 if (dp.getBool(DPI.IS_STAFF_TP)){
			 dp.set(DPI.IS_STAFF_TP, false);
			 p.teleport(dp.getLoc(DPI.GV1));
			 p.setFlying(false); p.setAllowFlight(false);
			 dp.set(DPI.GV1, "none");
			 return;
		 }
		 
		 if (args.length == 1 && main.api.doesPartialPlayerExist(args[0]) && main.api.isOnline(args[0])){
			 dp.set(DPI.IS_STAFF_TP, true);
			 dp.set(DPI.GV1, p.getLocation());
			 p.setAllowFlight(true); p.setFlying(true);
			 p.teleport(main.api.getPlayer(args[0]));
			 dp.s("Type /stafftp again to end this session.");
			 ElyChannel.STAFF.send("&6System", p.getDisplayName() + " &c&ostaff teleported to " + main.api.getPlayer(args[0]).getDisplayName() + "&c&o!", main.api);
		 } else {
			 dp.err("That player is not online or not in the API!");
		 }
	 }
	 
	 @DivCommand(perm = "wa.rank.citizen", aliases = {"workbench", "wb"}, desc = "Open a Workbench", help = "/wb", player = true)
	 public void onWB(Player p, String[] args){
		 p.openWorkbench(p.getLocation(), true);
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases= {"dis"}, desc = "Spooky Disguise Command", help = "/dis <mob>", player = true)
	 public void onDis(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 EntityType type = null;
		 
		 if (dp.getBool(DPI.IS_DIS)){
			 
			 if (!((LivingEntity)dp.getRawInfo(DPI.DIS_ENTITY)).isDead()){
				 ((LivingEntity)dp.getRawInfo(DPI.DIS_ENTITY)).remove();
			 }
			 
			 dp.set(DPI.DIS_ENTITY, "none");
			 dp.set(DPI.IS_DIS, false);
			 p.removePotionEffect(PotionEffectType.INVISIBILITY);
			 dp.s("Back to normal!");
			 
		 } else {
		 
			 try {
				 type = EntityType.valueOf(args[0].toUpperCase());
				 LivingEntity e = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), type);
				 e.setMaxHealth(Double.MAX_VALUE);
				 e.setHealth(Double.MAX_VALUE);
				 dp.set(DPI.IS_DIS, true);
				 dp.set(DPI.DIS_ENTITY, e);
				 p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 3), true);
				 e.setPassenger(p);
			 } catch (Exception e){
				 main.s(p, "&c&oThat entity does not existttttt!@#$%^& rawr &omeow");
			 }
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.admin", aliases = {"sunday"}, desc = "Sunday Balance Increase", help = "/sunday", player = false)
	 public void onSunday(CommandSender cs, String[] args){
		 
		 String who = cs instanceof Player ? ((Player)cs).getDisplayName() : "&6Console";
		 if(main.hasSunDayBeenPerformedBefore == false){
			 main.hasSunDayBeenPerformedBefore = true;
			 for (DivinityStorage dp : main.divinity.api.getAllPlayers()){
				 List<String> groups = new ArrayList<String>(((ElyPerms)main.api.getInstance(ElyPerms.class)).memberGroups);
				 Collections.reverse(groups);
				 for (String group : groups){
					 if (dp.getList(DPI.PERMS).contains("wa." + ("rank." + group).replace("rank.member", "member"))){
						 float amt = Float.parseFloat(((ElyPerms)main.api.getInstance(ElyPerms.class)).rankNames.get(group).split(" % ")[2])/100;
						 float amount = dp.getInt(DPI.BALANCE)*amt;
						 dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) + Math.round(amount));
						 dp.getList(DPI.MAIL).add("personal" + "%SPLIT%" + who + "%SPLIT%" + "Sunday balance updated! You were given " + Math.round(amount) + " this week!");
						 
						 if (Bukkit.getPlayer(dp.uuid()) != null){
							 main.s(Bukkit.getPlayer(dp.uuid()), "none", "You've recieved a mail! /mail read");
						 }
						 
						 break;
					 }
				 }
			 }
		 }else{
			 main.s(cs, "Sunday balance has already been done!");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"clear"}, desc = "Clear items on floor (and monsters)", help = "/clear <radius>", player = true, min = 1)
	 public void onClear(Player p, String[] args){
		 
		 if (DivinityUtilsModule.isInteger(args[0]) && Integer.parseInt(args[0]) <= 500){
			 
			 Double d = Double.parseDouble(args[0]);
			 int killed = 0;
			 
			 for (Entity e : p.getNearbyEntities(d, d, d)){
				 if (e instanceof Player == false && e instanceof ItemFrame == false && e instanceof Painting == false){
					 e.remove();
					 killed++;
				 }
			 }
			 
			 main.s(p, "Removed &6" + killed + " &bentities.");
			 
		 } else {
			 main.s(p, "&c&oThat's not a number, or it's too big.");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.admin", aliases = {"placesign"}, desc = "Place a market sign down", help = "/placesign <down/side>", player = true, min = 1)
	 public void onPlaceDown(Player p, String[] args){
		 
		 Block newSign = p.getWorld().getBlockAt(new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()));
		 newSign.setType(args[0].equals("side") ? Material.WALL_SIGN : Material.SIGN_POST);
			 
		 Sign s = (Sign) newSign.getState();
		 s.setLine(0, "§bEly §3Markkit");
			 
		 ConfigurationSection configSection = main.api.getDivSystem().getMarkkit().getConfigurationSection("Items");
		 String text = "§fNot Found";
			 
		 for (String path : configSection.getKeys(false)){
			 if((Integer.parseInt(main.api.getDivSystem().getMarkkit().getString("Items." + path + ".ID")) == p.getItemInHand().getTypeId()) && (Integer.parseInt(main.api.getDivSystem().getMarkkit().getString("Items." + path + ".Damage")) == p.getItemInHand().getDurability())){
				 text = "§f" + path;
				 break;
			 }
		 }
		 
		 s.setLine(1, text);
		 org.bukkit.material.Sign sign = new org.bukkit.material.Sign(args[0].equals("side") ? Material.WALL_SIGN : Material.SIGN_POST);
		 sign.setFacingDirection(DivinityUtilsModule.getPlayerDirection(p.getLocation().getYaw()).getOppositeFace());
		 s.setData(sign);
		 s.update();
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"chestview"}, desc = "Chest Lookup / View Command", help = "/chestview <player>, /chestview lookup <player> <item>", player = true, min = 1)
	 public void onChestView(Player p, String[] args){
		 
		 if (args.length != 1 && args.length != 3){
			 main.s(p, main.help("chestview", this));
			 return;
		 }

		 if (main.api.doesPartialPlayerExist(args.length == 1 ? args[0] : args[1])){
			 ((DivInvManager) main.api.getInstance(DivInvManager.class)).displayGui(p, new GuiChest(main, (args.length == 1 ? main.api.getDivPlayer(args[0]) : main.api.getDivPlayer(args[1])), args.length == 1 ? "all" : args[2]));
		 } else {
			main.s(p, "playerNotFound");
		 }
	 }
	 
	 @DivCommand(perm = "wa.rank.dweller", aliases = {"seen"}, desc = "Seen Command", help = "/seen <player>", player = false, min = 1)
	 public void onSeen(CommandSender cs, String[] args){
		 
		 DivinityPlayer dp = null;
		 
		 if (main.api.doesPartialPlayerExist(args[0])){
			 
			 dp = main.api.getDivPlayer(args[0]);
			 Vector v = dp.getLoc(DPI.LOGOUT_LOCATION).toVector();
			 String lastLoc = "&6" + dp.getLoc(DPI.LOGOUT_LOCATION).getWorld().getName() + " &7@ &6" + v.getBlockX() + "&7, &6" + v.getBlockY() + "&7, &6" + v.getBlockZ();
			 String lastLogin = "&6" + dp.getStr(DPI.LAST_LOGIN);
			 String lastLogout = "&6" + dp.getStr(DPI.LAST_LOGOUT);
			 String status = main.api.isOnline(args[0]) ? "&aonline" : "&4offline";
			 
			 main.s(cs, "&3Traffic Stats: " + dp.getStr(DPI.DISPLAY_NAME) + " &3(" + status + "&3)");
			 main.s(cs, "Logout Location: " + lastLoc);
			 main.s(cs, "Last Login: " + lastLogin);
			 main.s(cs, "Last Logout: " + lastLogout);
			 cs.sendMessage(main.AS("&7&oCurrent System Time: " + DivinityUtilsModule.getTimeFull()));
			 
		 } else {
			 main.s(cs, "playerNotFound");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"abandonship"}, desc = "ABANDON SHIP!", help = "/abandonship", player = true, min = 0)
	 public void onAbandon(CommandSender cs, String[] args){
		 main.api.schedule(this, "abandonship", 10L, "abandonship");
		 main.api.schedule(this, "abandonship", 20L, "abandonship");
		 main.api.schedule(this, "kick", 30L, "kick", (Player)cs);
	 }
	 
	 public void abandonship(){
		 for(Player p : Bukkit.getOnlinePlayers()){
			main.s(p, "&4Abandon Ship!");
		 }
	 }
	 
	 public void kick(Player p){
		 p.kickPlayer("§4Abandoned Ship!");
	 }
	 
	 @DivCommand(perm = "wa.staff.mod", aliases = {"speed"}, desc = "Speed Command", help = "/speed <1-10>", player = true, min = 1, max = 2)
	 public void onSpeed(CommandSender cs, String[] args){
		 Player p = (Player)cs;

		 if(DivinityUtilsModule.isInteger(args[0])){
			 float speed = Float.parseFloat(args[0]);
			 
			 if((speed) < 0 || (speed) > 10){
				 main.s(p, "/speed <1-10>");
			 }
			 if(args.length == 1){
				 if(p.isFlying()){
					 p.setFlySpeed(speed/10);
				 }else{
					 p.setWalkSpeed(speed/10);
				 }
				 main.s(p, "Speed updated!");
			 }else{

				 if(main.api.doesPartialPlayerExist(args[1]) && main.api.isOnline(args[1])){
					 Player pl = main.api.getPlayer(args[1]);
					 if(pl.isFlying()){
						 pl.setFlySpeed(speed/10);
					 }else{
						 pl.setWalkSpeed(speed/10);
					 }
					 main.s(pl, "Speed updated!");
					 main.s(p, "Speed updated!");
				 }
			 }
		 }else{
			 main.s(p, "That's not a number!");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"setcast"}, desc = "Set your cast prefix", help = "/setcast <prefix>", player = true, min = 1)
	 public void onSetCast(CommandSender cs, String[] args){
		 
		 Player pl = (Player)cs;
		 
		 StringBuilder prefix = new StringBuilder();
		 
		 for(String s : args){
			 prefix.append(s + "_");
		 }
		 
		 DivinityPlayer p = main.api.getDivPlayer(pl);
		 p.set(DPI.CAST_PREFIX, prefix.toString() + "\u2744");
		 main.s(pl, "People will see: " + prefix.toString().replace("_", " ") + "\u2744" + " Message");
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"cast"}, desc = "Send a broadcast message", help = "/cast <message>", player = true, min = 1)
	 public void onCast(CommandSender cs, String[] args){
		 
		 Player pl = (Player)cs;
		 DivinityPlayer p = main.api.getDivPlayer(pl);
		 String prefix = p.getStr(DPI.CAST_PREFIX);
		 
		 StringBuilder message = new StringBuilder();
		 
		 for(String s : args){
			 message.append(s + " ");
		 }
		 
		 for(Player player : Bukkit.getOnlinePlayers()){
			 player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix.replace("_", " ")) + " " + message);
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.admin", aliases = {"spreadsheet"}, desc = "Enable / disable the spreadsheet", help = "/spreadsheet enable, /spreadsheet disable", min = 1)
	 public void onSpreadSheet(CommandSender cs, String[] args){
		 
		 if (args[0].equals("enable") || args[0].equals("disable")){
			 main.api.getDivSystem().set(DPI.ENABLE_SPREADSHEET, Boolean.valueOf(args[0].replace("enable", "true").replace("disable", "false")));
			 main.s(cs, "The spreadsheet is now " + args[0] + "d.");
			 ElyChannel.STAFF.send("&6System", "The markkit spreadsheet has been &6" + args[0] + "d &cby &6" + (cs instanceof Player ? ((Player) cs).getDisplayName() : "console") + "&c!", main.api);
		 } else {
			 main.help("spreadsheet", this);
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"setmarkkit"}, desc = "Set a market place", help = "/setmarkkit <sellprice> <buyprice> <markkit name>", player = true, min = 3)
	 public void onSetMarket(CommandSender cs, String[] args){
		 
		 Player p = (Player)cs;
		 
		  if (!DivinityUtilsModule.isInteger(args[0]) || !DivinityUtilsModule.isInteger(args[1])){
			  main.s(cs, "That is not a number!");
			  return;
		  }
		  
		  if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR){
			  main.s(p, "You can't have nothing in your hand!");
			  return;
		  }
		  
		  if (p.getItemInHand().getAmount() == 64){
			  
	    	  int buyprice = Integer.parseInt(args[0]);
	    	  int sellprice = Integer.parseInt(args[1]);
	    	  String name = args[2].replace("-", " ");
	    	  ItemStack full = p.getItemInHand();
	    	  
	    	  
	    	  main.api.getDivSystem().getMarkkit().set("Items." + name, null);
	    	  
	    	  main.api.getDivSystem().getMarkkit().set("Items." + name + ".ID", full.getTypeId());
	    	  main.api.getDivSystem().getMarkkit().set("Items." + name + ".Damage", full.getDurability());
			  
	    	  main.api.getDivSystem().getMarkkit().set("Items." + name + ".64.buyprice", buyprice);
			  main.api.getDivSystem().getMarkkit().set("Items." + name + ".64.sellprice", sellprice);
			  
	    	  if(buyprice/2 >= 1){
	    		  main.api.getDivSystem().getMarkkit().set("Items." + name + ".32.buyprice", buyprice/2);
	    		  main.api.getDivSystem().getMarkkit().set("Items." + name + ".32.sellprice", sellprice/2);
	    	  }
	    	  
	    	  if(buyprice/4 >= 1){
	    		  main.api.getDivSystem().getMarkkit().set("Items." + name + ".16.buyprice", buyprice/4);
	    		  main.api.getDivSystem().getMarkkit().set("Items." + name + ".16.sellprice", sellprice/4);
	    	  }
	    	  
	    	  if(buyprice/8 >= 1){
	    		  main.api.getDivSystem().getMarkkit().set("Items." + name + ".8.buyprice", buyprice/8);
	    		  main.api.getDivSystem().getMarkkit().set("Items." + name + ".8.sellprice", sellprice/8);
	    	  }
	    	 	    	  
	    	  if(buyprice/64 >= 1){
	    		  main.api.getDivSystem().getMarkkit().set("Items." + name + ".1.buyprice", buyprice/64);
	    		  main.api.getDivSystem().getMarkkit().set("Items." + name + ".1.sellprice", sellprice/64);
	    	  }
			  main.s(p, "Added succesfully!");

		  } else if(p.getItemInHand().getAmount() == 1){
			  
			  int buyprice = Integer.parseInt(args[0]);
			  int sellprice = Integer.parseInt(args[1]);
			  String name = args[2].replace("-", " ");
	    	  ItemStack full = p.getItemInHand();
	    	  
	    	  
	    	  main.api.getDivSystem().getMarkkit().set("Items." + name, null);
	    	  
	    	  main.api.getDivSystem().getMarkkit().set("Items." + name + ".ID", full.getTypeId());
	    	  main.api.getDivSystem().getMarkkit().set("Items." + name + ".Damage", full.getDurability());
			  
			  main.api.getDivSystem().getMarkkit().set("Items." + name + ".1.buyprice", buyprice);
			  main.api.getDivSystem().getMarkkit().set("Items." + name + ".1.sellprice", sellprice);
			  
			  main.s(p, "Added succesfully!");
		  }
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"back"}, desc = "Back Command", help = "/tp <player> [player]", player = true)
	 public void onBack(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 List<String> locs = dp.getList(DPI.PREVIOUS_LOCATIONS);
		 String[] l = new String[]{};
		 
		 if (locs.size() > 0){
			 if (args.length == 1){
				 if (DivinityUtilsModule.isInteger(args[0])){
					 if (locs.size() > Integer.parseInt(args[0])){
						 l = locs.get((locs.size()-1)-Integer.parseInt(args[0])).split(" ");
						 main.api.event(new DivinityTeleportEvent(p, l[0], l[1], l[2], l[3]));
					 } else {
						 main.s(p, "&c&oYou don't have that many previous locations.");
					 }
				 } else {
					 main.s(p, "invalidNumber");
				 }
			 } else {
				 l = locs.get(locs.size()-1).split(" ");
				 main.api.event(new DivinityTeleportEvent(p, l[0], l[1], l[2], l[3]));
			 }
		 } else {
			 main.s(p, "&c&oYou have no previous locations.");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.mod", aliases = {"v", "vanish"}, desc = "Vanish Command", help = "/v", player = true)
	 public void onVanish(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 String[] hidden = new String[]{"&a&ovisible", "&c&oinvisible"};
		 
		 for (Player player : Bukkit.getOnlinePlayers()){
			 if (dp.getBool(DPI.VANISHED)){
				 player.hidePlayer(p);
			 } else {
				 player.showPlayer(p);
			 }
		 }
		 
		 dp.set(DPI.VANISHED, !dp.getBool(DPI.VANISHED));
		 
		 if (dp.getBool(DPI.VANISHED)){
			 p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));
		 } else {
			 p.removePotionEffect(PotionEffectType.INVISIBILITY);
		 }
		 
		 main.s(p, "You are now " + hidden[dp.getBool(DPI.VANISHED) ? 1 : 0]);
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"heal"}, desc = "Heal Command", help = "/heal", player = true)
	 public void onHeal(Player p, String[] args){
		 p.setHealth(p.getMaxHealth());
		 p.setFoodLevel(20);
		 p.setSaturation(20);
		 main.s(p, "Restored to full health!");
	 }
	 
	 @DivCommand(perm = "wa.rank.national", aliases = {"feed"}, desc = "Feed", help = "/feed", player = true)
	 public void onFeed(Player p, String[] args){
		 p.setFoodLevel(20);
		 p.setSaturation(20);
		 main.s(p, "Yum! Pixel food!");
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"tp", "teleport"}, desc = "Staff Telport Command", help = "/tp <player, alliance, coords> [player]", player = false, min = 1)
	 public void onTP(CommandSender p, String[] args){
		 
		 if (args.length == 1 && p instanceof Player && main.api.doesPartialPlayerExist(args[0])){

			 if (main.api.isOnline(args[0])){
					main.api.event(new DivinityTeleportEvent((Player)p, main.api.getPlayer(args[0]).getLocation()));
			 }

		 } else if (!(p instanceof Player)){
			 
			 main.s(p, "&c&oConsole can not tp to anyone!");
			 
		 } else if (main.api.doesPartialPlayerExist(args[0]) && main.api.doesPartialPlayerExist(args[1])){
			 
			 if (main.api.isOnline(args[0]) && main.api.isOnline(args[1])){
				 main.api.event(new DivinityTeleportEvent(main.api.getPlayer(args[0]), main.api.getPlayer(args[1]).getLocation()));
			 } else {
				 main.s(p, "playerNotFound");
			 }
			 
		 } else if (main.divinity.api.divManager.getMap(DivinityManager.allianceDir).containsKey(args[0])){
			 
			 String[] coords = main.api.getDivAlliance(args[0]).getStr(DAI.CENTER).split(" ");
			 
			 if (args.length > 1 && main.api.doesPartialPlayerExist(args[1])){
				 main.api.event(new DivinityTeleportEvent(main.api.getPlayer(args[0]), "world", coords[0], coords[1], coords[2]));
			 } else {
				 main.api.event(new DivinityTeleportEvent((Player)p, "world", coords[0], coords[1], coords[2]));
			 }
			 
		 } else if (args.length >= 3){
			 
			 if (DivinityUtilsModule.isInteger(args[0]) && DivinityUtilsModule.isInteger(args[1]) && DivinityUtilsModule.isInteger(args[2])){
			
				 if (args.length == 4 && main.api.doesPartialPlayerExist(args[3])){
					 main.api.event(new DivinityTeleportEvent(main.api.getPlayer(args[3]), ((Player)p).getWorld().getName(), args[0], args[1], args[2]));
				 } else {
					 main.api.event(new DivinityTeleportEvent((Player)p, ((Player)p).getWorld().getName(), args[0], args[1], args[2]));
				 }
			 }
			 
	     } else {
			 main.s(p, "playerNotFound");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"tphere"}, desc = "Staff Telport Command", help = "/tphere <player>", player = true, min = 1)
	 public void onTPHere(Player p, String[] args){
		 
		 if (main.api.doesPartialPlayerExist(args[0]) && main.api.isOnline(args[0])){
			 main.api.event(new DivinityTeleportEvent(main.api.getPlayer(args[0]), p.getLocation()));
		 } else {
			 main.s(p, "playerNotFound");
		 }
	 }
	 
	 @DivCommand(perm = "wa.staff.admin", aliases = {"tpall"}, desc = "Staff Telport Command", help = "/tpall", player = true)
	 public void onTPAll(Player sender, String[] args){
		 
		 for (Player p : Bukkit.getOnlinePlayers()){
			 main.api.event(new DivinityTeleportEvent(p, sender.getLocation()));
		 }
		 
		 main.s(sender, "&oMass temporal shift completed.");
	 }
	 
	 @DivCommand(perm = "wa.rank.statesman", aliases = {"tpa"}, desc = "TPA Command", help = "/tpa <player>", player = true, min = 1)
	 public void onTPA(Player sender, String[] args){
		 
		 if (main.api.doesPartialPlayerExist(args[0])){
			 if (main.api.isOnline(args[0])){
				 DivinityPlayer who = main.api.getDivPlayer(args[0]);
				 who.set(DPI.TP_INVITE, sender.getName() + " " + who.name());
				 main.s(main.api.getPlayer(args[0]), sender.getDisplayName() + " &b&ohas requested to TP to you.");
				 main.s(main.api.getPlayer(args[0]), "&oAccept it with &6/tpaccept&b. Decline with &6/tpdeny&b.");
				 main.s(sender, "Sent!");
			 } else {
				 main.s(sender, "playerNotFound");
			 }
		 } else {
			 main.s(sender, "playerNotFound");
		 }
	 }
	 
	 @DivCommand(perm = "wa.rank.emperor", aliases = {"tpahere"}, desc = "TPAHere Command", help = "/tpahere <player>", player = true, min = 1)
	 public void onTPAHere(Player sender, String[] args){
		 
		 if (main.api.doesPartialPlayerExist(args[0])){
			 if (main.api.isOnline(args[0])){
				 DivinityPlayer who = main.api.getDivPlayer(args[0]);
				 who.set(DPI.TP_INVITE, who.name() + " " + sender.getName());
				 main.s(main.api.getPlayer(args[0]), sender.getDisplayName() + " &b&ohas requested for you to TP to them.");
				 main.s(main.api.getPlayer(args[0]), "&oAccept it with &6/tpaccept&b. Decline with &6/tpdeny&b.");
				 main.s(sender, "Sent!");
			 } else {
				 main.s(sender, "playerNotFound");
			 }
		 } else {
			 main.s(sender, "playerNotFound");
		 }
	 }
	 
	 @DivCommand(name = "TPAuth", perm = "wa.rank.dweller", aliases = {"tpaccept", "tpdeny"}, desc = "TP Auth Command", help = "/tpaccept or /tpdeny", player = true)
	 public void onTPAuth(Player sender, String[] args, String cmd){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(sender);
		 
		 if (!dp.getStr(DPI.TP_INVITE).equals("none")){
			 
			 String[] req = dp.getStr(DPI.TP_INVITE).split(" ");
			 
			 if (main.api.isOnline(req[0]) && main.api.isOnline(req[1])){
				 
				 if (cmd.equalsIgnoreCase("tpaccept")){
					 
					 if (!dp.getStr(DPI.TP_INVITE).equals("none")){
						 main.api.event(new DivinityTeleportEvent(main.api.getPlayer(req[0]), main.api.getPlayer(req[1]).getLocation()));
						 main.s(main.api.getPlayer(req[1]), "Accepted.");
					 }
					 
				 } else {
					 main.s(sender, "&c&oDenied. Teleport Cancelled.");
					 main.s(main.api.getPlayer(req[1]), "&c&oDenied. Teleport Cancelled.");
				 }
				 
			 } else {
				 main.s(sender, "&c&oSomeone logged off. Cancelled teleport.");
			 }
			 
		 } else {
			 main.s(sender, "&c&oYou have no requests.");
		 }
		 
		 dp.set(DPI.TP_INVITE, "none");
	 }
	 
	 @DivCommand(perm = "wa.rank.citizen", aliases = {"tpblock"}, desc = "TP Block Command", help = "/tpblock", player = true)
	 public void onTPBlock(Player sender, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(sender);
		 dp.set(DPI.TP_BLOCK, !dp.getBool(DPI.TP_BLOCK));
		 main.s(sender, "&oTeleport block " + (dp.getBool(DPI.TP_BLOCK) + "").replace("true", "&aactive.").replace("false", "&cdisabled."));
	 }
	 
	 @DivCommand(aliases = {"staff"}, desc = "Staff List Command", help = "/staff", player = false)
	 public void onStaff(CommandSender p, String[] args){
		 
		 String interns = "";
		 String mods = "";
		 String mod2s = "";
		 String admins = "";
		 
		 for (DivinityStorage dp : main.divinity.api.getAllPlayers()){
			 List<String> perms = dp.getList(DPI.PERMS);
			 if (perms.contains("wa.staff.admin")){
				 admins = admins + " " + dp.getStr(DPI.DISPLAY_NAME);
			 } else if (perms.contains("wa.staff.mod2")){
				 mod2s = mod2s + " " + dp.getStr(DPI.DISPLAY_NAME);
			 } else if (perms.contains("wa.staff.mod")){
				 mods = mods + " " + dp.getStr(DPI.DISPLAY_NAME);
			 } else if (perms.contains("wa.staff.intern")){
				 interns = interns + " " + dp.getStr(DPI.DISPLAY_NAME);
			 }
		 }
		 
		 main.s(p, "&aIntern:");
		 interns = interns.trim();
		 interns = interns.replaceAll(" ", "&6, &7");
		 main.s(p, interns);
		 
		 main.s(p, "&2Mod:");
		 mods = mods.trim();
		 mods = mods.replaceAll(" ", "&6, &7");
		 main.s(p, mods);
		 
		 main.s(p, "&9Mod+:");
		 mod2s = mod2s.trim();
		 mod2s = mod2s.replaceAll(" ", "&6, &7");
		 main.s(p, mod2s);
		 
		 main.s(p, "&4Admin:");
		 admins = admins.trim();
		 admins = admins.replaceAll(" ", "&6, &7");
		 main.s(p, admins);
		 
		 main.s(p, "&cOwner:");
		 main.s(p, "&7tdstaz69");
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"gm"}, desc = "GameMode Command", help = "/gm <c, s, a> [player]", player = true, min = 1)
	 public void onGM(Player p, String[] args){
		 
		 Player toSet = null;
		 GameMode gm = null;
		 
		 if (args.length == 2 && main.api.perms(p, "wa.staff.admin", false)){
			 for (Player pp : Bukkit.getOnlinePlayers()){
				 if (pp.getName().toLowerCase().contains(args[1].toLowerCase())){
					 toSet = pp;
					 break;
				 }
			 }
		 } else {
			 toSet = p;
		 }
		 
		 switch (args[0]){
		 	case "c": gm = GameMode.CREATIVE; break;
		 	case "a": gm = GameMode.ADVENTURE; break;
		 	case "s": default: gm = GameMode.SURVIVAL; break;
		 }
		 
		 toSet.setGameMode(gm);
		 main.s(toSet, "none", "&o" + gm.toString().toLowerCase() + " mode activated");
		 
		 if (!p.equals(toSet)){
			 main.s(p, "&o" + gm.toString().toLowerCase() + " activated for " + toSet.getDisplayName());
		 }
	 }
	 
	 @DivCommand(aliases = {"more"}, desc = "Give yourself 64 of the item in your hand", help = "/more", perm = "wa.staff.mod2", player = true)
	 public void onMore(Player p, String[] args){

		if (p.getInventory().getItemInHand() != null){
			p.getInventory().getItemInHand().setAmount(64);
		}
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"fly"}, desc = "Fly Command", help = "/fly [player]", player = true)
	 public void onFly(Player p, String[] args){
		 
		 Player toSet = null;
		 
		 if (args.length == 1 && main.api.perms(p, "wa.staff.admin", false)){
			 for (Player pp : Bukkit.getOnlinePlayers()){
				 if (pp.getName().toLowerCase().contains(args[0].toLowerCase())){
					 toSet = pp;
					 break;
				 }
			 }
		 } else {
			 toSet = p;
		 }
		 
		 if (!toSet.isFlying()){
			 toSet.setAllowFlight(true);
		 }
		 
		 toSet.setFlying(!toSet.isFlying());
		 main.s(toSet, "none", ("&ofly mode " + toSet.isFlying()).replace("true", "&aactivated").replace("false", "&cdeactivated"));
		 
		 if (!p.equals(toSet)){
			 main.s(p, "none", ("&ofly mode " + toSet.isFlying()).replace("true", "&aactivated").replace("false", "&cdeactivated") + " &b&ofor " + toSet.getDisplayName());
		 }
	 }
	 
	@DivCommand(perm = "wa.staff.intern", aliases = {"o"}, desc = "Staff chat command", help = "/o <message>", player = true, min = 1)
	public void onO(Player p, String[] args){
		ElyChannel.STAFF.send(p.getDisplayName(), DivinityUtilsModule.createString(args, 0), main.api);
	}
	 
	@DivCommand(aliases = {"skull"}, min = 1, max = 1, player = true, perm = "wa.staff.intern")
	public void onSkull(Player p, String[] args){
			
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		sm.setOwner(args[0]);
		is.setItemMeta(sm);
		p.setItemInHand(is);
	}
		
	@DivCommand(aliases = {"top"}, desc = "Teleport to the highest block above", help = "/top", max = 0, perm = "wa.staff.mod2", player = true)
	public void onTop(Player p, String[] args){
		
		if (p.getWorld().getHighestBlockYAt(p.getLocation()) != -1){
			p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getWorld().getHighestBlockYAt(p.getLocation())+1, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));	
		} else {
			main.s(p, "none", "No location found!");
		}
	}
	
	@EventHandler
	public void onMOTD(ServerListPingEvent e){
		
		String motd = main.api.getDivSystem().getStr(DPI.MOTD);
		
		if (!motd.equals("none")){
			e.setMotd(main.AS(motd));
		}
	}
	
	@SuppressWarnings("deprecation")
	@DivCommand(aliases = {"sm"}, desc = "Spawn Mob", help = "/sm <type> <health> <nameTag> <armorType> <weapon> <potionEffect> <location> <passenger(s)> <amount>", max = 9, perm = "wa.staff.mod2", player = true)
	public void onSM(Player p, String[] args){

		String armors = "diamond iron chain gold leather";
		int x = 0;
				
		if (args.length < 9 && args.length != 2){
			main.s(p, "/sm <type> <health> <nameTag> <armorType> <weapon> <potionEffect> <location> <passenger(s)> <amount>");
			main.s(p, "&6Example: &7/sm zombie 20 Grumpy_Guy diamond diamond_sword damage Hugh_Jasses skeleton,zombie 1");
			main.s(p, "&dUse a '_' for spaces. Location can be 'aim' or a player.");
			main.s(p, "&dFor more passengers, seperate different mobs with a ','. Use # if you don't want a passenger.");
			main.s(p, "&dYou can use /sm <type> ## to indicate no extra features. Example: /sm zombie ##");
		}
			
		for (EntityType e : EntityType.values()){
			if (e.toString().toLowerCase().equals(args[0].toLowerCase())){
				if (DivinityUtilsModule.isInteger(args[1])){
					if (armors.contains(args[3].toLowerCase())){
						int y = 0;
						for (Material m : Material.values()){
							if (m.toString().toLowerCase().equals(args[4].toLowerCase())){
								int z = 0;
								for (PotionEffectType pe : PotionEffectType.values()){
									if (String.valueOf(pe).toString().toLowerCase().contains(args[5].toLowerCase())){
										if (main.api.getPlayer(args[6]) != null || args[6].equals("aim")){
											if (DivinityUtilsModule.isInteger(args[8])){
												if (args[7].contains(",") || args[7].equals("#")){
													List<String> passengers = new ArrayList<String>();
													if (args[7].contains(",")){
														passengers = Arrays.asList(args[7].split(","));
													}
													List<EntityType> goodPassengers = new ArrayList<EntityType>();
													for (String passenger : passengers){
														int a = 0;
														for (EntityType ee : EntityType.values()){
															if (passenger.toLowerCase().equals(ee.toString().toLowerCase())){
																goodPassengers.add(ee);
															} else {
																a++;
																if (a >= EntityType.values().length){
																	main.s(p, "The passenger " + passenger + " is not a valid entity.");
																	break;
																}
															}
														}
													}
													formMob(p, e, Integer.parseInt(args[1]), args[2], args[3], m, pe, args[6], goodPassengers, Integer.parseInt(args[8]));
													break;
												} else {
													int b = 0;
													for (EntityType ee : EntityType.values()){
														if (args[7].toLowerCase().equals(ee.toString().toLowerCase())){
															List<EntityType> gp = Arrays.asList(ee);
															formMob(p, e, Integer.parseInt(args[1]), args[2], args[3], m, pe, args[6], gp, Integer.parseInt(args[8]));
															break;
														} else {
															b++;
															if (b >= EntityType.values().length){
																main.s(p, "The passenger " + args[7] + " is not a valid entity.");
																break;
															}
														}
													}
												}
											} else {
												main.s(p, "You must use a number for the amount!");
												break;
											}
										} else {
											main.s(p, "You've entered an invalid player since and you didn't say 'aim'.");
											break;
										}
									} else {
										z++;
										if (z >= PotionEffectType.values().length){
											main.s(p, "The potion effect " + args[5] + " was not found.");
											break;
										}
									}
								}
							} else {
								y++;
								if (y >= Material.values().length){
									main.s(p, "The material " + args[4] + " was not found.");
									break;
								}
							}
						}
					} else {
						main.s(p, "Choose from " + armors.replace(" ", ", "));
						break;
					}
				} else if (args[1].equals("##")){
					p.getWorld().spawnEntity(p.getTargetBlock(null, 20).getLocation(), e);
					break;
				} else {
					main.s(p, "Health must be a number!");
					break;
				}
			} else if (args[0].equals("#")){
				main.s(p, "Why the hell did you use this command then? GAH");
			} else {
				x++;
				if (x >= EntityType.values().length){
					main.s(p, "The entity " + args[0] + " was not found.");
					break;
				}
			}
		}		
	}
	
	@SuppressWarnings("deprecation")
	@DivCommand(aliases = {"i"}, desc = "Give an item to yourself", help = "/i <item>", max = 1, perm = "wa.staff.mod2", player = true)
	public void onI(Player p, String[] args){
		
		boolean found = false;

		if (args.length == 0 || p.getInventory().firstEmpty() == -1){
			main.s(p, "/i <item> (must have room!)");
		} else {
			for (Material m : Material.values()){
				if (m.name().toString().toLowerCase().equals(args[0].toLowerCase())){
					p.getInventory().addItem(new ItemStack(m, 64));
					found = !found;
					break;
				} else if (DivinityUtilsModule.isInteger(args[0]) && m.getId() == Integer.parseInt(args[0])){
					p.getInventory().addItem(new ItemStack(m, 64));
					found = !found;
					break;
				}
			}
			if (!found){
				for (Material m : Material.values()){
					if (m.name().toString().toLowerCase().contains(args[0].toLowerCase())){
						p.getInventory().addItem(new ItemStack(m, 64));
						break;
					}
				}
			}
		}
	}
	
	@DivCommand(perm = "wa.rank.townsman", aliases = {"ci"}, desc = "Clear Inventory (or restore inventory. Results may vary. TM)", help = "/ci [confirm]", player = true)
	public void onCI(Player p, String[] args){
		
		if (args.length == 0){
			main.s(p, "&cType /ci confirm to clear your inventory. &4This can't be reversed. We will not refund you.");
		} else {
			p.getInventory().clear();
			main.s(p, "&oInventory inceneration activated.");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void formMob(Player p, EntityType e, int health, String nameTag, String armorType, Material m, PotionEffectType pe, String location, List<EntityType> goodPassengers, int amount) {
		
		List<LivingEntity> les = new ArrayList<LivingEntity>();
		List<LivingEntity> passengers = new ArrayList<LivingEntity>();
		int z = 1;
		
		if (location.equals("aim")){
			for (int x = 0; x < amount; x++){
				LivingEntity le = (LivingEntity) p.getWorld().spawnEntity(p.getTargetBlock(null, 20).getLocation(), e);
				les.add(le);
			}
		} else {
			for (int x = 0; x < amount; x++){
				LivingEntity le = (LivingEntity) p.getWorld().spawnEntity(Bukkit.getPlayer(location).getLocation(), e);
				les.add(le);
			}
		}

		for (LivingEntity le : les){
			
			for (EntityType et : goodPassengers){
				LivingEntity la = (LivingEntity) p.getWorld().spawnEntity(p.getTargetBlock(null, 20).getLocation(), et);
				passengers.add(la);
			}

			for (LivingEntity la : passengers){
				if (z < passengers.size()){
					la.setPassenger(passengers.get(z));
				}
				z++;
			}		
			
			if (passengers.size() > 0){
				le.setPassenger(passengers.get(0));
			}
			
			le.addPotionEffect(new PotionEffect(pe, 99999, 1));
			le.setCustomName(main.AS(nameTag.replaceAll("_", " ")));
			le.setCustomNameVisible(true);
			le.getEquipment().setItemInHand(new ItemStack(m, 1));
			le.setMaxHealth(health);
			le.setHealth(health);
			passengers = new ArrayList<LivingEntity>();
			
			switch(armorType){
				case "diamond":
					le.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
					le.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
					le.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
					le.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
				break;
				case "iron":
					le.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
					le.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
					le.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
					le.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
				break;
				case "leather":
					le.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
					le.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					le.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
					le.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
				break;
				case "chain":
					le.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
					le.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
					le.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
					le.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
				break;
				case "gold":
					le.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
					le.getEquipment().setHelmet(new ItemStack(Material.GOLD_HELMET));
					le.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
					le.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
				break;		
			}
		}
	}
}