package com.github.lyokofirelyte.Elysian;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.github.lyokofirelyte.Divinity.Divinity;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Divinity.Manager.RecipeHandler;
import com.github.lyokofirelyte.Elysian.Events.ElyLogger;
import com.github.lyokofirelyte.Elysian.Gui.GuiCloset;
import com.github.lyokofirelyte.Elysian.Gui.GuiRoot;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.Patrols.ElyPatrol;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyTask;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class ElySetup {

	private Elysian main;
	
	public ElySetup(Elysian i){
		main = i;
	}
	
	public void start(){
		
		main.divinity = (Divinity) Bukkit.getPluginManager().getPlugin("Divinity");
		main.we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		main.api = main.divinity.api;
		
		main.api.registerAll(main);
		
		closet();
		tasks();
		rec();
		
		main.numerals = new ArrayList<String>(YamlConfiguration.loadConfiguration(main.getResource("numerals.yml")).getStringList("Numerals"));

		try {
			main.api.loadAllFiles(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		main.divinity.api.sheets.fetch(false, true);
	}
	
	private void tasks(){
		main.tasks.put(ElyTask.ANNOUNCER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, (ElyAnnouncer) main.api.getInstance(ElyAnnouncer.class), 0L, 24000L));
		main.tasks.put(ElyTask.MMO_BLOCKS, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, (ElyMMOCleanup) main.api.getInstance(ElyMMOCleanup.class), 0L, 432000L));
		main.tasks.put(ElyTask.LOGGER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, (ElyLogger) main.api.getInstance(ElyLogger.class), 300L, 300L));
		main.tasks.put(ElyTask.WATCHER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, (ElyWatch) main.api.getInstance(ElyWatch.class), 500L, 500L));
		main.tasks.put(ElyTask.WEBSITE, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.divinity.api.web, 100L, 100L));
		main.tasks.put(ElyTask.AUTO_SAVE, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, (ElyAutoSave) main.api.getInstance(ElyAutoSave.class), 24000L, 24000L));
		main.tasks.put(ElyTask.PATROL,  Bukkit.getScheduler().scheduleSyncRepeatingTask(main, (ElyPatrol) main.api.getInstance(ElyPatrol.class), 40L, 144000L));
	}
	
	private void closet(){
		
		for (int i = 0; i < 5; i++){
			main.closets.put(i, new GuiCloset(main, i == 0 ? new GuiRoot(main) : main.closets.get(i-1)));
		}
		
		for (int i = 0; i < 5; i++){
			main.closets.get(i).create();
		}
		
		for (ItemStack i : main.api.getDivSystem().getStack(DPI.CLOSET_ITEMS)){
			for (int x = 0; x < 5; x++){
				if (main.closets.get(x).getInv().firstEmpty() != -1){
					main.closets.get(x).getInv().addItem(i);
					break;
				}
			}
		}
	}
	
	private void rec(){
		
		Potion splash = new Potion(PotionType.INSTANT_HEAL, 1);//poison 1
		splash.setSplash(true);
		 
		ItemStack i = splash.toItemStack(1);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(main.AS("&4&oVAMPYRE VIAL"));
		meta.setLore(Arrays.asList(main.AS("&c&oDrink up!")));
		i.setItemMeta(meta);

		ShapedRecipe r = new ShapedRecipe(i).shape(
			"000",
			"yay",
			"zzz"
		);
		
		RecipeHandler rh = new RecipeHandler(r);
		rh.setIngredient('0', new ItemStack(Material.ROTTEN_FLESH));
		rh.setIngredient('y', new ItemStack(Material.REDSTONE));
		rh.setIngredient('a', new ItemStack(Material.APPLE));
		rh.setIngredient('z', new ItemStack(Material.SPIDER_EYE));
		main.getServer().addRecipe(rh.getShapedRecipe());
		
		
		splash = new Potion(PotionType.POISON, 1);
		splash.setSplash(true);
		 
		i = splash.toItemStack(1);
		meta = i.getItemMeta();
		meta.setDisplayName(main.AS("&b&oFLASH!"));
		meta.setLore(Arrays.asList(main.AS("&9&oOh I wonder where you'll go...")));
		i.setItemMeta(meta);

		r = new ShapedRecipe(i).shape(
			"fff",
			"fff",
			"fff"
		);
		
		rh = new RecipeHandler(r);
		rh.setIngredient('f', new ItemStack(Material.FEATHER));
		main.getServer().addRecipe(rh.getShapedRecipe());
		
		
		i = DivInvManager.createItem("SUPERCOBBLE", new String[] {"&6&oConsumed by magic spells"}, Enchantment.DURABILITY, 10, Material.COBBLESTONE, 9);

		r = new ShapedRecipe(i).shape(
			"fff",
			"fff",
			"fff"
		);
		
		rh = new RecipeHandler(r);
		rh.setIngredient('f', new ItemStack(Material.COBBLESTONE));
		main.getServer().addRecipe(rh.getShapedRecipe());
		
		r = new ShapedRecipe(DivInvManager.createItem(main.AS("&5&o))( &f&odRaX &5&o)(("), new String[] {"&6&o(HAOS DEVICE", "&a&o7000/7000"}, Enchantment.DURABILITY, 10, Material.ARROW, 1)).shape(
				"0f1",
				"fff",
				"234"
		);
			
		rh = new RecipeHandler(r);
		rh.setIngredient('f', i);
		rh.setIngredient('0', DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 14));
		rh.setIngredient('1', DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 13));
		rh.setIngredient('2', DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 0));
		rh.setIngredient('3', DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 12));
		rh.setIngredient('4', DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, 5));
		main.getServer().addRecipe(rh.getShapedRecipe());
	}
}