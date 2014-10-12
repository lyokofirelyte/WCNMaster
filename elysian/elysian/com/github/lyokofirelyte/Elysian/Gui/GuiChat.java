package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.*;

public class GuiChat extends DivGui {
	
	private Elysian main;
	private DivGui parent;
	
	public GuiChat(Elysian main, DivGui parent){
		super(9, "&4CHAT");
		this.main = main;
		this.parent = parent;
	}
	
	@Override
	public void create(){
		
		addButton(0, createItem("&bGLOBAL COLOR", new String[] { "&9Change global color" }, Material.INK_SACK, 1, 14));
		addButton(1, createItem("&bPM COLOR", new String[] { "&9Change pm color" }, Material.INK_SACK, 1, 10));
		addButton(2, createItem("&bALLIANCE COLOR", new String[] { "&9Change alliance color" }, Material.INK_SACK, 1, 11));
		addButton(3, createItem("&bTIME CODES", new String[] { "&9Toggle chat timecodes" }, Material.INK_SACK, 1, 12));
		addButton(8, createItem("&b( e l y s i a n )", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
	}
	
	@Override
	public void actionPerformed(Player p){
		
		DivInvManager invManager = (DivInvManager) main.api.getInstance(DivInvManager.class);
		
		switch (this.slot){
		
		case 0:
			
			invManager.displayGui(p, new GuiColourSelection(main, "global_color", this));
			break;
			
		case 1:
			
			invManager.displayGui(p, new GuiColourSelection(main, "pm_color", this));
			break;
			
		case 2:
			
			invManager.displayGui(p, new GuiColourSelection(main, "alliance_color", this));
			break;
			
		case 3:
			
			p.performCommand("toggle timecode");
			break;
			
		case 8:
			
			invManager.displayGui(p, parent);
			break;
			
		}
	}
}