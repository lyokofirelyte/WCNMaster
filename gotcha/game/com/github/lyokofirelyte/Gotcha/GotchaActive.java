package com.github.lyokofirelyte.Gotcha;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.ParticleEffect;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class GotchaActive implements Listener, AutoRegister<GotchaActive> {

	private Gotcha root;
	
	@Getter
	private GotchaActive type = this;
	
	public GotchaActive(Gotcha i){
		root = i;
	}
	
	@EventHandler
	public void onIn(InventoryDragEvent e){
		if (!e.getWhoClicked().isOp()){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onIn(InventoryClickEvent e){
		if (!e.getWhoClicked().isOp()){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent e){
		
		if (!e.getPlayer().isOp()){
			e.setCancelled(true);
			e.getPlayer().closeInventory();
		}
	}
	
	@EventHandler
	public void onHit(ProjectileHitEvent e){
		
		if (e.getEntity() instanceof SmallFireball){
			
			SmallFireball ball = (SmallFireball) e.getEntity();
			
			if (((Projectile) ball).getShooter() instanceof Player){
				
				ProjectileSource source = ((Projectile) ball).getShooter();
				GamePlayer<GotchaPlayer> shooter = root.getApi().getGamePlayer(((Player) source).getUniqueId());
				
				for (Entity v : ball.getNearbyEntities(2D, 2D, 2D)){
					if (v instanceof Player){
						GamePlayer<GotchaPlayer> shot = root.getApi().getGamePlayer(((Player) v).getUniqueId());
						if (!shot.getName().equals(shooter.getName())){
							APIScheduler.REPEAT.stop(ball.getUniqueId().toString());
							ParticleEffect.ANGRY_VILLAGER.display(1, 1, 1, 0, 2000, v.getLocation(), 100);
							v.getWorld().playSound(v.getLocation(), Sound.EXPLODE, 15F, 15F);
							shooter.getType().setScore(shooter.getType().getScore() + 1);
							root.spawnPlayer(shot.getPlayer());
							Utils.bc(shooter.getName() + " &7&o" + GotchaWords.generate() + "'d &6" + shot.getName());
							
							if (shooter.getType().getScore() >= 30){
								Utils.bc("&7" + shooter.getName() + " &awas victorious!");
								root.endGame();
							}
							
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onSwap(PlayerItemHeldEvent e){
		if (!e.getPlayer().isOp()){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLaunch(final PlayerInteractEvent e){
		
		if (root.isCanShoot()){
			if (e.getAction() == Action.RIGHT_CLICK_AIR && e.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_HOE)){
				
				Player p = e.getPlayer();
				GamePlayer<GotchaPlayer> gp = root.getApi().getGamePlayer(p.getUniqueId());
				GotchaPlayer g = gp.getType();
				
				if (g.getCooldown() == -1){

					g.setCooldown(3L);
					
					APIScheduler.REPEAT.start(root.getApi(), "cooldown_" + p.getName(), 20L, 20L, new Runnable(){
						public void run(){
							
							Player p = e.getPlayer();
							
							if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)){
								ItemStack i = p.getItemInHand();
								ItemMeta im = i.getItemMeta();
								GamePlayer<GotchaPlayer> gp = root.getApi().getGamePlayer(p.getUniqueId());
								GotchaPlayer g = gp.getType();
								
								if (g.getCooldown() == 0){
									im.setDisplayName(Utils.AS("&aReady!"));
									APIScheduler.REPEAT.stop("cooldown_" + p.getName());
									g.setCooldown(-1);
								} else {
									im.setDisplayName(Utils.AS("&a" + g.getCooldown()));
									g.setCooldown(g.getCooldown() - 1L);
								}
								
								i.setItemMeta(im);
							}
						}
					});
					
					Location eyeLocation = p.getLocation();
					eyeLocation.setY(eyeLocation.getY() + 1.5);
					Location frontLocation = eyeLocation.add(eyeLocation.getDirection());
					
					final SmallFireball fireball = (SmallFireball) p.getWorld().spawnEntity(frontLocation, EntityType.SMALL_FIREBALL);
					fireball.setShooter(p);
					fireball.setVelocity(p.getLocation().getDirection().multiply(2.2));
				
					APIScheduler.REPEAT.start(root.getApi(), fireball.getUniqueId().toString(), 0L, 1L, new Runnable(){
						public void run(){
							if (!fireball.isDead()){
								ParticleEffect.DRIP_WATER.display(0, 0, 0, 1, 100, fireball.getLocation(), 30);
								ParticleEffect.RED_DUST.display(0, 0, 0, 1, 50, fireball.getLocation(), 30);
							} else {
								APIScheduler.REPEAT.stop(fireball.getUniqueId().toString());
							}
						}
					});
				}
			}
		}
	}
}