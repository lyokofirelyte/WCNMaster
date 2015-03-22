package com.github.lyokofirelyte.MM;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class MMClasses {
		
	public void addBonus(Player p, String type){
		
		p.getInventory().addItem(new ItemStack(Material.POTION, 1, (short)16417));
		p.getInventory().addItem(new ItemStack(Material.POTION, 2, (short)16421));
		
		switch(type){
		case "mage":
			p.getInventory().addItem(new ItemStack(Material.COOKED_CHICKEN, 16));
			break;
			
		case "pyro":
			p.getInventory().addItem(new ItemStack(Material.GRILLED_PORK, 16));
			break;
			
		case "barbarian":
			p.getInventory().addItem(new ItemStack(Material.COOKED_CHICKEN, 16));
			break;
			
		case "melee":
			p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
			break;
			
		case "healer":
			p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 4));
			break;
			
		case "ranger":
			p.getInventory().addItem(new ItemStack(Material.COOKED_MUTTON, 16));
			break;

		}
	}
	
	
	public void setClass(Player p, String type){
		
		p.getInventory().clear();
		
		for(PotionEffect pe : p.getActivePotionEffects()){
			p.removePotionEffect(pe.getType());
		}
		
		p.getInventory().setArmorContents(null);
		
		ItemStack[] armor = null;
		ItemStack[] contents = null;
		ItemStack health = new ItemStack(Material.POTION, 3, (short)8229);
		ItemStack milk = new ItemStack(Material.MILK_BUCKET, 3);
		
		switch(type.toLowerCase()){
		
			case "mage":
				ItemStack chest = new ItemStack(Material.GOLD_CHESTPLATE);
				chest.addUnsafeEnchantment(Enchantment.THORNS, 2);
				armor = new ItemStack[]{new ItemStack(Material.GOLD_HELMET), chest, new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.GOLD_BOOTS)};
				ItemStack fireprot = new ItemStack(Material.POTION, 8, (short)16387);
				ItemStack strength = new ItemStack(Material.POTION, 8, (short)16425);
				ItemStack food = new ItemStack(Material.COOKED_CHICKEN, 32);
				ItemStack stick = new ItemStack(Material.STICK);
				stick.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
				stick.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 2);
				stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

				
				contents = new ItemStack[]{stick, fireprot, strength, food, health, milk};

				break;
		
			case "melee":
				
				armor = new ItemStack[]{new ItemStack(Material.CHAINMAIL_HELMET),  new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS)};
				ItemStack leaping = new ItemStack(Material.POTION, 8, (short)16395);
				food = new ItemStack(Material.COOKED_BEEF, 32);
				ItemStack sword = new ItemStack(Material.IRON_SWORD);

				
				contents = new ItemStack[]{sword, leaping, food, health, milk};
				
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 1000, 0));
				
				break;
				
			case "ranger":
				
				armor = new ItemStack[]{new ItemStack(Material.IRON_HELMET),  new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS)};
				food = new ItemStack(Material.COOKED_MUTTON, 32);
				ItemStack bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
				bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
				ItemStack arrow = new ItemStack(Material.ARROW);

				contents = new ItemStack[]{arrow, bow, food, health, new ItemStack(Material.WOOD_SWORD), milk};
							
				break;
				
			case "healer":
				
				armor = new ItemStack[]{new ItemStack(Material.LEATHER_HELMET),  new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS)};
				ItemStack damage = new ItemStack(Material.POTION, 8, (short)16396);
				ItemStack regen = new ItemStack(Material.POTION, 8, (short)8193);
				health = new ItemStack(Material.POTION, 8, (short)8229);
				sword = new ItemStack(Material.GOLD_SWORD);
				sword.addUnsafeEnchantment(Enchantment.DURABILITY, 120);

				food = new ItemStack(Material.GOLDEN_APPLE, 16);

				for(ItemStack i : armor){
					i.addEnchantment(Enchantment.DURABILITY, 1);
				}
				
				contents = new ItemStack[]{regen, health, food, damage, sword, milk};
				
				break;
				
			case "barbarian":
				
				armor = new ItemStack[]{new ItemStack(Material.IRON_HELMET),  new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS)};
				food = new ItemStack(Material.COOKED_FISH, 32);
				ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
				axe.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
				
				
				contents = new ItemStack[]{food, axe, health, milk};
				
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 60 * 1000, 2));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 60 * 1000, 0));

				
				break;
				
			case "pyro":
				
				armor = new ItemStack[]{new ItemStack(Material.IRON_HELMET),  new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS)};
				food = new ItemStack(Material.GRILLED_PORK, 32);
				ItemStack flint = new ItemStack(Material.FLINT_AND_STEEL);
				ItemStack tnt = new ItemStack(Material.TNT, 64);
				damage = new ItemStack(Material.POTION, 8, (short)16396);

				contents = new ItemStack[]{flint, tnt, food, damage, health, new ItemStack(Material.WOOD_SWORD), milk};

				p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60 * 1000, 0));

				break;
		
		
		}
		
		for(ItemStack i : armor){
			i.addUnsafeEnchantment(Enchantment.DURABILITY, 120);
		}
		for(ItemStack i : contents){
			String name = i.getType().toString().toLowerCase();
			if(name.contains("sword") || name.contains("axe") || name.contains("and steel") || name.contains("stick") || name.contains("bow")){
				i.addUnsafeEnchantment(Enchantment.DURABILITY, 120);
			}
		}
		
		p.getInventory().addItem(contents);
		p.getInventory().setArmorContents(armor);

		
		
	}
	
	
	
}

