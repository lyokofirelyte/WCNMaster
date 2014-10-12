package com.github.lyokofirelyte.Divinity.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;

/**
 * @author Jesse Bryan
 */

public class DivInvManager implements Listener {
	
	private API main;
	public HashMap<String, DivGui> currentGui = new HashMap<String, DivGui>();
	
	public DivInvManager(API api){
		main = api;
	}
	
	ItemStack i;
	ItemMeta iMeta;
	List<String> loreSplit;
	
	public ItemStack makeItem(String dispName, String lore, Boolean e, Enchantment enchant, int amplifier, int itemType, Material mat, int itemAmount){
		
		i = new ItemStack(mat, itemAmount, (short) itemType);
        iMeta = i.getItemMeta();
    	List<String> loreList = new ArrayList<>();
    	loreList.add(lore);
        
        if (e){
        	iMeta.addEnchant(enchant, amplifier, true);
        }

        iMeta.setDisplayName(dispName);
        iMeta.setLore(loreList);
        i.setItemMeta(iMeta);
		
		return i;
	}
	
	public static ItemStack createItem(String display, String[] lore, Material mat, int... data){
		
		ItemStack item = null;
		
		switch (data.length){
		
		default:
			
			item = new ItemStack(mat, 1);
			break;
			
		case 1:
			
			item = new ItemStack(mat, data[0]);
			break;
			
		case 2:
			
			item = new ItemStack(mat, data[0], (short) data[1]);
			break;
			
		}
		
		ItemMeta meta = item.getItemMeta();
		List<String> loreL = new ArrayList<String>();
		
		for (String l : lore){
			loreL.add(DivinityUtilsModule.AS(l));
		}
		
		meta.setDisplayName(DivinityUtilsModule.AS(display));
		meta.setLore(loreL);
		item.setItemMeta(meta);
		
		return item;
		
	}
	
	public static ItemStack createItem(String display, String[] lore, short durability, Material mat, int... data){
		
		ItemStack item = createItem(display, lore, mat, data);
		item.setDurability(durability);
		
		return item;
		
	}
	
	public static ItemStack createItem(String display, String[] lore, Enchantment enchant, int amp, Material mat, int... data){
		
		ItemStack item = createItem(display, lore, mat, data);
		ItemMeta meta = item.getItemMeta();
		
		meta.addEnchant(enchant, amp, true);
		item.setItemMeta(meta);
		
		return item;
		
	}
	
	public static ItemStack createItem(String display, String[] lore, Enchantment enchant, int amp, short durability, Material mat, int... data){
		
		ItemStack item = createItem(display, lore, durability, mat, data);
		ItemMeta meta = item.getItemMeta();
		
		meta.addEnchant(enchant, amp, true);
		item.setItemMeta(meta);
		
		return item;
		
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e){
		
		if (e.getWhoClicked() instanceof Player){
			Player p = (Player) e.getWhoClicked();
			if (currentGui.containsKey(p.getName())){
				DivGui gui = currentGui.get(p.getName());
				if (e.getInventory().getName().equals(DivinityUtilsModule.AS(gui.title))){
					e.setCancelled(true);
					mouseClicked(p, e.getSlot(), gui, e.getCurrentItem(), e);
				}
			}
		}
	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent e){
		
		if (e.getWhoClicked() instanceof Player){
			Player p = (Player) e.getWhoClicked();
			if (this.currentGui.containsKey(p.getName())){
				DivGui gui = this.currentGui.get(p.getName());
				if (e.getInventory().getName().equals(DivinityUtilsModule.AS(gui.title))){
					e.setCancelled(true);
					mouseDragged(p, gui, e.getCursor(), e);
				}
			}
		}
	}
	
	@EventHandler
	public void onMove(InventoryMoveItemEvent e){
		
		if (e.getSource().getHolder() instanceof Player){
			Player p = (Player) e.getSource().getHolder();
			if (this.currentGui.containsKey(p.getName())){
				DivGui gui = this.currentGui.get(p.getName());
				if (e.getSource().getHolder().getInventory().getName().equals(DivinityUtilsModule.AS(gui.title))){
					e.setCancelled(true);
					mouseMoved(p, gui, e.getItem(), e);
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(InventoryInteractEvent e){
		
		if (e.getWhoClicked() instanceof Player){
			Player p = (Player) e.getWhoClicked();
			if (this.currentGui.containsKey(p.getName())){
				DivGui gui = this.currentGui.get(p.getName());
				if (e.getInventory().getName().equals(DivinityUtilsModule.AS(gui.title))){
					e.setCancelled(true);
					mouseInteracted(p, gui, e);
				}
			}
		}
	}
	
	public void displayGui(Player p, DivGui gui){
		currentGui.put(p.getName(), gui);
		gui.create();
		p.openInventory(gui.getInv());
	}
	
	public void mouseClicked(Player p, int slot, DivGui gui, ItemStack item, InventoryClickEvent click){
		gui.current = "click";
		gui.slot = slot;
		gui.item = item;
		gui.click = click;
		gui.actionPerformed(p);
	}
	
	public void mouseDragged(Player p, DivGui gui, ItemStack item, InventoryDragEvent drag){
		gui.current = "drag";
		gui.item = item;
		gui.drag = drag;
		gui.actionPerformed(p);
	}
	
	public void mouseMoved(Player p, DivGui gui, ItemStack item, InventoryMoveItemEvent move){
		gui.current = "move";
		gui.item = item;
		gui.move = move;
		gui.actionPerformed(p);
	}
	
	public void mouseInteracted(Player p, DivGui gui, InventoryInteractEvent interact){
		gui.current = "interact";
		gui.interact = interact;
		gui.actionPerformed(p);
	}
	
}