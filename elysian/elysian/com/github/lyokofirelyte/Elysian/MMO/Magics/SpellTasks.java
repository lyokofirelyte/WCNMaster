package com.github.lyokofirelyte.Elysian.MMO.Magics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import com.github.lyokofirelyte.Divinity.Events.SkillExpGainEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.Public.ParticleEffect;

public class SpellTasks {

	private Elysian main;
	
	public SpellTasks(Elysian i) {
		main = i;
	}
	
	public void earthBound(Elysian main, FallingBlock fireball, Player p){
		if (!fireball.isDead()){
			ParticleEffect.SLIME.display(1, 1, 1, 1, 300, fireball.getLocation(), 30);
			for (Entity e : fireball.getNearbyEntities(2D, 2D, 2D)){
				if (e instanceof Monster){
					((Monster) e).damage(5 + (0.4*main.api.getDivPlayer(p).getLevel(ElySkill.SOLAR)));
					main.api.event(new SkillExpGainEvent(p, ElySkill.SOLAR, 65));
					ParticleEffect.CLOUD.display(1, 1, 1, 0, 1000, e.getLocation(), 30);
				}
			}
		} else {
			main.api.cancelTask(main.spellTasks.get(fireball));
		}
	}
	
	public void diamondBlitz(Elysian main, FallingBlock fireball, Player p){
		if (!fireball.isDead()){
			ParticleEffect.displayBlockCrack(Material.DIAMOND_BLOCK.getId(), (byte) 0, 1, 1, 1, 300, fireball.getLocation(), 30);
			for (Entity e : fireball.getNearbyEntities(2D, 2D, 2D)){
				if (e instanceof Monster){
					((Monster) e).damage(6 + (0.4*main.api.getDivPlayer(p).getLevel(ElySkill.SOLAR)));
					main.api.event(new SkillExpGainEvent(p, ElySkill.SOLAR, 80));
					ParticleEffect.CLOUD.display(1, 1, 1, 0, 1000, e.getLocation(), 30);
				}
			}
		} else {
			main.api.cancelTask(main.spellTasks.get(fireball));
		}
	}
	
	public void rapidFire(Elysian main, SmallFireball fireball){
		if (!fireball.isDead()){
			ParticleEffect.LAVA.display(1, 1, 1, 1, 300, fireball.getLocation(), 30);
		} else {
			main.api.cancelTask(main.spellTasks.get(fireball));
		}
	}
	
	public void kersplash(Elysian main, SmallFireball fireball){
		if (!fireball.isDead()){
			ParticleEffect.SPLASH.display(1, 1, 1, 1, 300, fireball.getLocation(), 30);
		} else {
			main.api.cancelTask(main.spellTasks.get(fireball));
		}
	}
	
	public void fireball(Elysian main, SmallFireball fireball){
		if (!fireball.isDead()){
			ParticleEffect.RED_DUST.display(0, 0, 0, 0, 200, fireball.getLocation(), 30);
		} else {
			main.api.cancelTask(main.spellTasks.get(fireball));
		}
	}
	
	public void normalArrow(Elysian main, Arrow arrow){
		if (!arrow.isDead()){
			ParticleEffect.FIREWORKS_SPARK.display(1, 0, 1, 2, 30, arrow.getLocation(), 16);
		} else {
			main.api.cancelTask(main.spellTasks.get(arrow));
		}
	}
	
	public void renewal(Elysian main, Player p){
		
		if (p.isOnline()){
			
			List<Entity> ents = new ArrayList<Entity>(p.getNearbyEntities(5D, 5D, 5D));
			ents.add(p);
			
			for (Entity e : ents){
				if (e instanceof Player){
					Player them = (Player) e;
					if (((Damageable) them).getHealth() < ((Damageable) them).getMaxHealth()){
						main.api.event(new SkillExpGainEvent(p, ElySkill.LUNAR, 50));
						if (((Damageable) them).getHealth() < ((Damageable) them).getMaxHealth() - 2){
							them.setHealth(((Damageable) them).getHealth() + 2);
						} else {
							them.setHealth(((Damageable) them).getMaxHealth());
						}
					}
				}
			}
			
		} else {
			cancelRenewal(main, p);
		}
	}
	
	public void cancelRenewal(Elysian main, Player p){
		main.api.getDivSystem().remEffect("renewal" + p.getName());
		main.api.getDivSystem().remEffect("renewal2" + p.getName());
		main.api.cancelTask("renewal3" + p.getName());
	}
}