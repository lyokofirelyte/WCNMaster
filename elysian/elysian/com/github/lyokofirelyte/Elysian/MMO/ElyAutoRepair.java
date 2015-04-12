package com.github.lyokofirelyte.Elysian.MMO;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElyAutoRepair implements AutoRegister<ElyAutoRepair>, Listener {
	
	Elysian main;
	
	@Getter
	private ElyAutoRepair type = this;

	public ElyAutoRepair(Elysian i) {
		main = i;
		enchants.put(Enchantment.DAMAGE_ALL, 1);
		enchants.put(Enchantment.LURE, 2);
		enchants.put(Enchantment.DAMAGE_ARTHROPODS, 2);
		enchants.put(Enchantment.KNOCKBACK, 2);
		enchants.put(Enchantment.FIRE_ASPECT, 4);
		enchants.put(Enchantment.LOOT_BONUS_MOBS, 4);
		enchants.put(Enchantment.DIG_SPEED, 1);
		enchants.put(Enchantment.DURABILITY, 2);
		enchants.put(Enchantment.LOOT_BONUS_BLOCKS, 4);
		enchants.put(Enchantment.SILK_TOUCH, 8);
		enchants.put(Enchantment.ARROW_DAMAGE, 1);
		enchants.put(Enchantment.ARROW_KNOCKBACK, 4);
		enchants.put(Enchantment.ARROW_FIRE, 4);
		enchants.put(Enchantment.ARROW_INFINITE, 8);
	}
	
	List<String> accept = Arrays.asList("_sword", "_spade", "_axe", "_pickaxe", "shear", "bow", "_hoe");
	Map<Enchantment, Integer> enchants = new THashMap<>();
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e){
		repair(main.api.getDivPlayer(e.getPlayer()), e.getPlayer().getItemInHand());
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e){
		if (e.getEntity() instanceof Player == false && e.getDamager() instanceof Player){
			repair(main.api.getDivPlayer((Player)e.getDamager()), ((Player)e.getDamager()).getItemInHand());
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e){
		
		if (e.getInventory().getTitle().contains(" EXP STORED") && e.getPlayer() instanceof Player){
			List<ItemStack> i = new ArrayList<ItemStack>();
			for (ItemStack item : e.getInventory().getContents()){
				if (item != null){
					i.add(item);
				}
			}
			main.api.getDivPlayer((Player)e.getPlayer()).set(DPI.REPAIR_INV, i);
		}
	}
	
	@DivCommand(perm = "wa.staff.admin", aliases = {"dur"}, desc = "Sets durability to 50 for debugging", help = "/dur", player = true)
	public void onDebug(Player p, String[] args){
		p.getItemInHand().setDurability((short)(p.getItemInHand().getType().getMaxDurability() - 30));
	}
	
	@DivCommand(perm = "wa.rank.dweller", aliases = {"repair", "rep"}, desc = "Auto-Repair Configuration", help = "/repair", player = true)
	public void onRepair(Player p, String[] args){
		
		switch (p.getWorld().getName()){
			case "world": case "world_nether": case "world_the_end": break;
			default: main.s(p, "&c&oYou can't use this in this world."); return;
		}
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (args.length == 0 || args[0].equals("help")){
			
			for (String msg : new String[]{
				"/rep config",
				"/rep addxp <amount>",
				"/rep remxp <amount>",
				"/rep addtype [takes item in hand]",
				"/rep remtype [takes item in hand]",
				"/rep list",
				"/rep toggle"
			}){
				dp.s(msg);
			}
			
		} else {
			
			switch (args[0].toLowerCase()){
			
				case "list":
					
					String msg = "";
					dp.s("&3The following tool types auto-repair:");
					
					for (String s : dp.getList(DPI.REPAIR_TOOLS)){
						msg = msg.equals("") ? "&6" + s : msg + "&7, &6" + s;
					}
					
					dp.s(msg);
					
				break;
			
				case "config":
					
					Inventory inv = Bukkit.createInventory(null, 9, main.AS("&e" + dp.getInt(DPI.REPAIR_EXP) + " EXP STORED"));
					
					for (ItemStack i : dp.getStack(DPI.REPAIR_INV)){
						inv.addItem(i);
					}
					
					p.openInventory(inv);
					
				break;
			
				case "addxp": case "remxp":
					
					if (args.length == 2 && DivinityUtilsModule.isInteger(args[1])){
						
						int amt = Integer.parseInt(args[1]);
						int stored = dp.getInt(DPI.EXP);
						int repairExp = dp.getInt(DPI.REPAIR_EXP);
						
						if (args[0].equals("addxp")){
							
							if (amt <= stored){
								dp.set(DPI.REPAIR_EXP, repairExp + amt);
								dp.set(DPI.EXP, stored - amt);
								dp.s("Added your XP to the repair system.");
							} else {
								dp.err("You don't have that much xp!");
							}
							
						} else {
							
							if (amt <= repairExp){
								dp.set(DPI.EXP, stored + amt);
								dp.set(DPI.REPAIR_EXP, repairExp - amt);
								dp.s("Took xp from the repair system.");
							} else {
								dp.err("You don't have enough xp in the repair system!");
							}	
						}
						
					} else {
						dp.err("That's not a number.");
					}
					
				break;
				
				case "toggle":
					
					dp.set(DPI.REPAIR_TOGGLE, !dp.getBool(DPI.REPAIR_TOGGLE));
					dp.s("You will " + (dp.getBool(DPI.REPAIR_TOGGLE) ? "&aauto-repair tools" : "&cno longer auto-repair tools") + "&b.");
					
				break;
				
				case "addtype":
					
					if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)){
						
						ItemStack i = p.getItemInHand();
						List<String> tools = dp.getList(DPI.REPAIR_TOOLS);
						
						for (String acceptable : accept){
							if (i.getType().toString().contains(acceptable.toUpperCase())){
								if (!tools.contains(i.getType().toString())){
									tools.add(i.getType().toString());
									dp.set(DPI.REPAIR_TOOLS, tools);
									dp.s("Added this tool to the allowed list!");
								} else {
									dp.err("You already have that tool type on the list.");
								}
								return;
							}
						}
						
						dp.err("This item is not applicable for the Elysian Auto Repair.");
						
					} else {
						dp.err("You're not holding anything... -_-");
					}
					
				break;
				
				case "remtype":
					
					if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)){
						if (dp.getList(DPI.REPAIR_TOOLS).contains(p.getItemInHand().getType().toString())){
							dp.getList(DPI.REPAIR_TOOLS).remove(p.getItemInHand().getType().toString());
							dp.s("Removed!");
						}
					} else {
						dp.err("You're not holding anything.");
					}
					
				break;
				
			}
		}
	}
	
	private void repair(DivinityPlayer dp, ItemStack i){
		
		if (i.getDurability() >= i.getType().getMaxDurability() - 30){
			for (String acceptable : accept){
				if (i.getType().toString().contains(acceptable.toUpperCase())){
					if (dp.getBool(DPI.REPAIR_TOGGLE) && doesContain(dp, Material.ANVIL, 1, false)){
						
						int expLevelCost = 2;
						int expCost = 0;
						int neededItems = 1;
						
						if (i.hasItemMeta() && i.getItemMeta().hasEnchants()){
							
							ItemMeta im = i.getItemMeta();
							neededItems += im.getEnchants().size();
							
							for (Enchantment e : im.getEnchants().keySet()){
								if (enchants.containsKey(e)){
									expLevelCost += enchants.get(e) * im.getEnchants().get(e);
								}
							}
							
							switch (im.getEnchants().size()){
								case 1: expLevelCost += 1; break;
								case 2: expLevelCost += 3; break;
								case 3: expLevelCost += 6; break;
								case 4: expLevelCost += 10; break;
								case 5: expLevelCost += 15; break;
							}
							
							for (int ii = 0; ii < expLevelCost; ii++){
								if (ii <= 15){
									expCost += (2*expLevelCost)+7;
								} else if (ii <= 30){
									expCost += (5*expLevelCost)-38;
								} else {
									expCost += (9*expLevelCost)-158;
								}
							}
						}
						
						if (dp.getInt(DPI.REPAIR_EXP) >= expCost){
							
							Material needed = null;
							
							switch (i.getType().toString().split("_")[0].toLowerCase()){
								case "diamond": needed = Material.DIAMOND; break;
								case "iron": needed = Material.IRON_INGOT; break;
								case "golden": needed = Material.GOLD_INGOT; break;
								case "stone": needed = Material.COBBLESTONE; break;
								case "wood": needed = Material.WOOD; break;
							}
							
							if (needed != null){
								if (doesContain(dp, needed, neededItems, true)){
									dp.set(DPI.REPAIR_EXP, dp.getInt(DPI.REPAIR_EXP) - expCost);
									i.setDurability((short) 0);
									dp.s("Item auto-repaired. Cost: &6" + neededItems + "x " + needed.toString().toLowerCase() + " &band &6" + expCost + " &bexp.");
								}
							}
						}
						
						return;
					}
				}
			}
		}
	}
	
	private boolean doesContain(DivinityPlayer dp, Material m, int amt, boolean remove){
		
		for (ItemStack i : dp.getStack(DPI.REPAIR_INV)){
			if (i != null && i.getType().equals(m) && i.getAmount() >= amt){
				if (remove){
					if (i.getAmount() > amt){
						i.setAmount(i.getAmount() - amt);
					} else {
						i.setType(Material.AIR);
					}
					Bukkit.getPlayer(dp.getUuid()).updateInventory();
				}
				return true;
			}
		}
		
		return false;
	}
}