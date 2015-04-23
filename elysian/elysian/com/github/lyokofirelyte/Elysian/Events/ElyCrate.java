package com.github.lyokofirelyte.Elysian.Events;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Elysian.ElyMarkkitItem;
import com.github.lyokofirelyte.Empyreal.Gui.DivInvManager;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class ElyCrate implements AutoRegister<ElyCrate>, Listener {

	Elysian main;
	
	@Getter
	private ElyCrate type = this;
	
	List<ItemStack> loot = new ArrayList<ItemStack>();
//	Map<Material, Integer> prices = new THashMap<Material, Integer>();
	Map<Integer, HashMap<Material, Integer>> prices = new THashMap<Integer, HashMap<Material, Integer>>();
	boolean loaded = false;
	
	public ElyCrate(Elysian main){
		this.main = main;
		
		System.out.println("Class registered");
		
		main.api.schedule(this, "loadValues", 40L, "test");
		
		/*if(!loaded){
			loadValues();
			loaded = true;
		}*/
	
	}
	
	public void loadValues(){
		ElyMarkkitItem item = new ElyMarkkitItem(main.api, Material.STONE, 0);
		
		for(int i :  new int[]{256, 512, 1024, 2048}){
			prices.put(i, new HashMap<Material, Integer>());
		}
		
		for(Material m : Material.values()){
			item.setMaterial(m);
			if(item.getSellPrice(1) >= 4){
				
				for(int i :  new int[]{256, 512, 1024, 2048}){
					if(item.getAmountForPrice(i) != 0 && item.getAmountForPrice(i) <= 64){
						System.out.println("Adding " + item.getAmountForPrice(i) + " " + m);
						prices.get(i).put(m, item.getAmountForPrice(i));
					}
				}

			}
			
		}
		System.out.println("Done loading!");
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			Player p = e.getPlayer();

			if(p.getItemInHand() != null && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasLore() && p.getItemInHand().getItemMeta().getLore().get(0).equals(getKey(128).getItemMeta().getLore().get(0))){
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
					int value = Integer.parseInt(p.getItemInHand().getItemMeta().getLore().get(1).replace(main.AS("&bValue: &6"), ""));

					if(p.getInventory().getItemInHand().getAmount() != 1){
						p.getInventory().getItemInHand().setAmount(p.getInventory().getItemInHand().getAmount() - 1);
					}else{
						p.setItemInHand(new ItemStack(Material.AIR));
					}
					System.out.println(value);
					
					List<Material> test = new ArrayList<Material>(prices.get(value).keySet());
					Material random = test.get(new Random().nextInt(prices.get(value).keySet().size()));
					
					if(p.getInventory().firstEmpty() == -1){
						p.getWorld().dropItem(p.getLocation(), new ItemStack(random, prices.get(value).get(random)));
						main.s(p, "&cYour item was dropped on the ground!");
					}else{
						p.getInventory().addItem(new ItemStack(random, prices.get(value).get(random)));
					}
					p.updateInventory();
					main.s(p, "Random reward given! You have received " + prices.get(value).get(random) + " " + random.toString().toLowerCase().replace('_', ' ') + "'s!");
				}else{
					main.s(p, "&cYou don't have a chest in your inventory!");
				}
			}
		}
	}

	@GameCommand(aliases = {"key"}, perm = "wa.staff.mod", desc = "Get a crate key!", help = "/key <amount>", player = true)
	public void onKey(Player p, String[] args){
		if(args.length == 0){
			showOptions(p, 1);
		}else if(args.length == 1){
			if(Utils.isInteger(args[0])){
				showOptions(p, Integer.parseInt(args[0]));
			}else{
				main.s(p, "&cThat is not a number!");
			}
		}else{
			if(Utils.isInteger(args[0]) && Utils.isInteger(args[0])){
				ItemStack i = getKey(Integer.parseInt(args[1]));
				i.setAmount(Integer.parseInt(args[0]));
				p.getInventory().addItem(i);
			}else{
				main.s(p, "&cThat is not a number!");
			}
		}
	}
		
	
	public ItemStack getKey(int value){
		return DivInvManager.createItem("&6A key!", new String[]{main.AS("&bRight click with this item in your hand and a chest in your inventory!"), main.AS("&bValue: &6" + value)}, Enchantment.DURABILITY, 10, Material.TRIPWIRE_HOOK, 1);
	}
	
	public void showOptions(Player p, int amount){
		JSONChatMessage m = new JSONChatMessage(main.AS("&bWhat is the value of the &6" + amount + " &bkey you want? "));
		for(int i : new int[]{256, 512, 1024, 2048}){
			JSONChatExtra extra = new JSONChatExtra(main.AS("&6" + i + " "));
			extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&bClick to get &6" + amount + " &bkeys with the markkit value of &6" + i + " &bshinies"));
			extra.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/key " + amount + " " + i);
			m.addExtra(extra);
		}
		main.s(p, m);	
	}
	
}
