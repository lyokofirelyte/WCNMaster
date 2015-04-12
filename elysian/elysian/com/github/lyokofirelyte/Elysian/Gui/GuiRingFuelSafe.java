package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityRing;
import com.github.lyokofirelyte.Empyreal.Gui.DivGui;

public class GuiRingFuelSafe extends DivGui {
	
	private Elysian main;
	private String name;
	private DivinityRing ring;
	
	public GuiRingFuelSafe(Elysian main,  DivinityRing ring){
		
		super(54, "Deposit to " + ring.getName());
		this.main = main;
		this.ring = ring;
	}
	
	@Override
	public void create(){}
	
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