package com.github.lyokofirelyte.Elysian.Gui;

import static com.github.lyokofirelyte.Empyreal.Gui.DivInvManager.createItem;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Elysian.ElySkill;
import com.github.lyokofirelyte.Empyreal.Gui.DivGui;

public class GuiSolarSpells extends DivGui {

	private Elysian main;
	
	ItemStack[] stacks = new ItemStack[]{
		createItem("&cFire Blast", new String[] { "&6&oA simple blast of fire.", "&8&oLevel 0" }, Enchantment.DURABILITY, 0, Material.STICK),
		createItem("&bKersplash", new String[] { "&6&oA simple blast of water.", "&8&oLevel 5" }, Enchantment.DURABILITY, 0, Material.STICK),
		createItem("&3Rapid Fire", new String[] { "&6&oA simple quickly-fired... fireball.", "&8&oLevel 10" }, Enchantment.DURABILITY, 0, Material.STICK),
		createItem("&2Earth Bound", new String[] { "&6&oChuck chunks of dirt into your foes!", "&8&oLevel 15" }, Enchantment.DURABILITY, 0, Material.STICK),
		createItem("&bDiamond Blitz", new String[] { "&6&oKill your foes... with style!", "&8&oLevel 20" }, Enchantment.DURABILITY, 0, Material.STICK)
	};
	
	public GuiSolarSpells(Elysian main){
		
		super(9, "&4)) &6SOLAR &4((");
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
		return main.api.getDivPlayer(p).getLevel(ElySkill.SOLAR) >= Integer.parseInt(i.getItemMeta().getLore().get(1).split(" ")[1]);
	}
}