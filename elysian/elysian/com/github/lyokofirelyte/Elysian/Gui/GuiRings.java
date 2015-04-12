package com.github.lyokofirelyte.Elysian.Gui;

import static com.github.lyokofirelyte.Empyreal.Gui.DivInvManager.createItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Commands.ElyRings;
import com.github.lyokofirelyte.Elysian.api.RingsType;
import com.github.lyokofirelyte.Empyreal.Database.DRS;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityRing;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Gui.DivGui;
import com.github.lyokofirelyte.Empyreal.Gui.DivInvManager;

public class GuiRings extends DivGui {
	
	private Elysian main;
	private Vector v = new Vector();
	private String name = "ring";
	private RingsType type = RingsType.SYSTEM;
	
	public GuiRings(Elysian main, Vector v, String name, RingsType type){
		
		super(type.equals(RingsType.MENU) ? 9 : 45, "&3Rings Destination");
		this.main = main;
		this.v = v;
		this.name = name;
		this.type = type;
	}
	
	@Override
	public void create(){
		
		Collection<DivinityStorageModule> rings = new ArrayList<DivinityStorageModule>();
		
		for (DivinityStorageModule m : main.api.getOnlineModules().values()){
			if (m.getTable().equals("rings")){
				rings.add(m);
			}
		}
		
		List<DivinityStorageModule> ringz = new ArrayList<DivinityStorageModule>(rings);
		List<DivinityRing> systemRings = new ArrayList<DivinityRing>();
		List<DivinityRing> allianceRings = new ArrayList<DivinityRing>();
		List<DivinityRing> serverRings = new ArrayList<DivinityRing>();
		int x = 0;
		
		for (DivinityStorageModule module : ringz){
			if (module.getBool(DRS.IS_ALLIANCE_OWNED)){
				allianceRings.add((DivinityRing) module);
			} else if (module.getBool(DRS.IS_SERVER_RING)){
				serverRings.add((DivinityRing) module);
			} else {
				systemRings.add((DivinityRing) module);
			}
		}
		
		switch (type){
		
			case MENU:
				
				addButton(0, createItem("&7&oSYSTEM", new String[] { "&3&oSystem Ring Selection"}, Material.STAINED_GLASS, 1, 1));
				addButton(4, createItem("&b&oALLIANCE", new String[] { "&3&oAlliance Ring Selection"}, Material.STAINED_GLASS, 1, 2));
				addButton(8, createItem("&6&oSERVER", new String[] { "&3&oServer Ring Selection"}, Material.STAINED_GLASS, 1, 3));
				
			break;
			
			default:
				
				int y = 1;
				String disp = "&7&o" + (type.equals(RingsType.SYSTEM) ? "System" : type.equals(RingsType.ALLIANCE) ? "Alliance" : "Server") + " Ring";

				for (DivinityRing ring : type.equals(RingsType.SYSTEM) ? systemRings : type.equals(RingsType.ALLIANCE) ? allianceRings : serverRings){
					if (!ring.getName().equals(name)){
						addButton(x, createItem("&e" + ring.getName(), new String[] { "&6&oTeleport to &b&o" + ring.getName(), disp}, Material.STAINED_GLASS, 1, y));
						y++;
						x++;
						if (y > 16){
							y = 0;
						}
					}
				}
				
			break;
		}
		
		if (!type.equals(RingsType.MENU)){
			addButton(getInv().getSize()-1, createItem("&bPrevious", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
		}
	}
	
	@Override
	public void actionPerformed(Player p){
		
		if (item != null){
			if (slot == getInv().getSize()-1 && !type.equals(RingsType.MENU)){
				((DivInvManager) main.api.getInstance(DivInvManager.class)).displayGui(p, new GuiRings(main, v, name, RingsType.MENU));
			} else {
				
				switch (type){
				
					case MENU:
						((DivInvManager) main.api.getInstance(DivInvManager.class)).displayGui(p, new GuiRings(main, v, name, RingsType.valueOf(item.getItemMeta().getDisplayName().substring(4))));
					break;
					
					case SYSTEM: case ALLIANCE:
						
						if (!main.api.getDivRing(item.getItemMeta().getDisplayName().substring(2)).isInOperation() && !main.api.getDivRing(name).isInOperation()){
							p.closeInventory();
							((ElyRings)main.api.getInstance(ElyRings.class)).calculate(p, v, this.item.getItemMeta().getDisplayName().substring(2), name, true);
						}
						
					break;
					
					case SERVER:
						
						List<Player> toTake = new ArrayList<Player>(Arrays.asList(p));
						p.closeInventory();
						
						for (Entity e : p.getNearbyEntities(5D, 5D, 5D)){
							if (e instanceof Player){
								toTake.add((Player) e);
							}
						}
						
						for (Player player : toTake){
							main.api.sendToServer(player.getName(), item.getItemMeta().getDisplayName().substring(2).toLowerCase());
						}
						
					break;
				}
			}
		}
	}
}