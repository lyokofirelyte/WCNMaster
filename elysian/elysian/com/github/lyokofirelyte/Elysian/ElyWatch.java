package com.github.lyokofirelyte.Elysian;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Commands.ElyPerms;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Elysian.Patrols.ElyPatrol;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyChannel;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.PatrolTask;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;

public class ElyWatch implements Runnable, AutoRegister {
	
	private Elysian main;
	
	public ElyWatch(Elysian i){
		main = i;
	}
	
	List<String> worlds = Arrays.asList(
		"WACP",
		"Keopi",
		"Tripolis",
		"Syracuse",
		"not_cylum",
		"WCN_Builds",
		"creative"
	);

	@Override
	public void run(){
		
		DivinitySystem system = main.api.getDivSystem();
		
		for (Player p : Bukkit.getOnlinePlayers()){
			DivinityPlayer dp = main.api.getDivPlayer(p);
			dp.set(DPI.IN_COMBAT, false);
			afkCheck(p, dp, system);
			nameCheck(p, dp);
			unMuteCheck(p, dp);
			creativeCheck(p);
			rankCheck(dp);
			moneyCheck(p, dp);
			invCheck(p, dp);
			opCheck(p);
			main.api.event(new ScoreboardUpdateEvent(p));
		}
		
		sleeping();
		markkit();
		system.set(DPI.ROLLBACK_IN_PROGRESS, false);
	}
	
	private void creativeCheck(Player p){
		
		if (!main.api.getDivPlayer(p).getBool(DPI.IN_GAME)){	
			if (worlds.contains(p.getWorld().getName())){
				if (!p.getGameMode().equals(GameMode.CREATIVE)){
					p.setGameMode(GameMode.CREATIVE);
				}
				if (!p.getAllowFlight()){
					p.setAllowFlight(true);
				}
				if (p.getWalkSpeed() < 0.2F){
					p.setWalkSpeed(0.2F);
				}
				if (p.getFlySpeed() < 0.2F){
					p.setFlySpeed(0.2F);
				}
			}
		}
	}
	
	private void opCheck(Player p){
		if (p.isOp() && !main.api.getDivSystem().getList(DPI.OP_CHECK).contains(p.getName())){
			p.setOp(false);
			main.s(p, "&c&oYour OP has been removed by the system.");
			main.s(p, "&c&oYou were not on Elysian's approved OP list.");
			main.s(p, "&c&oIf this is an error, contact an admin.");
			ElyChannel.STAFF.send("&6System", "OP auto-removed from " + p.getDisplayName() + ".", main.api);
			ElyChannel.STAFF.send("&6System", "User not found in /divinity/system/system.yml.", main.api);
		}
	}
	
	private void unMuteCheck(Player p, DivinityPlayer dp){
		
		if (dp.getBool(DPI.MUTED)){
			if (System.currentTimeMillis() >= dp.getLong(DPI.MUTE_TIME)){
				dp.set(DPI.MUTED, false);
				DivinityUtilsModule.bc("&4&oThe mute placed on " + p.getDisplayName() + " &4&ohas expired.");
			}
		}
		
		if (dp.getBool(DPI.DISABLED) && dp.getStr(DPI.RING_LOC).equals("none") && dp.getBool(DPI.PVP_CHOICE)){
			if (System.currentTimeMillis() >= dp.getLong(DPI.DISABLE_TIME)){
				dp.set(DPI.DISABLED, false);
				DivinityUtilsModule.bc("&4&oThe disable placed on " + p.getDisplayName() + " &4&ohas expired.");
			}
		}
	}
	
	private void nameCheck(Player p, DivinityPlayer dp){
		
		if (!ChatColor.stripColor(main.AS(p.getDisplayName())).startsWith(ChatColor.stripColor(main.AS(dp.getStr(DPI.DISPLAY_NAME).substring(0, 3))))){
			main.s(p, "none", "Invalid nickname detected, resetting...");
			p.setDisplayName("&7" + p.getName());
		}
		
		if (dp.getStr(DPI.ALLIANCE_NAME).equals("none") && !p.getDisplayName().startsWith("&7")){
			p.setDisplayName("&7" + dp.getStr(DPI.DISPLAY_NAME));
		}
	}
	
	private void afkCheck(Player p, DivinityPlayer dp, DivinitySystem system){
		
		if (dp.getLong(DPI.AFK_TIME_INIT) <= 0){
			dp.set(DPI.AFK_TIME_INIT, System.currentTimeMillis());
		}
		
		if (!system.getList(DPI.AFK_PLAYERS).contains(p.getName()) && System.currentTimeMillis() >= dp.getLong(DPI.AFK_TIME_INIT) + 180000L){
			dp.set(DPI.AFK_TIME, System.currentTimeMillis());
			system.getList(DPI.AFK_PLAYERS).add(p.getName());
			DivinityUtilsModule.bc(p.getDisplayName() + " &3&ois now away.");
			if (main.AS("&7[afk] " + p.getDisplayName()).length() > 16){
				p.setPlayerListName(main.AS("&7[afk] " + p.getDisplayName()).substring(0, 15));
			} else {
				p.setPlayerListName(main.AS("&7[afk] " + p.getDisplayName()));
			}
		}
	}
	
