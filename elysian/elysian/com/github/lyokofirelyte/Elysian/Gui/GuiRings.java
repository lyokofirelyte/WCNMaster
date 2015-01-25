package com.github.lyokofirelyte.Elysian.Gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorageModule;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Commands.ElyRings;
import com.github.lyokofirelyte.Spectral.DataTypes.DRS;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRing;

import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.createItem;

public class GuiRings extends DivGui {
	
	private Elysian main;
	private Vector v;
	private int i = 45;
	private String name;
	
	public GuiRings(Elysian main, Vector v, String name){
		
		super(45, "&3Rings Destination");
		this.main = main;
		this.v = v;
		this.name = name;
	}
	
	@Override
	public void create(){
		
		int x = 0;
		Collection<DivinityStorageModule> rings = main.divinity.api.divManager.getMap(DivinityManager.ringsDir).values();
		List<DivinityStorageModule> ringz = new ArrayList<DivinityStorageModule>(rings);
		List<DivinityRing> systemRings = new ArrayList<DivinityRing>();
		List<DivinityRing> allianceRings = new ArrayList<DivinityRing>();
		
		for (DivinityStorageModule module : ringz){
			if (module.getBool(DRS.IS_ALLIANCE_OWNED)){
				allianceRings.add((DivinityRing) module);
			} else {
				systemRings.add((DivinityRing) module);
			}
		}
		
		for (DivinityRing ring : systemRings){
			if (!ring.name().equals(name)){
				addButton(x, createItem("&e" + ring.name(), new String[] { "&6&oTeleport to &b&o" + ring.name(), "&7&oSystem Ring"}, Material.STAINED_GLASS, 1, 0));
				x++;
			}
		}
		
		x = 27;
		int y = 0;
		
		for (DivinityRing ring : allianceRings){
			if (!ring.name().equals(name)){
				addButton(x, createItem("&e" + ring.name(), new String[] { "&6&oTeleport to &b&o" + ring.name(), "&7&oAlliance Ring"}, Material.STAINED_GLASS, 1, y));
				y++;
				x++;
				if (y > 16){
					y = 0;
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(Player p){
		
		try {
		
			if (slot <= i && (slot < 18 || slot > 26)){
				if (main.api.getDivRing(item.getItemMeta().getDisplayName().substring(2)).isInOperation()){
					return;
				}
				if (main.api.getDivRing(name).isInOperation()){
					return;
				}
				p.closeInventory();
				((ElyRings)main.api.getInstance(ElyRings.class)).calculate(p, v, this.item.getItemMeta().getDisplayName().substring(2), name, true);
			}
		
		} catch (Exception e){}
	}
}