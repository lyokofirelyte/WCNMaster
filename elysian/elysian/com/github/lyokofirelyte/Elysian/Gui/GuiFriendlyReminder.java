package com.github.lyokofirelyte.Elysian.Gui;

import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.createItem;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.ElyFriendlyReminder;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class GuiFriendlyReminder extends DivGui{
	
	private Elysian main;
	private DivGui parent;
	private ElyFriendlyReminder friendlyReminder;
	private DivinityPlayer player;

	public GuiFriendlyReminder(Elysian i, DivGui parent, DivinityPlayer dp) {
		super(9, "&5FRIENDLY REMINDERS");
		this.main = i;
		this.parent = parent;
		this.player = dp;
		this.friendlyReminder = (ElyFriendlyReminder) main.api.getInstance(ElyFriendlyReminder.class);
		
	}

	@Override
	public void create() {
		
		addButton(0, createItem("&aAnimal Killing", new String[] { "&3Toggle animal", "&3killing msgs" , friendlyReminder.getStatus(player, DPI.FR_FK_TOGGLE)}, Material.LEATHER, 1));
		addButton(1, createItem("&aCreeper Holes", new String[] { "&3Toggle creeper", "&3hole msgs" , friendlyReminder.getStatus(player, DPI.FR_CH_TOGGLE)}, Material.SKULL_ITEM, 1, 4));
		addButton(2, createItem("&aCrop Replanting", new String[] { "&3Toggle crop re-", "&3planting msgs" , friendlyReminder.getStatus(player, DPI.FR_CR_TOGGLE)}, Material.DIAMOND_HOE, 1));
		addButton(3, createItem("&aTree Replanting", new String[] { "&3Toggle tree re-", "&3planting msgs" , friendlyReminder.getStatus(player, DPI.FR_TR_TOGGLE)}, Material.SAPLING, 0));
		addButton(8, createItem("&bRETURN", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
	}

	@Override
	public void actionPerformed(Player p) {
		
		switch(slot){
			case 0: friendlyReminder.Toggle(main.api.getDivPlayer(p), DPI.FR_FK_TOGGLE); break;
			case 1: friendlyReminder.Toggle(main.api.getDivPlayer(p), DPI.FR_CH_TOGGLE); break;
			case 2: friendlyReminder.Toggle(main.api.getDivPlayer(p), DPI.FR_CR_TOGGLE); break;
			case 3: friendlyReminder.Toggle(main.api.getDivPlayer(p), DPI.FR_TR_TOGGLE); break;
			case 8: ((DivInvManager) main.api.getInstance(DivInvManager.class)).displayGui(p, this.parent); break;
		}	
		
		switch (slot){
			case 0: case 1: case 2: case 3: create(); break;
		}
	}
}