package com.github.lyokofirelyte.Elysian.MMO.Magics;

import java.util.Random;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.SkillExpGainEvent;
import com.github.lyokofirelyte.Elysian.api.ElySkill;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;

public class SpellEvents implements AutoRegister<SpellEvents>, Listener {
	
	private Elysian main;
	
	@Getter
	private SpellEvents type = this;

	public SpellEvents(Elysian i) {
		main = i;
	}
	
	@EventHandler
	public void onIgnite(BlockIgniteEvent e){
		
		if (e.getCause() == IgniteCause.FIREBALL){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onChange(EntityChangeBlockEvent e){
		if (main.spellTasks.containsKey(e.getEntity())){
			e.setCancelled(true);
			try {
				e.getEntity().remove();
			} catch (Exception ee){}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onHit(ProjectileHitEvent e){
		
		if (main.spellTasks.containsKey(e.getEntity())){
			
			Projectile pro = e.getEntity();
			double dmg = 0;
			int xp = 0;
			
			if (pro.getShooter() instanceof Player){
			
				switch (main.spellTasks.get(pro).split("%")[0]){
				
					case "FIRE_BLAST":
						
						ParticleEffect.ANGRY_VILLAGER.display(1, 0, 1, 0, 1000, pro.getLocation(), 30);
						xp = 30;
						dmg = 4;
						
					break;
					
					case "KERSPLASH":
						
						ParticleEffect.MAGIC_CRIT.display(1, 1, 1, 0, 1000, pro.getLocation(), 30);
						xp = 40;
						dmg = 4.5;
						
					break;
					
					case "RAPID_FIRE":
						
						ParticleEffect.MAGIC_CRIT.display(1, 1, 1, 0, 1000, pro.getLocation(), 30);
						xp = 50;
						dmg = 5;
						
					break;
					
					case "NORMAL_ARROW":
						
						ParticleEffect.SPELL.display(0, 0, 0, 1, 1000, pro.getLocation(), 30);
						
					break;
					
					default: break;
				}
				
				dmg = dmg + (0.4*main.api.getDivPlayer((Player)pro.getShooter()).getLevel(ElySkill.SOLAR));
				
				switch (main.spellTasks.get(pro).split("%")[0]){
				
					case "FIRE_BLAST": case "KERSPLASH": case "RAPID_FIRE":
						
						for (Entity ent : pro.getNearbyEntities(2D, 2D, 2D)){
							if (ent instanceof Monster){
								((Monster) ent).damage(dmg);
								main.api.event(new SkillExpGainEvent((Player) pro.getShooter(), ElySkill.SOLAR, xp));
							}
						}
						
					break;
				}
			}
			
			main.api.cancelTask(main.spellTasks.get(pro));
			main.spellTasks.remove(pro);
		}
	}
	
	@EventHandler
	public void onBow(EntityShootBowEvent e){
		if (e.getProjectile() instanceof Arrow){
			Arrow pro = (Arrow) e.getProjectile();
			main.spellTasks.put(pro, "NORMAL_ARROW" + "%" + new Random().nextInt(1000));
			main.api.repeat(new SpellTasks(main), "normalArrow", 0L, 1L, main.spellTasks.get(pro), main, pro);
		}
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent e){
		
		if (e.getAction() == Action.RIGHT_CLICK_AIR){
			Player p = e.getPlayer();
			if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName() && p.getItemInHand().getItemMeta().hasLore()){
				if (Spell.FIRE_BLAST.contains(ChatColor.stripColor(main.AS(p.getItemInHand().getItemMeta().getDisplayName())))){
					try {
						Spell.valueOf(ChatColor.stripColor(main.AS(p.getItemInHand().getItemMeta().getDisplayName().toUpperCase().replace(" ", "_")))).cast(main, p, false);
					} catch (Exception ex){
						main.s(p, "&c&oSomething went wrong casting this spell.");
						ex.printStackTrace();
					}
					return;
				}
			}
		}
	}
}