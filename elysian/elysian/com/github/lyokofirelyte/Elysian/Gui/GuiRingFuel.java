package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Database.DRS;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityRing;
import com.github.lyokofirelyte.Empyreal.Gui.DivGui;

public class GuiRingFuel extends DivGui {
	
	private Elysian main;
	private String name;
	private DivinityRing ring;
	
	public GuiRingFuel(Elysian main,  DivinityRing ring){
		
		super(54, ring.getName() + " Fuel");
		this.main = main;
		this.ring = ring;
	}
	
	@Override
	public void create(){
		
		int x = 0;
		
		for (ItemStack i : ring.getStack(DRS.FUEL)){
			addButton(x, i);
			x++;
		}
	}
	
	@Override
	public void actionPerformed(Player p){
		
		if (click != null){
			click.setCancelled(false);
		}
		
		if (drag != null){
			drag.setCancelled(false);
		}
		
		if (interact != null){
			interact.setCancelled(false);
		}
		
		if (move != null){
			move.setCancelled(false);
		}
	}
}