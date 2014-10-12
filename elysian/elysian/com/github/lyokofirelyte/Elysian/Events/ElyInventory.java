/*package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyInventory implements Listener {

	private Elysian main;
	
	public ElyInventory(Elysian i){
		main = i;
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent e){
		
		if (e.getPlayer() instanceof Player){
			if (e.getInventory().equals(e.getPlayer().getInventory())){
				Player p = (Player) e.getPlayer();
				DivinityPlayer dp = main.api.getDivPlayer(p);
				Inventory inv = Bukkit.createInventory(null, InventoryType.)
			}
		}
	}
}*/