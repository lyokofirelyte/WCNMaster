package com.github.lyokofirelyte.Elysian.Gui;

import static com.github.lyokofirelyte.Empyreal.Gui.DivInvManager.createItem;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.api.ElySkill;
import com.github.lyokofirelyte.Empyreal.Gui.DivGui;

public class GuiLunarSpells extends DivGui {

	private Elysian main;
	
	ItemStack[] stacks = new ItemStack[]{
		createItem("&aDeflect", new String[] { "&6&oPush back mobs near you.", "&8&oLevel 0" }, Enchantment.DURABILITY, 10, Material.FEATHER),
		createItem("&9Renewal", new String[] { "&6&oHeal you and those around you with a healing beam.", "&8&oLevel 10" }, Enchantment.DURABILITY, 10, Material.FEATHER)
	};
	
	public GuiLunarSpells(Elysian main){
		
		super(9, "&3(( &9LUNAR &3))");
		this.main = main;
	}
	
	@Override
	public void create(){
		
		for (int i = 0; i < stacks.length; i++){
			addButton(i, stacks[i]);
		}
	}
	
	@Override
	public void actionPerformed(final Player p){
		
		if (item != null){
			if (canUse(p, stacks[slot])){
				ItemStack i = stacks[slot];
				ItemMeta im = i.getItemMeta();
				List<String> lore = im.getLore();
				lore.add(main.AS("&7&o" + p.getName()));
				im.setLore(lore);
				i.setItemMeta(im);
				if (!p.getInventory().contains(i)){
					p.getInventory().addItem(i);
				}
			} else {
				main.s(p, "&c&oYou don't have the correct level for that!");
			}
		}
	}
	
	public boolean canUse(Player p, ItemStack i){
		return main.api.getDivPlayer(p).getLevel(ElySkill.LUNAR) >= Integer.parseInt(i.getItemMeta().getLore().get(1).split(" ")[1]);
	}
}