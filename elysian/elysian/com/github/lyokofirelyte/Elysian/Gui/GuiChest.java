package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Gui.DivGui;

import static com.github.lyokofirelyte.Empyreal.Gui.DivInvManager.*;

public class GuiChest extends DivGui {
	
	private Elysian main;
	private DivinityPlayer p;
	private String lookup;
	
	public GuiChest(Elysian main, DivinityPlayer p, String lookup){
		
		super(54, "&2Chest Viewer");
		this.main = main;
		this.p = p;
		this.lookup = lookup;
	}
	
	@Override
	public void create(){
		 
		 for (String chest : p.getList(DPI.OWNED_CHESTS)){
			 if (inv.firstEmpty() != -1){
				 String[] loc = chest.split(" ");
				 Block block = new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3])).getBlock();
				 if (block.getState() instanceof Chest || block.getState() instanceof DoubleChest){
					if (lookup.equals("all") || lookup(lookup, chest)){
						if (getInv().firstEmpty() != -1){
							addButton(getInv().firstEmpty(), createItem("&b" + loc[0] + " " + loc[1] + " " + loc[2] + " " + loc[3], new String[] { "&7&oOwned Chest", "&6Left-click: Open Chest", "&6Right-click: Teleport to Chest" }, Enchantment.DURABILITY, 10, Material.CHEST));
						}
					}
				 }
			 }
		 }
	}
	
	@Override
	public void actionPerformed(final Player p){

		if (item != null && item.getType().equals(Material.CHEST) && click != null && click.isLeftClick()){
			
			String[] loc = ChatColor.stripColor(main.AS(item.getItemMeta().getDisplayName())).split(" ");
			Block block = new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3])).getBlock();
			
			if (block.getState() instanceof Chest){
				Chest chest = (Chest) block.getState();
				p.openInventory(chest.getInventory());
			} else if (block.getState() instanceof DoubleChest){
				DoubleChest chest = (DoubleChest) block.getState();
				p.openInventory(chest.getInventory());
			}
			
		} else if (item != null && item.getType().equals(Material.CHEST) && click != null && click.isRightClick()){
			String[] loc = ChatColor.stripColor(main.AS(item.getItemMeta().getDisplayName())).split(" ");
			Location locTP = new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
			main.api.event(new DivinityTeleportEvent(p, locTP));
		}
	}
	
	private boolean lookup(String lookup, String c){
		
		String[] loc = c.split(" ");
		Block block = new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3])).getBlock();
		ItemStack[] contents = null;
		 
		if (block.getState() instanceof Chest){
			Chest chest = (Chest) block.getState();
			contents = chest.getInventory().getContents();
		} else if (block.getState() instanceof DoubleChest){
			DoubleChest chest = (DoubleChest) block.getState();
			contents = chest.getInventory().getContents();
		}
		
		for (ItemStack i : contents){
			if (i != null && i.getType().toString().toLowerCase().contains(lookup.toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
}