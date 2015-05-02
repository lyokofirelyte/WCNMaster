package com.github.lyokofirelyte.Elysian.Commands;

import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import lombok.Getter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Gui.GuiRoot;
import com.github.lyokofirelyte.Empyreal.JSONMap;
import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityAlliance;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityRegion;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityRing;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinitySystem;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.Elysian.ElyChannel;
import com.github.lyokofirelyte.Empyreal.Gui.DivInvManager;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.Direction;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;
import com.github.lyokofirelyte.Empyreal.Utils.WebsiteManager;
import com.google.common.io.Files;

public class ElyCommand implements AutoRegister<ElyCommand> {

	private Elysian main;
	
	@Getter
	private ElyCommand type = this;
	
	public ElyCommand(Elysian i){
		main = i;
	}
	
	String[] divLogo = new String[]{
		"&b. . . . .&f(  &3D  i  v  i  n  i  t  y &f  )&b. . . . .",
		"",
		"&7&oAn API by Hugs, for Elysian & Connecting Plugins",	
		"&6&oRegistered Modules: "
	};
	
	String[] elyLogo = new String[]{
		"&b. . . . .&f(  &3E  l  y  s  i  a  n &f  )&b. . . . .",	
		"",
		"&7&oA MC Operating System by Hugs",
		"&6&o/ely help or /ely help full"
	};
	
	Map<String, String[]> help = new THashMap<String, String[]>();

	private void fillMap(List<String> perms, boolean all){
		help.clear();
		for (Object o : main.api.commandMap.values()){
			for (Method m : o.getClass().getMethods()){
				if (m.getAnnotation(GameCommand.class) != null){
					GameCommand anno = m.getAnnotation(GameCommand.class);
					if (perms.contains(anno.perm()) || all){
						String name = anno.aliases()[0];
						for (int i = 1; i < anno.aliases().length; i++){
							name = anno.aliases().length > i ? name + "&7, &3" + anno.aliases()[i] : name;
						}
						String[] perm = anno.perm().split("\\.");
						String p = perm[perm.length-1];
						help.put("/" + name, s(p.substring(0, 1).toUpperCase() + p.substring(1) + "+", anno.desc() + "\n&6" + anno.help()));
					}
				}
			}
		}
	}
	
	private String[] s(String arg, String arg1){
		return new String[]{arg, arg1};
	}
	
	private boolean isRoom(ItemStack i, boolean add){
		
		for (int x = 0; x < 5; x++){
			if (main.closets.get(x).getInv().firstEmpty() != -1){
				if (add){
					main.closets.get(x).getInv().addItem(i);
				}
				return true;
			}
		}
		
		return false;
	}
	
	@GameCommand(aliases = {"transfer2"}, perm = "wa.staff.admin", desc = "Transfer", help = "/transfer", player = true)
	public void onTransfer2(Player p, String[] args){
		
		for (File f : new File("./plugins/Divinity/regions").listFiles()){
			if (f.getName().endsWith(".yml")){
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
				DivinityRegion dp = new DivinityRegion(f.getName().replace(".yml", ""), main.api);
				JSONMap json = new JSONMap<String, Object>();
				for (String key : yaml.getKeys(false)){
					json.set(key, yaml.get(key));
				}
				dp.fill(json);
				dp.save();
				main.api.getOnlineModules().put("REGION_" + f.getName().replace(".yml", ""), dp);
				main.s(p, "Saved " + f.getName() + " @ " + dp.size() + " objects.");
			}
		}
		
		for (File f : new File("./plugins/Divinity/alliances").listFiles()){
			if (f.getName().endsWith(".yml")){
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
				DivinityAlliance dp = new DivinityAlliance(f.getName().replace(".yml", ""), main.api);
				JSONMap json = new JSONMap<String, Object>();
				for (String key : yaml.getKeys(false)){
					json.set(key, yaml.get(key));
				}
				dp.fill(json);
				dp.save();
				main.api.getOnlineModules().put("ALLIANCE_" + f.getName().replace(".yml", "").toLowerCase(), dp);
				main.s(p, "Saved " + f.getName() + " @ " + dp.size() + " objects.");
			}
		}
		
		for (File f : new File("./plugins/Divinity/rings").listFiles()){
			if (f.getName().endsWith(".yml")){
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
				DivinityRing dp = new DivinityRing(f.getName().replace(".yml", ""), main.api);
				JSONMap json = new JSONMap<String, Object>();
				for (String key : yaml.getKeys(false)){
					json.set(key, yaml.get(key));
				}
				dp.fill(json);
				dp.save();
				main.api.getOnlineModules().put("RING_" + f.getName().replace(".yml", ""), dp);
				main.s(p, "Saved " + f.getName() + " @ " + dp.size() + " objects.");
			}
		}
		
		File f = new File("./plugins/Divinity/system/system.yml");
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
		DivinitySystem dp = new DivinitySystem(main.api, f.getName().replace(".yml", ""));
		JSONMap json = new JSONMap<String, Object>();
		for (String key : yaml.getKeys(false)){
			json.set(key, yaml.get(key));
		}
		dp.fill(json);
		dp.save();
		main.api.getOnlineModules().put("SYSTEM", dp);
		main.setDivSystem(dp);
		main.s(p, "Saved " + f.getName() + " @ " + dp.size() + " objects.");
	}
	
