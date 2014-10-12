package com.github.lyokofirelyte.Elysian.Gui;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.*;

public class GuiCloset extends DivGui {
	
	private Elysian main;
	private DivGui parent;
	private DivInvManager invManager;
	int closets = 0;
	
	public GuiCloset(Elysian main, DivGui parent){
		
		super(54, "&3t r a d e &f][&3 c e n t r e");
		this.main = main;
		this.parent = parent;
		this.invManager = (DivInvManager) main.api.getInstance(DivInvManager.class);
	}
	
	@Override
	public void create(){
		
		addButton(45, createItem("&b( e l y s i a n )", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.BEACON));
		
		for (int i = 0; i < 5; i++){
			if (parent.equals(main.closets.get(i))){
				closets = i+1;
				break;
			}
		}
		
		if (main.closets.containsKey(closets-1)){
			addButton(52, createItem("&3PAGE " + (closets-1), new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
		} else {
			addButton(52, createItem("&b( e l y s i a n )", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
		}
		
		if (main.closets.containsKey(closets+1)){
			addButton(53, createItem("&3PAGE " + (closets+1), new String[] { "&b> > >" }, Enchantment.DURABILITY, 10, Material.FLINT));
		} else {
			addButton(53, createItem("&c&oLast Page Reached.", new String[] { "&b> > >" }, Enchantment.DURABILITY, 10, Material.FLINT));
		}
	}
	
	@Override
	public void actionPerformed(final Player p){

		switch (slot){
		
			case 45: invManager.displayGui(p, parent); break;
			
			case 52:
				
				if (main.closets.containsKey(closets-1)){
					invManager.displayGui(p, main.closets.get(closets-1));
				} else {
					invManager.displayGui(p, new GuiRoot(main));
				}
				
			break;
			
			case 53: 
				
				if (main.closets.containsKey(closets+1)){
					invManager.displayGui(p, main.closets.get(closets+1));
				}
				
			break;
			
			default:
				
				if (item != null && !item.getType().equals(Material.AIR) && item.hasItemMeta() && item.getItemMeta().hasLore()){
					
					DivinityPlayer seller = main.api.getDivPlayer(ChatColor.stripColor(main.AS(item.getItemMeta().getLore().get(1))));
					DivinityPlayer buyer = main.api.getDivPlayer(p);
					int price = Integer.parseInt(item.getItemMeta().getLore().get(0).substring(2));
					
					if (buyer.getInt(DPI.BALANCE) >= price){
						
						if (!seller.uuid().equals(buyer.uuid())){
							buyer.set(DPI.BALANCE, buyer.getInt(DPI.BALANCE) - price);
							seller.set(DPI.BALANCE, seller.getInt(DPI.BALANCE) + price);
						}
						
						buyer.s("Purchased for &6" + price + "&b!");
						seller.getList(DPI.MAIL).add("personal" + "%SPLIT%" + "&6System" + "%SPLIT%" + p.getDisplayName() + " &bpurchased your &6" + item.getType().toString() + "&b!");
						
						main.api.getDivSystem().getStack(DPI.CLOSET_ITEMS).remove(item);
						p.getInventory().addItem(modItem(item));
						getInv().remove(item);
						
					} else {
						buyer.err("You don't have enough money.");
					}
				}
				
			break;
		}
	}
	
	private ItemStack modItem(ItemStack i){
		ItemMeta im = i.getItemMeta();
		im.setLore(new ArrayList<String>());
		i.setItemMeta(im);
		return i;
	}
}