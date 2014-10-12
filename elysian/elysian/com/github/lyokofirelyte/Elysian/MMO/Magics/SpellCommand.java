package com.github.lyokofirelyte.Elysian.MMO.Magics;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Gui.GuiLunarSpells;
import com.github.lyokofirelyte.Elysian.Gui.GuiSolarSpells;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class SpellCommand implements AutoRegister {
	
	private Elysian main;
	private DivInvManager invManager;

	public SpellCommand(Elysian i) {
		main = i;
		invManager = (DivInvManager) main.api.getInstance(DivInvManager.class);
	}

	@DivCommand(aliases = {"solar"}, desc = "Open the solar spellbook", help = "/solar", player = true)
	public void onSolar(Player p, String[] args){
		invManager.displayGui(p, new GuiSolarSpells(main));
	}
	
	@DivCommand(aliases = {"lunar"}, desc = "Open the lunar spellbook", help = "/lunar", player = true)
	public void onLunar(Player p, String[] args){
		invManager.displayGui(p, new GuiLunarSpells(main));
	}
}