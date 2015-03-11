package com.github.lyokofirelyte.Elysian.Events;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Divinity.Manager.ElyMarkkitItem;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class ElyCrate implements AutoRegister, Listener{

	Elysian main;
	List<ItemStack> loot = new ArrayList<ItemStack>();
	Map<Material, Integer> prices = new THashMap<Material, Integer>();
	
	public ElyCrate(Elysian main){
		this.main = main;
		
		ElyMarkkitItem item = new ElyMarkkitItem(main.divinity.api, Material.STONE, 0);
		
		for(Material m : Material.values()){
			item.setMaterial(m);
			if(item.getSellPrice(1) >= 4 && item.getAmountForPrice(512) != 0){
				prices.put(m, item.getAmountForPrice(512));
			}
			
		}
	
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			Player p = e.getPlayer();

			if(p.getItemInHand() != null && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasLore() && p.getItemInHand().getItemMeta().getLore().equals(getKey().getItemMeta().getLore())){
				e.setCancelled(true);
				if(p.getInventory().contains(Material.CHEST)){
					int index = 0;
					for(ItemStack i : p.getInventory().getContents()){
						if(i != null && i.getType() != Material.AIR && i.getType().equals(Material.CHEST)){
							if(i.getAmount() != 1){
								i.setAmount(i.getAmount() - 1);
							}else{
								p.getInventory().setItem(index, new ItemStack(Material.AIR));
							}
							break;
						}
						index++;
					}
					
					if(p.getInventory().getItemInHand().getAmount() != 1){
						p.getInventory().getItemInHand().setAmount(p.getInventory().getItemInHand().getAmount() - 1);
					}else{
						p.setItemInHand(new ItemStack(Material.AIR));
					}
					
					Material random = new ArrayList<Material>(prices.keySet()).get(new Random().nextInt(prices.keySet().size()));
					
					if(p.getInventory().firstEmpty() == -1){
						p.getWorld().dropItem(p.getLocation(), new ItemStack(random, prices.get(random)));
						main.s(p, "&cYour item was dropped on the ground!");
					}else{
						p.getInventory().addItem(new ItemStack(random, prices.get(random)));
					}
					p.updateInventory();
					main.s(p, "Random reward given! You have received " + prices.get(random) + " " + random.toString().toLowerCase().replace('_', ' ') + "'s!");
				}else{
					main.s(p, "&cYou don't have a chest in your inventory!");
				}
			}
		}
	}

	@DivCommand(aliases = {"key"}, perm = "wa.staff.intern", desc = "Get a crate key!", help = "/key <amount>", player = true)
	public void onRollDice(Player p, String[] args){
		if(args.length == 0){
			p.getInventory().addItem(getKey());
		}else{
			if(main.divinity.api.divUtils.isInteger(args[0])){
				ItemStack i = getKey();
				i.setAmount(Integer.parseInt(args[0]));
				p.getInventory().addItem(i);
			}else{
				main.s(p, "&cThat is not a number!");
			}
		}
	}
		
	
	public ItemStack getKey(){
		return DivInvManager.createItem("&6A key!", new String[]{main.AS("&bRight click with this item in your hand and a chest in your inventory!")}, Enchantment.DURABILITY, 10, Material.TRIPWIRE_HOOK, 1);
	}
}
