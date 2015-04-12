package com.github.lyokofirelyte.Elysian.Events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;

public class HolidayStuff implements AutoRegister<HolidayStuff>, Listener {

	private Elysian main;
	
	@Getter
	private HolidayStuff type = this;
	
	Random rand = new Random();
	List<Material> valid = new ArrayList<Material>();
	List<Material> invalid = Arrays.asList(
		Material.BEDROCK,
		Material.MOB_SPAWNER,
		Material.MONSTER_EGG,
		Material.MONSTER_EGGS,
		Material.BARRIER,
		Material.BED_BLOCK,
		Material.COMMAND,
		Material.BURNING_FURNACE,
		Material.PISTON_BASE,
		Material.PISTON_EXTENSION,
		Material.PISTON_MOVING_PIECE,
		Material.PISTON_STICKY_BASE,
		Material.LOCKED_CHEST
	);
	
	public HolidayStuff(Elysian i){
		main = i;
		for (Material m : Material.values()){
			if (!invalid.contains(m)){
				valid.add(m);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onPotionSplash(PotionSplashEvent e){
		
		if (e.getPotion() != null && e.getPotion().getWorld().getName().equals("world")){
			
			Player thrower = null;
			ItemStack i = e.getPotion().getItem();
			boolean succ = false;
			
			if (i.hasItemMeta() && i.getItemMeta().hasLore()){
				
				if (i.getItemMeta().getLore().get(0).contains("HOLIDAY BOMB")){
					for (LivingEntity ent : e.getAffectedEntities()){
						if (ent instanceof Player){
							if (!i.getItemMeta().getLore().get(1).contains(((Player) ent).getName())){
								succ = true;
								break;
							} else {
								thrower = ((Player) ent);
							}
						}
					}
					
					if (succ){
						
						Location loc = e.getPotion().getLocation();
						ParticleEffect.ENCHANTMENT_TABLE.display(1, 1, 1, 0, 1000, e.getPotion().getLocation(), 16);
						
						for (int ii = 0; ii < 2; ii++){
							ParticleEffect.CRIT.display(2, 2, 2, 0, 100, e.getPotion().getLocation(), 16);
							ParticleEffect.INSTANT_SPELL.display(2, 2, 2, 0, 100, e.getPotion().getLocation(), 16);
							ParticleEffect.MAGIC_CRIT.display(2, 2, 2, 0, 100, e.getPotion().getLocation(), 16);
						}
						
						ParticleEffect.RED_DUST.display(0, 10, 0, 1, 1000, e.getPotion().getLocation(), 16);
						
						
						for (int ii = 0; ii < 5; ii++){
							int place = rand.nextInt(5);
							place = place*(rand.nextInt(2) == 0 ? 1 : -1);
							try {
								loc.getWorld().dropItem(new Location(loc.getWorld(), loc.getBlockX()+place, loc.getBlockY(), loc.getBlockZ()+place), new ItemStack(valid.get(rand.nextInt(valid.size())), 1));
							} catch (Exception ee){}
						}
						
					} else {
						e.setCancelled(true);
						if (thrower != null){
							main.s(thrower, "&c&oYou must throw this at someone else!");
						}
					}
				}
			}
		}
	}
}