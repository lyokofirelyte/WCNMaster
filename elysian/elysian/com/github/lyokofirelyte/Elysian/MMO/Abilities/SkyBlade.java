package com.github.lyokofirelyte.Elysian.MMO.Abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class SkyBlade {

	public Elysian main;
	
	public SkyBlade(Elysian i) {
		main = i;
	}

	public void r(Player p, DivinityPlayer dp){
		dp.set(MMO.IS_SKY_BLADING, !dp.getBool(MMO.IS_SKY_BLADING));
		dp.s("Sky blade " + (dp.getBool(MMO.IS_SKY_BLADING) + "").replace("true", "&aactive! Right click a mob to go HAM!").replace("false", "&cinactive."));
	}
	
	public void l(Player p, DivinityPlayer dp){
		
		if (dp.getLong(MMO.SKY_BLADE_CD) <= System.currentTimeMillis()){
			dp.set(MMO.IS_SKY_BLADING, true);
			blade(p, dp);
			dp.set(MMO.SKY_BLADE_CD, System.currentTimeMillis() + (600000 - (dp.getLevel(ElySkill.ATTACK)*1000)));
		} else {
			dp.err("Sky blade is on cooldown! &6" + ((System.currentTimeMillis() - dp.getLong(MMO.SKY_BLADE_CD))/1000)*-1 + " &c&oseconds remain.");
		}
	}
	
	private void blade(Player p, DivinityPlayer dp){
		
		List<Entity> ents = new ArrayList<Entity>();
		
		for (Entity e : p.getNearbyEntities(10D, 10D, 10D)){
			if (e instanceof Monster){
				ents.add(e);
				e.setVelocity(e.getLocation().getDirection().multiply(-3));
			}
		}
		
		for (int i = p.getLocation().getBlockY(); i < 256; i++){
			if (!new Location(p.getWorld(), p.getLocation().getBlockX(), i, p.getLocation().getBlockZ()).getBlock().getType().equals(Material.AIR)){
				dp.err("The sky must be clear!");
				dp.set(MMO.SKY_BLADE_CD, 0);
				return;
			}
		}

		dp.set(MMO.SKY_BLADE_COUNTER, 0);
		dp.set(MMO.IS_SKY_BLADING, true);
		dp.set(MMO.IS_BLADING, true);
		
		main.api.schedule(this, "start", 10L, "starting" + p.getName(), p, dp, ents);
	}
	
	public void start(Player p, DivinityPlayer dp, List<Entity> ents){
		
		p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), 90));
		p.setVelocity(new Vector(0, 3, 0));
		
		finish(p, dp, ents);
		main.api.schedule(this, "after", (ents.size()) + 40L, "after" + p.getName(), p, dp);
	}
	
	private void finish(Player p, DivinityPlayer dp, List<Entity> ents){
		
		dp.set(MMO.SKY_BLADE_COUNTER, 1);
		
		for (Entity e : ents){
			main.api.schedule(this, "killUm", 1L*dp.getLong(MMO.SKY_BLADE_COUNTER) + 20L, "killUm" + p.getName() + dp.getInt(MMO.SKY_BLADE_COUNTER), p, dp, e);
			dp.set(MMO.SKY_BLADE_COUNTER, dp.getInt(MMO.SKY_BLADE_COUNTER) + 1);
		}
	}
	
	public void killUm(Player p, DivinityPlayer dp, Entity e){
		
		dp.set(MMO.SKY_BLADE_COUNTER, dp.getInt(MMO.SKY_BLADE_COUNTER) + 1);
		
		Entity snowball = (Snowball) p.launchProjectile(Snowball.class);
		snowball.setVelocity(p.getEyeLocation().getDirection().multiply(2));
		p.playSound(p.getLocation(), Sound.GHAST_FIREBALL, 3.0F, 0.5F);
		
		main.api.repeat(this, "track", 0L, 5L, "track" + p.getName() + dp.getInt(MMO.SKY_BLADE_COUNTER), p, snowball, e, e.getLocation().getY(), p.getName() + dp.getInt(MMO.SKY_BLADE_COUNTER));
	}
	
	public void track(Player p, Entity e, Entity mob, double y, String name){
		
		main.fw(e.getWorld(), e.getLocation(), Type.BURST, DivinityUtilsModule.getRandomColor());
		
		if (e.getLocation().getY() <= y+2 || e.isDead()){
			main.api.cancelTask("track" + name);
			mob.setLastDamageCause(new EntityDamageEvent(p, DamageCause.ENTITY_ATTACK, 1.0));
			main.api.event(new EntityDeathEvent((LivingEntity) mob, Arrays.asList(new ItemStack(Material.MELON, 1))));
			mob.remove();
			main.fw(e.getWorld(), mob.getLocation(), Type.BALL_LARGE, Color.WHITE);
		}
	}
	
	public void after(Player p, DivinityPlayer dp){
		dp.set(MMO.IS_SKY_BLADING, true);
		p.setVelocity(new Vector(0, -4, 0));
		main.api.schedule(this, "returned", 40L, "returned" + p.getName(), p, dp);
	}
	
	public void returned(Player p, DivinityPlayer dp){
		dp.set(MMO.IS_SKY_BLADING, false);
		dp.set(MMO.IS_BLADING, false);
	}
}