	@GameCommand(aliases = {"dank"}, perm = "wa.member", desc = "Dank Command", help = "/dank", player = true)
	public void onDank(Player p, String[] args){
		main.api.getDivPlayer(p).set(DPI.DANK, !main.api.getDivPlayer(p).getBool(DPI.DANK));
		main.s(p, "Updated dank mode.");
	}
	
	@GameCommand(aliases = {"tutorial"}, perm = "wa.guest", desc = "Basic tutorial books for Worlds Apart!", help = "/tutorial", player = true)
	public void onTutorial(Player p, String[] args){
		
		if(args.length == 0){
			for(String s : new String[]{
					"membership / member",
					"staff"
			}){
				main.s(p, "/tutorial " + s);
			}
			return;
		}
		
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta) book.getItemMeta();
		bm.setAuthor("Server");
		
		switch(args[0].toLowerCase()){
		
			case "membership": case "member":
				bm.setTitle(args[0].toUpperCase());
				bm.setPages(textAsPages("Welcome to the Worlds Collide Network! Things can be a bit confusing when you're a new player, so we have some "
						+ "tutorial books to help you along. The first thing you're going to want to do is apply for membership! This is "
						+ "super easy and wont take much time. Start by typing '/member' in chat, this will give you a link to our "
						+ "forums. The membership application is super simple, all you have to do is answer a few questions by posting on our forums. If you have any questions feel free to ask other players or staff for help."));
				
				book.setItemMeta(bm);
				p.getInventory().addItem(book);
				main.s(p, "Tutorial book: &6" + args[0] + "&b added!");
				break;
		
			case "staff":
				bm.setTitle(args[0].toUpperCase());
				bm.setPages(textAsPages("You can type '/staff' in game to see which members are staff. Always feel free to ask them questions, they "
						+ "will do the best they can to help you out! You will also see a colored 'WCN' in front of the names of staff "
						+ "when they are talking."
						+ " If you hover over 'WCN' it will give you a brief description of what that staff member does."));
				
				book.setItemMeta(bm);
				p.getInventory().addItem(book);
				main.s(p, "Tutorial book: &6" + args[0] + "&b added!");
				break;
		}
		
		
		
	}
		
	public List<String> textAsPages(String text){
		List<String> pages = new ArrayList<String>();
		StringBuilder page = new StringBuilder();
		int charCount = 0;
		
		for(String s : text.split(" ")){
			if(charCount + s.toCharArray().length > 170){
				charCount = 0;
				page.append(s + " ");
				pages.add(page.toString());
				page = new StringBuilder();
			}else{
				charCount = charCount + s.toCharArray().length;
				page.append(s + " ");
			}
		}

		if(charCount != 0){
			pages.add(page.toString());
		}
		
		return pages;
		
	}
		
	@GameCommand(aliases = {"rolldice", "rd"}, perm = "wa.staff.intern", desc = "Get a random result from a list!", help = "/rolldice", player = true)
	public void onRollDice(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		List<String> list = new ArrayList(main.api.getDivSystem().getList(DPI.ROLLDICE_LIST));

		if(args.length == 0){
			for(String s : new String[]{
					"roll (Gets the random result)",
					"add <#> (Adds it to the rolldice list)",
					"remove <#> (Removes it from the rolldice list)",
					"list (Shows you the list of current values)"
			}){
				dp.s("/rolldice " + s);
			}
			return;
		}
		
		switch(args[0].toLowerCase()){
		
		case "roll":
			dp.s("The value &6" + list.get(new Random().nextInt(list.size())) + " &bwas rolled!");
			
			break;
		
		
		case "add":
			if(args.length != 2) return;
			
			if(!list.contains(args[1])){
				list.add(args[1]);
				main.api.getDivSystem().set(DPI.ROLLDICE_LIST, list);
				dp.s("Added!");
			}else{
				dp.s("Value already in the list!");
			}
			
			break;
		
		
		case "remove":
			if(args.length != 2) return;
			
			if(list.contains(args[1])){
				list.remove(args[1]);
				main.api.getDivSystem().set(DPI.ROLLDICE_LIST, list);
				dp.s("Removed!");
			}else{
				dp.s("Value not in the list!");
			}
			
			break;
	
		case "list":
			StringBuilder result = new StringBuilder();
			
			for(String s : list){
				result.append("&6" + s + "&b, ");
			}
			
			dp.s(result.toString());
				
			break;
		}
	}
	
	@GameCommand(aliases = {"website"}, desc = "Obtain a registration code for the website", help = "/website", player = true)
	public void onWebsite(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		boolean result = true;
		
		String upper = new Random().nextInt(2) == 1 ? p.getName().substring(0, 1) : p.getName().substring(0, 1).toUpperCase();
		String enc = DivinityUtilsModule.encrypt(upper + p.getUniqueId().toString().substring(3, 8) + new Random().nextInt(10), "MD5");
		
		if (enc.length() > 6){
			enc = enc.substring(0, 6);
		}
		
		dp.set(DPI.WEBSITE_CODE, enc);
		JSONObject sendMap = new JSONObject();
		sendMap.put("uuid", p.getUniqueId().toString());
		sendMap.put("id", enc);
		sendMap.put("staff", dp.getList(DPI.PERMS).contains("wa.staff.intern"));
		sendMap.put("check", main.api.getDivSystem().getStr(DPI.WEB_CHECK));
		result = (boolean) main.api.getInstance(WebsiteManager.class).getType().sendPost("/api/register_code/", sendMap).get("success");
		
		if (result){
			JSONChatMessage msg = new JSONChatMessage("");
			JSONChatExtra extra = new JSONChatExtra(main.AS("&bSuccess & things! &6Click here &bto complete your account."));
			extra.setClickEvent(JSONChatClickEventType.OPEN_URL, "http://worldscolli.de/register?code=" + enc);
			extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&6Click here! :D"));
			msg.addExtra(extra);
			msg.sendToPlayer(p);
		} else {
			main.s(p, "Error contacting website... so... :3");
		}
	}
	
	@GameCommand(aliases = {"sell"}, desc = "Add an item to the trading house in /root!", help = "/sell <price>", player = true, min = 1)
	public void onSell(Player p, String[] args){
		
		int amt = 0;
		
		for (int i = 0; i < 5; i++){
			if (main.closets.get(i).getInv().getContents().length > 0){
				for (ItemStack item : main.closets.get(i).getInv().getContents()){
					if (item != null && item.getItemMeta().getLore().contains(p.getName())){
						amt++;
						if (amt >= 3){
							main.s(p, "&c&oYou can only sell 3 items. Try opening up a player-owned shop?");
							return;
						}
					}
				}
			}
		}
		
		if (isRoom(null, false)){
			if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)){
				if (DivinityUtilsModule.isInteger(args[0])){
					ItemStack i = p.getItemInHand();
					ItemMeta im = i.getItemMeta();
					List<String> lore = Arrays.asList(main.AS("&6" + args[0]), main.AS(p.getName()));
					im.setLore(lore);
					i.setItemMeta(im);
					isRoom(i, true);
					main.api.getDivSystem().getStack(DPI.CLOSET_ITEMS).add(i);
					p.getInventory().remove(i);
					((DivInvManager)main.api.getInstance(DivInvManager.class)).displayGui(p, main.closets.get(0));
				} else {
					main.s(p, "&c&oThat is not a valid price.");
				}
			} else {
				main.s(p, "&c&oYou must hold an item in your hand!");
			}
		} else {
			main.s(p, "&c&oThe trading hub is full.");
		}
	}
	
	@GameCommand(perm = "wa.staff.admin", aliases = {"effects"}, desc = "Effects Command", help = "/effects help", player = true)
	public void onEffects(final Player p, String[] args){
		
		DivinitySystem ds = main.api.getDivSystem();
		
		if (args.length == 0){
			
			for (String s : new String[]{
				"/effects add <name> <effName> <OSX> <OSY> <OSZ> <speed> <amount> <range> <cycleTime> [x,y,z]",
				"/effects rem <name>",
				"/effects stop <name>",
				"/effects list",
				"/effects playonce <effName> <OSX> <OSY> <OSZ> <speed> <amount> <range> [x,y,z]",
				"/effects effectlist",
				"/effects locktoplayer <player> <name> <effName> <OSX> <OSY> <OSZ> <speed> <amount> <range> <cycleTime>",
				"/effects draw <word> <effName> <direction> <cycleTime>",
				"/effects clearplayer <name>"
			}){
				main.s(p, s);
			}
			
		} else {
			
			switch (args[0]){
				
				case "draw":
					
					try {
						ds.addLetterEffect(args[1], ParticleEffect.fromName(args[2]), DivinityUtilsModule.getCardinalMove(p), Direction.getDirection(args[3]), Long.parseLong(args[4]));
						main.s(p, "Added!");
					} catch (Exception e){
						main.s(p, "Invalid inputs!");
					}
					
				break;
			
				case "locktoplayer":
					
					if (main.api.doesPartialPlayerExist(args[1]) && main.api.isOnline(args[1])){
						
						try {
							DivinityPlayer dp = main.api.getDivPlayer(args[1]);
							dp.lockEffect(args[2], ParticleEffect.fromName(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]),
								Integer.parseInt(args[7]), Integer.parseInt(args[8]), Integer.parseInt(args[9]), Long.parseLong(args[10]));
							main.s(p, "Added!");
						} catch (Exception e){
							main.s(p, "Invalid args!");
						}
						
					} else {
						main.s(p, "Player not found.");
					}
					
				break;
				
				case "clearplayer":
					
					if (main.api.isOnline(args[1])){
						main.api.getDivPlayer(args[1]).clearEffects();
					}
					
				break;
			
				case "playonce":
					
					try {
						ds.playEffect(ParticleEffect.fromName(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), 
							Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]),
							args.length == 9 ? new Location(p.getWorld(), Integer.parseInt(args[10].split(",")[0]), 
								Integer.parseInt(args[8].split(",")[1]),
								Integer.parseInt(args[8].split(",")[2])) : 
								p.getLocation(),
							Integer.parseInt(args[7]));
					} catch (Exception e){
						main.s(p, "&c&oInvalid inputs!");
					}
					
				break;
				
				case "add":
					
					try {
						ds.addEffect(args[1], ParticleEffect.fromName(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), 
							Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7]),
							args.length == 11 ? new Location(p.getWorld(), Integer.parseInt(args[10].split(",")[0]), 
								Integer.parseInt(args[10].split(",")[1]),
								Integer.parseInt(args[10].split(",")[2])) : 
								p.getLocation(),
							Integer.parseInt(args[8]), Long.parseLong(args[9]));
						main.s(p, "Added! :D");
					} catch (Exception e){
						main.s(p, "&c&oInvalid inputs!");
					}
					
				break;
				
				case "rem":
					
					if (args.length == 2 && (ds.containsKey("Effects." + args[1])) || (ds.containsKey("LetterEffects." + args[1]))){
						ds.remEffect(args[1]);
						main.s(p, "Removed.");
					} else {
						main.s(p, "Not found!");
					}
					
				break;
				
				case "stop":
					
					if (args.length == 2){
						ds.cancelEffect(args[1]);
						main.s(p, "Cancelled!");
					}
					
				break;
				
				case "list":
					
					for (String s : ds.getList("Effects")){
						main.s(p, s);
					}
					
					for (String s : ds.getList("LetterEffects")){
						main.s(p, s);
					}
					
				break;
				
				case "effectlist":
					
					for (ParticleEffect e : ParticleEffect.values()){
						main.s(p, e.toString());
					}
					
				break;
			}
		}
	}
	
	@GameCommand(perm = "wa.rank.dweller", aliases = {"notepad"}, desc = "Notepad Management System", help = "/notepad", player = true)
	public void onNotepad(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		JSONChatMessage msg = new JSONChatMessage("");
		JSONChatExtra addButton = new JSONChatExtra(main.AS("&bElysian Note System &a{+}"));
		addButton.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/notepad #add");
		addButton.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&aAdd a new note!"));
		msg.addExtra(addButton);
		
		int counter = 0;
		
		if (args.length == 0){
			
			p.sendMessage("");
			main.s(p, msg);
			
			for (String message : dp.getList(DPI.NOTEPAD)){
				JSONChatMessage m = new JSONChatMessage(main.AS("&7" + main.numerals.get(counter) + "&f: &3" + message + " "));
				JSONChatExtra editButton = new JSONChatExtra(main.AS("&7[&e*&7] "));
				JSONChatExtra deleteButton = new JSONChatExtra(main.AS("&7[&c-&7]"));
				editButton.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&eEdit this note."));
				editButton.setClickEvent(JSONChatClickEventType.SUGGEST_COMMAND, "/notepad #edit <" + counter + "> " + message);
				deleteButton.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&cDelete this note."));
				deleteButton.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/notepad #delete " + message);
				m.addExtra(editButton);
				m.addExtra(deleteButton);
				main.s(p, m);
				counter++;
				if (counter == 101){
					break;
				}
			}
			
			p.sendMessage("");
			
		} else {
			
			switch (args[0]){
			
				case "#add":
					
					dp.s("Please type in a new note to add.");
					dp.s("%c will be replaced by your current coords.");
					dp.getList(DPI.NOTEPAD_SETTING).add("add");
					
				break;
				
				case "#edit":
					
					String message = "";
					
					if (args.length >= 2){
						boolean set = args[1].contains("<");
						int num = set ? Integer.parseInt(args[1].replace("<", "").replace(">", "")) : 0;
						num = num < dp.getList(DPI.NOTEPAD).size() ? num : dp.getList(DPI.NOTEPAD).size()-1;
						Location l = p.getLocation();
						if (set){
							message = DivinityUtilsModule.createString(args, 2);
							dp.getList(DPI.NOTEPAD).set(num, message.replace("%c", l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ()));
						} else {
							message = DivinityUtilsModule.createString(args, 1);
							dp.getList(DPI.NOTEPAD).add(message.replace("%c", l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ()));
						}
						onNotepad(p, new String[]{});
					}
					
				break;
				
				case "#delete":
					
					dp.getList(DPI.NOTEPAD).remove(DivinityUtilsModule.createString(args, 1));
					onNotepad(p, new String[]{});
					
				break;
			}
		}
	}
	
	@GameCommand(aliases = {"root", "menu"}, desc = "Open the main menu", help = "/root", player = true)
	public void onRoot(Player p, String[] args){
		((DivInvManager) main.api.getInstance(DivInvManager.class)).displayGui(p, new GuiRoot(main));
	}
	
	 @GameCommand(perm = "wa.member", aliases = {"poll"}, desc = "Polls!", help = "/poll help", player = false, min = 1)
	 public void onPoll(CommandSender p, String[] args){
		 
		 /*DivinitySystem system = main.api.getSystem();
		 
		 switch (args[0]){
		 
			 case "help":
				 
				 for (String s : new String[]{
					"/poll vote [yes/no]",
					"/poll view",
					"/poll set <what to vote for>"
				 }){
					main.s(p, s);
				 }
				 
			 break;
			 
			 case "yes": case "no":
				 
				 for (String s : system.getList(DPI.VOTED)){
					 if (s.contains(p.getName())){
						 main.s(p, "You already voted.");
						 return;
					 }
				 }
				 
				 system.getList(DPI.VOTED).add(args[0].replace("yes", "A").replace("no", "B") + " " + p.getName());
				 main.s(p, "Thanks for voting.");
				 
		     break;
		     
			 case "view":
				 
				 String msg = "";
				 List<String> votes = new ArrayList(system.getList(DPI.VOTED));
				 Collections.sort(votes);
				 int yes = 0;
				 
				 for (String vote : votes){
					 msg = msg + " " + vote.replace("A ", "&a").replace("B ", "&c");
					 yes = vote.startsWith("A") ? yes++ : yes;
				 }
				 
				 main.s(p, msg);
				 p.sendMessage("");
				 main.s(p, "&a" + Math.round((yes/votes.size())*100) + "% yes, &c" + Math.round(((votes.size()-yes)/votes.size())*100) + "% no.");
				 
			 break;
			 
			 case "set":
				 
				 if (main.perms(p, "wa.staff.admin")){
					 system.set(DPI.VOTE_MESSAGE, DivinityUtlsModule.createString(args, 0));
					 main.s(p, "Set!");
				 }
				 
			 break;
		 }*/
		 
		 
		 if(args[0].equalsIgnoreCase("help")){
		 for (String s : new String[]{
					"/poll vote [yes/no]",
					"/poll view",
					"/poll set <what to vote for>"
				}){
					main.s(p, s);
				}
		 }else if(args[0].equalsIgnoreCase("vote") && args.length == 2){
			 if(args[1].equalsIgnoreCase("yes")){
				 if(main.api.getDivSystem().getList(DPI.YES_VOTE).contains(p.getName())){
					 main.s(p, "You already voted yes!");
					 return;
				 }else if(main.api.getDivSystem().getList(DPI.NO_VOTE).contains(p.getName())){
					 main.api.getDivSystem().getList(DPI.NO_VOTE).remove(p.getName());
					 main.api.getDivSystem().getList(DPI.YES_VOTE).add(p.getName());
				 }else{
					 main.api.getDivSystem().getList(DPI.YES_VOTE).add(p.getName());
				 }
				 main.s(p, "Thanks for your vote, you voted yes.");
			 }else if(args[1].equalsIgnoreCase("no")){
				 if(main.api.getDivSystem().getList(DPI.NO_VOTE).contains(p.getName())){
					 main.s(p, "You already voted no!");
					 return;
				 }else if(main.api.getDivSystem().getList(DPI.YES_VOTE).contains(p.getName())){
					 main.api.getDivSystem().getList(DPI.YES_VOTE).remove(p.getName());
					 main.api.getDivSystem().getList(DPI.NO_VOTE).add(p.getName());
				 }else{
					 main.api.getDivSystem().getList(DPI.NO_VOTE).add(p.getName());
				 }
				 main.s(p, "Thanks for your vote, you voted no.");
			 }else{
				 main.s(p, "/poll help");
			 }
		 }else if(args[0].equalsIgnoreCase("set")){
			 if(main.api.perms(p, "wa.staff.admin", false)){
				 StringBuilder message = new StringBuilder();
				 for(int i = 0; i < args.length; i++){
					 if(i >=1){
						 message.append(args[i] + " ");
					 }
				 }
				 main.api.getDivSystem().set(DPI.VOTE_MESSAGE, message.toString());
				 main.api.getDivSystem().set(DPI.YES_VOTE, null);
				 main.api.getDivSystem().set(DPI.NO_VOTE, null);
				 main.s(p, "Created vote!");
			 }
		 }else if(args[0].equalsIgnoreCase("view")){
			 main.s(p, "Current voting for: " + main.api.getDivSystem().getStr(DPI.VOTE_MESSAGE));
			 main.s(p, "Players who voted yes: ");
			 for(String s : main.api.getDivSystem().getList(DPI.YES_VOTE)){
				 main.s(p, s);
			 }
			 main.s(p, "Players who voted no: ");
			 for(String s : main.api.getDivSystem().getList(DPI.NO_VOTE)){
				 main.s(p, s);
			 }
			 if(main.api.getDivSystem().getList(DPI.YES_VOTE).size() + main.api.getDivSystem().getList(DPI.NO_VOTE).size() != 0){
				 main.s(p, "Yes: " + main.api.getDivSystem().getList(DPI.YES_VOTE).size() / (main.api.getDivSystem().getList(DPI.YES_VOTE).size() + main.api.getDivSystem().getList(DPI.NO_VOTE).size()) * 100 + "%");
				 main.s(p, "No: " + main.api.getDivSystem().getList(DPI.NO_VOTE).size() / (main.api.getDivSystem().getList(DPI.YES_VOTE).size() + main.api.getDivSystem().getList(DPI.NO_VOTE).size()) * 100 + "%");
			 }
		 }
	 }
	 
		@GameCommand(perm = "wa.rank.regional", aliases = {"rainoff"}, desc = "Turn off that rain!", help = "/rainoff", player = true)
		public void onRainoff(Player p, String[] args){
			DivinityPlayer player = main.api.getDivPlayer(p.getName());
			World w = p.getWorld();
			if(!w.hasStorm()){
				player.s("There is no rain!");
				return;
			}
			if(player.getLong(DPI.RAIN_TOGGLE) == 0 || player.getLong(DPI.RAIN_TOGGLE) <= System.currentTimeMillis() - 3 * 60 * 60 * 1000){
				player.set(DPI.RAIN_TOGGLE, System.currentTimeMillis());
				w.setStorm(false);
				Utils.bc(player.getStr(DPI.DISPLAY_NAME) + " &bhas turned off the rain!");
			}else{
				player.s("You have to wait " + ((player.getLong(DPI.RAIN_TOGGLE) + 1000 * 60 * 60 * 3) - System.currentTimeMillis()) / 1000 / 60 + " more minutes");
			}
			
		}
		
		@GameCommand(perm = "wa.staff.admin", aliases = {"testdrax"}, desc = "TestDrax", help = "/testdrax", player = true)
		public void onTestDrax(Player p, String[] args){
			p.getInventory().addItem(DivInvManager.createItem(main.AS("&5&o))( &f&odRaX &5&o)(("), new String[] {"&6&o(HAOS DEVICE", "&a&o5000/5000"}, Enchantment.DURABILITY, 10, Material.ARROW, 1));
			p.getInventory().addItem(DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 14));
			p.getInventory().addItem(DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 13));
			p.getInventory().addItem(DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 0));
			p.getInventory().addItem(DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 12));
			p.getInventory().addItem(DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 5));
		}
		
		@GameCommand(perm = "wa.rank.districtman", aliases = {"near"}, desc = "See nearby players!", help = "/near", player = true, min = 0)
		public void onNear(Player p, String[] args){
			StringBuilder players = new StringBuilder();
			int count = 0;
			for(Entity e : p.getNearbyEntities(100, 100, 100)){
				if(e instanceof Player){
					Player found = (Player) e;
					DivinityPlayer pl = main.api.getDivPlayer(found.getName());
					if(found.getName() != p.getName()){
					count = count + 1;
					players.append(pl.getStr(DPI.DISPLAY_NAME) + ", &b");
					}
				}
			}
			
			if(count == 0){
				main.s(p, "No nearby players found");
			}else{
				main.s(p, count + " player(s) found: " + players);
			}
			
		}
		
	@GameCommand(aliases = {"setprice"}, desc = "Set the item price for your personal markkit.", help = "/setprice <price>", player = true, min = 1)
	public void onPrice(Player p, String[] args){
		if(!Utils.isInteger(args[0])) return;
		if(p.getItemInHand().getType() == null) return;
		
		int price = Integer.parseInt(args[0]);
		ItemStack inhand = p.getItemInHand();
		main.api.getDivSystem().getMarkkit().set("playershop." + p.getName() + "." + inhand.getTypeId() + "." + inhand.getDurability(), price);
		main.s(p, "Price set!");
	}
		
	@GameCommand(aliases = {"bio"}, desc = "Modify your hover-over description", help = "/bio <message>", player = true, min = 1)
	public void onBio(Player p, String[] args){
	
		DivinityPlayer dp = main.api.getDivPlayer(p);
		dp.set(DPI.PLAYER_DESC, "&7&o" + main.AS(DivinityUtilsModule.createString(args, 0)));
		main.s(p, "Updated!");
	}
	
	@GameCommand(aliases = {"me"}, desc = "This is you", help = "/me <action>", player = true, min = 1)
	public void onMe(Player p, String[] args){
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(main.AS("&f* " + p.getName() + " " + DivinityUtilsModule.createString(args, 0)));
		}
	}
	
	@GameCommand(perm = "wa.staff.admin", aliases = {"motd"}, desc = "Change the MOTD", help = "/motd <message>", player = false, min = 1)
	public void onMOTD(CommandSender p, String[] args){
		main.api.getDivSystem().set(DPI.MOTD, DivinityUtilsModule.createString(args, 0));
		main.s(p, "&bUpdated!");
	}
	
	@GameCommand(aliases = {"enderdragon"}, desc = "Spawn the enderdragon in the end", help = "/enderdragon", player = true)
	public void onEnderDragon(Player p, String[] args){
		
		DivinitySystem system = main.api.getDivSystem();
		
		if (system.getLong(DPI.ENDERDRAGON_CD) <= System.currentTimeMillis() && !system.getBool(DPI.ENDERDRAGON_DEAD)){
			system.set(DPI.ENDERDRAGON_DEAD, true);
			system.set(DPI.ENDERDRAGON_CD, System.currentTimeMillis() + 7200000L);
			Location temp = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
			p.teleport(new Location(Bukkit.getWorld("world_the_end"), 0, 10, 0));
			Bukkit.getWorld("world_the_end").spawnEntity(new Location(Bukkit.getWorld("world_the_end"), 0, 10, 0), EntityType.ENDER_DRAGON);
			p.teleport(temp);
			DivinityUtilsModule.bc(p.getDisplayName() + " has spawned an enderdragon in the end!");
		} else {
			main.s(p, "&c&oActive cooldown. &6" + ((system.getLong(DPI.ENDERDRAGON_CD) - System.currentTimeMillis())/1000)/60 + " &c&ominutes remain.");
		}
	}
	
	@GameCommand(aliases = {"colors"}, desc = "View the colors", help = "/colors", player = false)
	public void onColors(CommandSender cs, String[] args){
		main.s(cs, "&aa &bb &cc &dd &ee &ff &00 &11 &22 &33 &44 &55 &66 &77 &88 &99 &7&ll &7&mm &7&nn &7&oo &7&rr");
	}
	
	@GameCommand(aliases = {"calendar"}, desc = "View our calendar!", help = "/calendar", player = true, min = 0)
	public void onCalendar(Player p, String[] args){

		DivinitySystem player = main.api.getDivSystem();
		if(args.length == 1){
			if(main.api.perms(p, "wa.staff.admin", true)){
				player.set(DPI.CALENDAR_LINK, args[0]);
			}
		}
		
		 JSONChatMessage msg = new JSONChatMessage("", null, null);
		 JSONChatExtra extra = new JSONChatExtra(main.AS("&aClick here to for our calendar!"), null, null);
		 extra.setClickEvent(JSONChatClickEventType.OPEN_URL, player.getStr(DPI.CALENDAR_LINK));
		 msg.addExtra(extra);
		 msg.sendToAllPlayers(); 
	}
	
	@GameCommand(perm = "wa.staff.admin", aliases = {"sudo"}, desc = "Force someone to run a command", help = "/sudo <player> <command>", player = false, min = 2)
	public void onSudo(CommandSender cs, String[] args){
		
		if (main.api.doesPartialPlayerExist(args[0]) && main.api.isOnline(args[0])){
			main.api.getPlayer(args[0]).performCommand(DivinityUtilsModule.createString(args, 1));
			main.s(cs, "Forced " + main.api.getPlayer(args[0]).getDisplayName() + " &bto run " + DivinityUtilsModule.createString(args, 1) + ".");
		} else {
			main.s(cs, "playerNotFound");
		}
	}
	
	@GameCommand(perm = "wa.staff.admin", aliases = {"bc", "broadcast"}, desc = "Broadcasts a message", help = "/broadcast", player = false, min = 1)
	public void onBroadcast(CommandSender cs, String[] args){
		DivinityUtilsModule.bc(DivinityUtilsModule.createString(args, 0));
	}
	
	@GameCommand(aliases = {"calc"}, desc = "Calculator Command", help = "/calc <query>", player = false)
	public void onCalc(CommandSender cs, String[] args){
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");  
			main.s(cs, (double)engine.eval(DivinityUtilsModule.createString(args, 0)) + "");
		} catch (Exception e){
			main.s(cs, "&c&oInvalid equation.");
		}
	}
	
	@GameCommand(perm = "wa.rank.villager", aliases = {"hat"}, desc = "Wear a hat!", help = "/hat", player = true)
	public void onHat(Player p, String[] args){
		if (p.getItemInHand() != null && (p.getInventory().getHelmet() == null || p.getInventory().getHelmet().getType().equals(Material.AIR))){
			p.getInventory().setHelmet(p.getItemInHand());
			p.setItemInHand(new ItemStack(Material.AIR));
		} else {
			main.s(p, "&c&oHand must have something in it and helmet be open.");
		}
	}
	
	@GameCommand(perm = "wa.staff.admin", aliases = {"modify"}, desc = "Divinity Modification Command", help = "/modify list, /modify <player/alliance> <stat> <value>", player = false, min = 1)
	public void onModify(CommandSender p, String[] args){
		
		String dispName = p instanceof Player ? ((Player) p).getDisplayName() : "&6Console";
		
		if (args[0].equals("list")){
			
			String list = "";
			
			for (DivinityStorageModule m : main.api.getOnlineModules().values()){
				list += list.equals("") ? "&6" + m.getName() : "&b, &6" + m.getName();
			}
			
			main.s(p, list);
			
		} else if (args.length >= 3){
			
			try {
				for (DivinityStorageModule m : main.api.getOnlineModules().values()){
					if (m.getName().contains(args[0])){
						if (m.containsKey(args[1])){
							m.put(args[1], Utils.createString(args, 2));
							ElyChannel.STAFF.send("&6System", dispName + " &cmodified &6" + args[1].toUpperCase() + " &cfor " + m.getName() + "&c!", main.api);
							break;
						} else {
							main.s(p, "&c&oNo enum of " + args[1] + " found for " + m.getUuid() + ".");
						}
					}
				}
			} catch (Exception e){
				main.s(p, "&c&oCouldn't find that value or the player.");
			}
			
		} else {
			main.s(p, main.help("modify", this));
		}
	}
	
	@GameCommand(aliases = {"list"}, desc = "List everyone online!", help = "/list", player = false)
	public void onList(CommandSender cs, String[] args){
		
		JSONChatMessage msg = new JSONChatMessage("");
		JSONChatExtra extra = new JSONChatExtra("");
		String m = "";
		boolean color = true;
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (cs instanceof Player){
				extra = new JSONChatExtra(main.AS((color ? "&3" : "&9") + ChatColor.stripColor(main.AS(p.getDisplayName())) + " "));
				extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&o" + p.getName()));
				extra.setClickEvent(JSONChatClickEventType.SUGGEST_COMMAND, "/tell " + p.getName() + " ");
				msg.addExtra(extra);
				color = !color;
			} else {
				m = m.equals("") ? p.getDisplayName() : m + "&8, " + p.getDisplayName();
			}
		}
		
		if (cs instanceof Player){
			msg.sendToPlayer((Player)cs);
		} else {
			main.s(cs, m);
		}
	}
	
	@GameCommand(aliases = {"setrebootserver"}, desc = "Set Reboot Server", help = "/setrebootserver <boolean>", player = false, perm = "wa.staff.admin")
	public void onSetRebootServer(CommandSender cs, String[] args){
		main.api.getDivSystem().set(DPI.IS_REBOOT_SERVER, args[0].equals("true") ? true : false);
		main.s(cs, "Changed to &6" + main.api.getDivSystem().getBool(DPI.IS_REBOOT_SERVER) + "&b!");
	}
	
	@GameCommand(aliases = {"reboot"}, desc = "Reboot Command", help = "/reboot", player = false, perm = "wa.staff.admin")
	public void onReboot(CommandSender cs, String[] args){
		
		String ext = System.getProperty("os.name").contains("Windows") ? ".bat" : ".sh";
		main.api.updateServerName();
		
		try {
			Runtime.getRuntime().exec("../reboot" + ext);
			main.s(cs, "Reboot queued! The reboot server is starting up. The transfer will start soon.");
			DivinityUtilsModule.bc("Reboot in 20 seconds! You will be transfered to the waiting server, no need to log off!");
			main.api.schedule(this, "rebootQueue", 400L, "rebootQueue");
			main.api.schedule(this, "shutdown", 500L, "shutdown");
		} catch (Exception e){
			e.printStackTrace();
			main.s(cs, "&c&oAn error occured with the reboot - aborting!");
		}
	}
	
	public void rebootQueue(){
		main.api.sendAllToServer("reboot");
	}
	
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}
	
	@GameCommand(aliases = {"div", "divinity"}, desc = "Divinity Main Command", help = "/ely help", player = false)
	public void onDivinity(CommandSender p, String[] args){
		
		for (String s : divLogo){
			p.sendMessage(main.AS(s));
		}
	}
	
	@GameCommand(aliases = {"ely", "elysian", "?"}, desc = "Elysian Main Command", help = "/ely help, /ely help all", player = false)
	public void onElysian(CommandSender p, String[] args){
		
		if (args.length == 0){
			
			for (String s : elyLogo){
				p.sendMessage(main.AS(s));
			}
			
		} else {
			
			switch (args[0].toLowerCase()){

				case "help": case "helpmepleaseidontknowwhatimdoing": case "?":
					
					if (p instanceof Player){
					
						DivinityStorageModule dp = main.api.getDivPlayer((Player) p);
						List<String> allowed = new ArrayList<String>();
						Map<String, GameCommand> annos = new HashMap<String, GameCommand>();
						
						for (Object o : main.api.commandMap.values()){
							for (Method m : o.getClass().getMethods()){
								if (m.getAnnotation(GameCommand.class) != null){
									GameCommand anno = m.getAnnotation(GameCommand.class);
									if (dp.getList(DPI.PERMS).contains(anno.perm()) || p.isOp() || anno.perm().equals("wa.guest") || anno.perm().equals("emp.member")){
										allowed.add(anno.aliases()[0]);
									}
									annos.put(anno.aliases()[0], anno);
								}
							}
						}
						
						JSONChatMessage msg = new JSONChatMessage("", null, null);
						int x = 0;
						int y = 0;
						
						List<String> sorted = new ArrayList<String>(annos.keySet());
						Collections.sort(sorted);
						
						for (String command : sorted){
							String alias = "";
							for (String a : annos.get(command).aliases()){
								if (!a.equals(command)){
									alias += alias.equals("") ? "&6" + a : "&7, &6" + a;
								}
							}
							String color = allowed.contains(command) ? "&a" : "&c";
							JSONChatExtra extra = new JSONChatExtra(main.AS(color + command + " &9- "));
							extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&bAliases: " + alias + "\n" + "&bPerm: &6" + annos.get(command).perm() + "\n" + "&bDesc: &6" + annos.get(command).desc() + "\n" + "&bHelp: &6" + annos.get(command).help()));
							msg.addExtra(extra);
							x++; y++;
							if (x == 5 || y >= annos.size()-1){
								if (y > annos.size()-1){
									JSONChatExtra ee = new JSONChatExtra(main.AS("&7&oHover over each command for info!"));
									msg.addExtra(ee);
								}
								msg.sendToPlayer((Player) p);
								x = 0;
								msg = new JSONChatMessage("", null, null);
							}
						}
					} else {
						main.s(p, "&c&oConsole can't run this!");
					}
					
				break;
			
				case "save":
					
					if (main.api.perms(p, "wa.staff.admin", true)){
						new Thread(new Runnable(){
							public void run(){
								long currTime = new Long(System.currentTimeMillis());
								
								List<JSONMap<String, Object>> list = new ArrayList<JSONMap<String, Object>>();
								
								for (DivinityStorageModule dp : main.api.getOnlineModules().values()){
									if (dp.getTable().equals("users")){
										dp.set(DPI.DIS_ENTITY, "none");
										dp.set(DPI.IS_DIS, false);
									}
									list.add(dp);
								}
								
								main.api.getInstance(EmpyrealSQL.class).getType().saveMapsToDatabase(list);
								
								System.out.println("Save Complete @ " + ((System.currentTimeMillis() - currTime)/1000) + " seconds");
								
								ElyChannel.STAFF.send("&6System", "&7Auto-save complete (" + (System.currentTimeMillis()-currTime) + "ms)", main.api);
							}
						}).start();

					}
					
				break;
				
				case "reload":
					
					if (main.api.perms(p, "wa.staff.admin", true)){
					
						try {
							main.api.loadAllDivinityModules();
							DivinityUtilsModule.bc("Empyreal has reloaded.");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				break;
				
				case "backup":
					
					main.s(p, "Coming soon(TM) but not really.");
					
				break;
			}
		}
	}
}