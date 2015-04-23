package com.github.lyokofirelyte.Elysian.MMO.Magics;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.SkillExpGainEvent;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.ElySkill;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;

public enum Spell {

	NORMAL_ARROW("NORMAL_ARROW", ElySkill.SOLAR, 0, 0),
	FIRE_BLAST("FIRE_BLAST", ElySkill.SOLAR, 0, 2 * 1000L),
	KERSPLASH("KERSPLASH", ElySkill.SOLAR, 5, 2 * 1000L),
	RAPID_FIRE("RAPID_FIRE", ElySkill.SOLAR, 10, 5 * 1000L),
	EARTH_BOUND("EARTH_BOUND", ElySkill.SOLAR, 15, 5 * 1000L),
	DIAMOND_BLITZ("DIAMOND_BLITZ", ElySkill.SOLAR, 20, 10 * 1000L),
	
	DEFLECT("DEFLECT", ElySkill.LUNAR, 0, 10 * 1000L),
	RENEWAL("RENEWAL", ElySkill.LUNAR, 10, 10 * 60 * 1000L);
	
	Spell(String type, ElySkill skill, int level, long cooldown){
		this.type = type;
		this.skill = skill;
		this.level = level;
		this.cooldown = cooldown;
	}
	
	String type;
	ElySkill skill;
	int level;
	long cooldown;
	
	public void cast(Elysian main, final Player shooter, boolean force){
		
		SpellTasks tasks = new SpellTasks(main);
		
		DivinityPlayer dp = main.api.getDivPlayer(shooter);
		ItemStack toRemove = null;
		boolean cont = false;
		boolean found = false;
		
		if (dp.hasLevel(skill, level) || force){
			
			if (dp.getLong(type + "_COOLDOWN") > System.currentTimeMillis() && !force){
				dp.err((dp.getLong(type + "_COOLDOWN") - System.currentTimeMillis())/1000 + " seconds cooldown!");
				return;
			}
			
			for (String s : shooter.getItemInHand().getItemMeta().getLore()){
				if (s.contains(shooter.getName())){
					cont = true;
				}
			}
			
			for (ItemStack i : shooter.getInventory().getContents()){
				if (!found && i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
					for (String lore : i.getItemMeta().getLore()){
						if (lore.contains("Consumed by magic spells")){
							if (i.getAmount() > 1){
								i.setAmount(i.getAmount() -1);
							} else {
								toRemove = i;
							}
							found = true;
							shooter.updateInventory();
							break;
						}
					}
				}
			}
			
			if (toRemove != null){
				shooter.getInventory().removeItem(toRemove);
				shooter.updateInventory();
			}
			
			if ((cont && found) || force){
		
				Location from = shooter.getLocation();
				from.setY(from.getY() + 1.5);
				final Location frontLocation = from.add(from.getDirection());
				dp.set(type + "_COOLDOWN", System.currentTimeMillis() + cooldown);
				
				switch (type){
				
					case "FIRE_BLAST": case "KERSPLASH": case "RAPID_FIRE":
						
						SmallFireball fireball = (SmallFireball) shooter.getWorld().spawnEntity(frontLocation, EntityType.SMALL_FIREBALL);
						fireball.setShooter(shooter);
						fireball.setVelocity(shooter.getLocation().getDirection().multiply(1.4));
						main.spellTasks.put(fireball, type + "%" + new Random().nextInt(1000));
						
						if (type.equals("FIRE_BLAST")){
							main.api.repeat(tasks, "fireball", 0L, 1L, main.spellTasks.get(fireball), main, fireball);
						} else if (type.equals("KERSPLASH")){
							main.api.repeat(tasks, "kersplash", 0L, 1L, main.spellTasks.get(fireball), main, fireball);
						} else {
							main.api.repeat(tasks, "rapidFire", 0L, 1L, main.spellTasks.get(fireball), main, fireball);
						}
						
					break;
					
					case "EARTH_BOUND":
						
						FallingBlock b = shooter.getWorld().spawnFallingBlock(frontLocation, Material.DIRT.getId(), (byte) 0);
						b.setDropItem(false);
						b.setVelocity(shooter.getLocation().getDirection().multiply(1.4));
						main.spellTasks.put(b, type + "%" + new Random().nextInt(1000));
						main.api.repeat(tasks, "earthBound", 0L, 1L, main.spellTasks.get(b), main, b, shooter);
						
					break;
					
					case "DIAMOND_BLITZ":
						
						b = shooter.getWorld().spawnFallingBlock(frontLocation, Material.DIAMOND_BLOCK.getId(), (byte) 0);
						b.setDropItem(false);
						b.setVelocity(shooter.getLocation().getDirection().multiply(1.4));
						main.spellTasks.put(b, type + "%" + new Random().nextInt(1000));
						main.api.repeat(tasks, "diamondBlitz", 0L, 1L, main.spellTasks.get(b), main, b, shooter);
						
					break;
					
					case "DEFLECT":
						
						ParticleEffect.SPELL.display(2, 0, 2, 1, 6000, shooter.getLocation(), 30);
						
						for (Entity ee : shooter.getNearbyEntities(5D, 5D, 5D)){
							if (ee instanceof Player == false){
								Vector v = ee.getLocation().getDirection().multiply(-3);
								v.setY(2);
								ee.setVelocity(v);
								main.api.event(new SkillExpGainEvent(shooter, ElySkill.LUNAR, 65));
							}
						}
						
					break;
					
					case "RENEWAL":
						
						main.api.getDivSystem().addEffect("renewal" + shooter.getName(), ParticleEffect.RED_DUST, 0, 10, 0, 1, 200, frontLocation, 16, 1);
						main.api.getDivSystem().addEffect("renewal2" + shooter.getName(), ParticleEffect.PORTAL, 2, 2, 2, 1, 200, frontLocation, 16, 1);
						main.api.repeat(tasks, "renewal", 0L, 40L, "renewal3" + shooter.getName(), main, shooter);
						main.api.schedule(tasks, "cancelRenewal", 400L, "renewalCancel", main, shooter);
						
					break;
				}
				
			} else {
				dp.err("This isn't yours or you're out of supercobble.");
			}
			
		} else {
			main.s(shooter, "&c&oThis spell requires level &6&o" + level + " &c&oin &6&o" + skill.s() + "&c&o!");
		}
	}
	
	public boolean contains(String name){
		
		for (Spell spell : Spell.values()){
			if (spell.toString().equalsIgnoreCase(name.replace(" ", "_"))){
				return true;
			}
		}
		
		return false;
	}
}