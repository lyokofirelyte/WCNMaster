package com.github.lyokofirelyte.Elysian.MMO.Magics;

import lombok.Getter;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Gui.GuiLunarSpells;
import com.github.lyokofirelyte.Elysian.Gui.GuiSolarSpells;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Gui.DivInvManager;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class SpellCommand implements AutoRegister<SpellCommand> {
	
	private Elysian main;
	private DivInvManager invManager;
	
	@Getter
	private SpellCommand type = this;

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