	private void rankCheck(DivinityPlayer dp){
		
		if (dp.getList(DPI.PERMS).contains("wa.staff.intern")){
			for (String rank : ((ElyPerms) main.api.getInstance(ElyPerms.class)).staffGroups){
				if (dp.getList(DPI.PERMS).contains("wa.staff." + rank)){
					switch (rank){
						case "owner":
							dp.set(DPI.RANK_NAME, "&5WCN");
							dp.set(DPI.STAFF_DESC, "&7&oThe owner of the server!");
						break;
						case "admin":
							dp.set(DPI.RANK_NAME, "&4WCN"); 
							dp.set(DPI.STAFF_DESC, "&7&oAn administrator of the server.\n&7&oResponsible for server management.\n&7&oPlugin Devs: Hugs, Winneon, Msnijder");
						break;
						case "mod2": case "mod+":
							dp.set(DPI.RANK_NAME, "&9WCN");
							dp.set(DPI.STAFF_DESC, "&7&oAn expirenced moderator.\n&7&oResponsible for all moderation actions and community well-being.\n&7&oAccess to most commands.");
						break;
						case "mod":
							dp.set(DPI.RANK_NAME, "&2WCN");
							dp.set(DPI.STAFF_DESC, "&7&oA moderator of the server.\n&7&oResponsible for chat, helping people, grief checks, and general server support.");
						break;
						case "intern":
							dp.set(DPI.RANK_NAME, "&aWCN");
							dp.set(DPI.STAFF_DESC, "&7&oNew staff of the server.\n&7&oResponsible for improving their focus on the server.\n&7&oCan check griefs and provide general help to members.");
						break;
					}
					break;
				}
			}
		}
	}
	
	private void moneyCheck(Player p, DivinityPlayer dp){
		
		if (dp.getInt(DPI.MOB_MONEY) > 0){
			dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) + dp.getInt(DPI.MOB_MONEY));
			main.s(p, dp.getInt(DPI.MOB_MONEY) + " &oshinies earned in the last 20 seconds.");
			dp.set(DPI.MOB_MONEY, 0);
		}
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void invCheck(Player p, DivinityPlayer dp){
		
		if (!dp.getBool(MMO.IS_MINING) && !dp.getBool(MMO.IS_DIGGING)){
			for (ItemStack i : p.getInventory().getContents()){
				if (i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
					if (i.getItemMeta().getLore().contains(main.AS("&3&oSuperbreaker active!"))){
						ItemMeta im = i.getItemMeta();
						List<String> lore = im.getLore();
						lore.remove(main.AS("&3&oSuperbreaker active!"));
						im.setLore(lore);
						for (Enchantment e : i.getItemMeta().getEnchants().keySet()){
							i.removeEnchantment(e);
						}
						try {
							if (((Map<Enchantment,Integer>)dp.getRawInfo(MMO.SAVED_ENCHANTS)).size() > 0){
								for (Enchantment e : ((Map<Enchantment,Integer>)dp.getRawInfo(MMO.SAVED_ENCHANTS)).keySet()){
									i.addUnsafeEnchantment(e, ((Map<Enchantment, Integer>)dp.getRawInfo(MMO.SAVED_ENCHANTS)).get(e));
								}
							}
						} catch (Exception e){}
						i.setItemMeta(im);
						p.updateInventory();
						dp.set(MMO.IS_SUPER_BREAKING, false);
						dp.set(MMO.IS_MINING, false);
						dp.err("&c&oSuperbreaker ended!");
					}
				}
			}
		}
		
		for (ItemStack i : p.getInventory().getContents()){
			if (i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName()){
				if ((p.getItemInHand() != null && !p.getItemInHand().equals(i)) || p.getItemInHand() == null){
					for (ElySkill skill : ElySkill.values()){
						if (i.getItemMeta().getDisplayName().toLowerCase().contains(skill.s().toLowerCase())){
							ItemStack newItem = new ItemStack(i.getTypeId(), i.getAmount(), i.getData().getData());
							ItemMeta im = newItem.getItemMeta();
							im.setLore(i.getItemMeta().hasLore() ? i.getItemMeta().getLore() : new ArrayList<String>());
							
							for (Enchantment e : i.getItemMeta().getEnchants().keySet()){
								im.addEnchant(e, i.getItemMeta().getEnchants().get(e), true);
							}
							
							i.setItemMeta(im);
							break;
						}
					}
				}
			}
		}
	}
	
	private void sleeping(){
		
		int sleeping = 0;
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.isSleeping() && p.getWorld().getName().equals("world")){
				sleeping++;
			}
		}
		
		if (sleeping > (Bukkit.getOnlinePlayers().size()/2)){
			Bukkit.getWorld("world").setTime(0);
			DivinityUtilsModule.bc("Over half of the server is sleeping - setting to day.");
		}
	}
	
	private void markkit(){
		main.divinity.api.sheets.fetch(false, false);
	}
}