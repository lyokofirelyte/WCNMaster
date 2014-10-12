package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.*;

public class GuiRoot extends DivGui {
	
	private Elysian main;
	
	public GuiRoot(Elysian main){
		
		super(9, "&b( e l y s i a n )");
		this.main = main;
	}
	
	@Override
	public void create(){
		
		addButton(0, createItem("&aCHAT", new String[] { "&3Chat Options" }, Material.INK_SACK, 1, 9));
		addButton(1, createItem("&4TOGGLES", new String[] { "&cToggle Options" }, Material.INK_SACK, 1, 1));
		addButton(2, createItem("&3TRADE &f][&3 CENTRE", new String[] { "&3General Store Trading" }, Material.INK_SACK, 1, 12));
		addButton(3, createItem("&4INCINERATOR", new String[] { "&cThrow away items" }, Material.INK_SACK, 1, 10));
		addButton(4, createItem("&bPATROLS", new String[] { "&dPatrol Menu", "&4[ OFFLINE ]" }, Material.INK_SACK, 1, 11));
		addButton(5, createItem("&eLOGOFF", new String[] { "&6Leave the game" }, Material.INK_SACK));
		addButton(8, createItem("&bCLOSE", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
	}
	
	@Override
	public void actionPerformed(final Player p){
		
		DivInvManager invManager = (DivInvManager) main.api.getInstance(DivInvManager.class);
	
		switch (slot){
			
			case 0:
				invManager.displayGui(p, new GuiChat(main, this));
			break;
				
			case 1:
				invManager.displayGui(p, new GuiToggles(main, this));
			break;
			
			case 2:
				invManager.displayGui(p, main.closets.get(0));
			break;
			
			case 3:
				p.openInventory(Bukkit.createInventory(null, 54, main.AS("&c&oINCENERATOR")));
			break;
			
			case 5:
				p.kickPlayer(main.AS("&3&oLogout Success!"));
			break;
			
			case 8:
				p.closeInventory();
			break;
			
		}
	}
}