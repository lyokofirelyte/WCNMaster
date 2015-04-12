package com.github.lyokofirelyte.Creator.Events.Listeners.Entity;

import java.util.HashMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Creator.VTParser;
import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.github.lyokofirelyte.Creator.Identifiers.VTMap;

public class ItemSpawn extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public ItemSpawn(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "ItemSpawn.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onSpawn(ItemSpawnEvent e){
		
		if (getList("Worlds").contains(e.getEntity().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "ItemSpawn.yml", "main", getList("main"), e.getEntity().getLocation(), getCustoms(e), e.getEntityType().name()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(ItemSpawnEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		ItemStack i = e.getEntity().getItemStack();
		int x = 0;
		
		map.put("<spawneditem:name>", i.getType().name().toLowerCase());
		map.put("<spawneditem:displayname>", i.hasItemMeta() && i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : "none");
		map.put("<spawneditem:amount>", i.getAmount() + "");
		
		if (i.hasItemMeta() && i.getItemMeta().hasLore()){
			map.put("<spawneditem:lore:amount>", i.getItemMeta().getLore().size() + "");
			for (String lore : i.getItemMeta().getLore()){
				map.put("<spawneditem:lore:" + x + ">", lore);
				x++;
			}
		} else {
			map.put("<spawneditem:lore:amount>", "0");
		}
		
		x = 0;
		
		if (i.hasItemMeta() && i.getItemMeta().hasEnchants()){
			map.put("<spawneditem:enchant:amount>", i.getItemMeta().getEnchants().size() + "");
			for (Enchantment ench : i.getItemMeta().getEnchants().keySet()){
				map.put("<spawneditem:enchant:" + x + ">", ench.getName().toLowerCase() + "_" + i.getItemMeta().getEnchants().get(ench));	
				x++;
			}
		} else {
			map.put("<spawneditem:enchant:amount>", "0");
		}

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}