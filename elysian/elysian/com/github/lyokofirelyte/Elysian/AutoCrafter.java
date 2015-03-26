package com.github.lyokofirelyte.Elysian;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class AutoCrafter implements AutoRegister, Listener {
	
	@Getter @Setter
	private Elysian main;
	
	@Getter @Setter
	private Map<String, YamlConfiguration> yaml = new HashMap<String, YamlConfiguration>();
	
	@Getter @Setter
	private Map<String, Location> players = new HashMap<String, Location>();
	
	public AutoCrafter(Elysian i){
		main = i;
		load();
	}
	
	@DivCommand(aliases = { "autocrafter" }, desc = "AutoCrafter Command", help = "/autocrafter", player = true, min = 1)
	public void onAutoCraft(Player p, String[] args){
		
		switch (args[0]){
		
			case "add":
				
				if (!getPlayers().containsKey(p.getName())){
					getPlayers().put(p.getName(), p.getLocation());
				}
				
				DivinityUtilsModule.s(p, "Left click a chest to configure an auto-crafter.");
			
			break;
			
			case "cancel":
				
				getPlayers().remove(p.getName());
				
			break;
			
			default:
				
				DivinityUtilsModule.s(p, "/autocrafter add, /autocrafter cancel");
				
			break;
		}
	}
	
	@EventHandler
	public void onOpen(PlayerInteractEvent e){

		if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getClickedBlock().getType().equals(Material.CHEST)){
			Player p = (Player) e.getPlayer();
			if (getPlayers().containsKey(p.getName())){
				getPlayers().put(p.getName(), e.getClickedBlock().getLocation());
				e.setCancelled(true);
				Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.WORKBENCH);
				Location l = e.getClickedBlock().getLocation();
				if (yaml.containsKey(l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ())){
					inv.addItem(yaml.get(l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ()).getItemStack("Result"));
					int ii = 1;
					for (ItemStack i : (List<ItemStack>) yaml.get(l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ()).get("Items")){
						inv.setItem(ii, i);
						ii++;
					}
				}
				p.openInventory(inv);
			}
		}
	}
	
	@EventHandler @SneakyThrows
	public void onClose(InventoryCloseEvent e){
		
		if (getPlayers().containsKey(e.getPlayer().getName())){
			
			YamlConfiguration yaml = new YamlConfiguration();
			List<ItemStack> items = new ArrayList<ItemStack>();
			
			for (int i = 1; i < 10; i++){
				if (e.getInventory().getContents()[i] != null){
					items.add(e.getInventory().getContents()[i]);
				} else {
					items.add(new ItemStack(Material.AIR));
				}
			}
			
			yaml.set("Result", (e.getInventory().getContents()[0] != null ? e.getInventory().getContents()[0] : new ItemStack(Material.AIR)));
			yaml.set("Items", items);
			
			Location l = getPlayers().get(e.getPlayer().getName());
			
			this.yaml.put(l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ(), yaml);
			yaml.save(new File("./plugins/Divinity/autocrafter/" + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + ".yml"));
			
			for (ItemStack i : items){
				e.getPlayer().getInventory().addItem(i);
			}
			
			e.getInventory().clear();
			((Player) e.getPlayer()).updateInventory();
			
			getPlayers().remove(e.getPlayer().getName());
		}
	}
	
	public void load(){
		
		File file = new File("./plugins/Divinity/autocrafter");
		file.mkdirs();
		
		for (File f : file.listFiles()){
			yaml.put(f.getName().replace(".yml", ""), YamlConfiguration.loadConfiguration(f));
		}
	}
	
	@SneakyThrows
	public void save(){
		
		for (String f : yaml.keySet()){
			yaml.get(f).save(new File("./plugins/Divinity/autocrafter/" + f + ".yml"));
		}
	}
}