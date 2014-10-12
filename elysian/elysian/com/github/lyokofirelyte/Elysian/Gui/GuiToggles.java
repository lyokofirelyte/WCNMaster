package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Elysian.Elysian;

import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.createItem;

public class GuiToggles extends DivGui {
	
	private Elysian main;
	private DivGui parent;
	
	public GuiToggles(Elysian main, DivGui parent){
		
		super(18, "&4TOGGLES");
		this.main = main;
		this.parent = parent;
		
	}
	
	@Override
	public void create(){

		this.addButton(0, createItem("&eSIDEBOARD", new String[] { "&6The scoreboard" }, Material.GLOWSTONE));
		this.addButton(1, createItem("&bPOKES", new String[] { "&9Toggle pokes" }, Material.STICK));
		this.addButton(2, createItem("&dFIREWORKS", new String[] { "&8Toggle paragon fireworks" }, Material.FIREWORK));
		this.addButton(3, createItem("&3FILTER", new String[] { "&aToggle chat censoring"}, Material.CAKE));
		this.addButton(4, createItem("&4DEATH LOCATIONS", new String[] { "&bToggle displaying death", "&blocation on death" }, Material.ARROW));
		this.addButton(5, createItem("&bHOME PARTICLES", new String[] { "&3Change home particle effects"}, Material.FIREBALL));
		this.addButton(6, createItem("&5FRIENDLY REMINDERS", new String[] { "&bChange which reminder", "&bmessages you see" }, Material.BOOK_AND_QUILL));
		this.addButton(8, createItem("&b( e l y s i a n )", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
		
	}
	
	@Override
	public void actionPerformed(Player p){
		
		DivInvManager invManager = (DivInvManager) main.api.getInstance(DivInvManager.class);
		
		switch (this.slot){
		
			case 0:
				p.performCommand("toggle scoreboard");
			break;
				
			case 1:
				p.performCommand("toggle pokes");
			break;
				
			case 2:
				p.performCommand("toggle fireworks");
			break;
				
			case 3:
				p.performCommand("toggle chat_filter");
			break;
				
			case 4:
				p.performCommand("toggle deathlocs");
			break;
				
			case 5:
				p.performCommand("toggle particles");
			break;
			
			case 6:
				invManager.displayGui(p, new GuiFriendlyReminder(main, this, main.api.getDivPlayer(p)));
			break;
			
			case 8:
				invManager.displayGui(p, this.parent);
			break;
			
		}
	}
}