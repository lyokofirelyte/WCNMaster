package com.github.lyokofirelyte.Elysian.Events;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class ElyGameMode implements Listener, AutoRegister {
	
	private Elysian main;
	
	public ElyGameMode(Elysian i){
		main = i;
	}

	@EventHandler
	public void onGameMode(PlayerGameModeChangeEvent e){
		
		if (e.getNewGameMode().equals(GameMode.SPECTATOR) || e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)){
			return;
		}
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		DPI dpi;
		boolean creative = true;
		
		if (e.getNewGameMode().equals(GameMode.SURVIVAL)){
			creative = false;
		}
		
		dpi = creative ? DPI.CREATIVE_INVENTORY : DPI.SURVIVAL_INVENTORY;
		dp.set(dpi, new ArrayList<ItemStack>());
		
		for (ItemStack i : e.getPlayer().getInventory().getContents()){
			if (i != null){
				dp.getStack(dpi).add(i);
			}
		}
		
		for (ItemStack i : e.getPlayer().getInventory().getArmorContents()){
			if (i != null){
				dp.getStack(dpi).add(new ItemStack(i));
			}
		}
		
		ItemStack air = new ItemStack(Material.AIR);
		e.getPlayer().getInventory().setArmorContents(new ItemStack[]{air, air, air, air});
		e.getPlayer().getInventory().clear();
		dpi = !creative ? DPI.CREATIVE_INVENTORY : DPI.SURVIVAL_INVENTORY;
		
		for (ItemStack i : dp.getStack(dpi)){
			if (i != null){
				if (e.getPlayer().getInventory().firstEmpty() != -1){
					e.getPlayer().getInventory().addItem(i);
				} else {
					e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), i);
				}
			}
		}
	}
}