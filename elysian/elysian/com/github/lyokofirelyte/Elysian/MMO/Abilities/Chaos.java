package com.github.lyokofirelyte.Elysian.MMO.Abilities;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Elysian.MMO.Magics.SpellTasks;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.Public.ParticleEffect;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class Chaos {
	
	public Elysian main;

	public Chaos(Elysian i) {
		main = i;
	}

	public void r(Player p, DivinityPlayer dp){
		if (!dp.getBool(MMO.IS_CHAOSING)){
			dp.set(MMO.IS_CHAOS, !dp.getBool(MMO.IS_CHAOS));
			dp.s("Chaos " + (dp.getBool(MMO.IS_CHAOS) + "").replace("true", "&aactive! Right-click on multiple mobs to join them together!").replace("false", "&cinactive."));
		}
	}
	
	public void l(Player p, DivinityPlayer dp, LivingEntity e){
		
		if (dp.getLong(MMO.CHAOS_CD) <= System.currentTimeMillis() && !dp.getBool(MMO.IS_CHAOSING)){
			dp.set(MMO.IS_CHAOSING, true);
			dp.set(MMO.CHAOS_CD, System.currentTimeMillis() + (180000 - (dp.getLevel(ElySkill.CHAOS)*1000)));
			main.api.schedule(this, "reset", 200L, "reset " + p.getName(), dp);
		} else {
			dp.err("Chaos on cooldown! &6" + ((System.currentTimeMillis() - dp.getLong(MMO.CHAOS_CD))/1000)*-1 + " &c&oseconds remain.");
		}
	}
	
	public void reset(DivinityPlayer dp){
		
		try {
			
			dp.s("The entites that you've tagged have morphed into one!");
			List<LivingEntity> entList = (List<LivingEntity>) dp.getRawInfo(DPI.CHAOS_LIST);
			List<LivingEntity> entFinalList = new ArrayList<LivingEntity>();
			double hp = 0;
			LivingEntity lastEnt = null;
			int i = 0;
			
			for (LivingEntity ent : entList){
				if (!ent.isDead()){
					entFinalList.add(ent);
				}
			}
			
			for (LivingEntity ent : entFinalList){
				i++;
				hp += ent.getMaxHealth();
				if (i == entFinalList.size()){
					lastEnt = ent;
				} else {
					ent.damage(300);
				}
			}
			
			lastEnt.setMaxHealth(hp);
			lastEnt.setHealth(hp);
			lastEnt.setCustomName(main.AS("&k| &4&lchA0t!c &k|"));
			lastEnt.setCustomNameVisible(true);
			
			main.api.repeat(this, "check", 0L, 20L, "chaosCheck" + dp.name(), lastEnt, "chaosCheck" + dp.name());
			
		} catch (Exception e){}
	}
	
	public void check(LivingEntity ent, String task){
		if (ent.isDead()){
			main.api.cancelTask(task);
			if (new Random().nextInt(1001) == 500){
				ent.getWorld().dropItem(ent.getKiller().getLocation(), DivInvManager.createItem(main.AS("&5&o))( &f&odRaX &5&o)(("), new String[] {"&6&o(HAOS DEVICE", "&a&o22000/7000"}, Enchantment.DURABILITY, 10, Material.ARROW, 1));
				DivinityUtilsModule.bc(ent.getKiller().getDisplayName() + " has found a super drax! (15,000 extra charges!)");
				main.api.getDivSystem().addEffect("renewal" + ent.getKiller().getName(), ParticleEffect.RED_DUST, 0, 10, 0, 1, 200, ent.getKiller().getLocation(), 16, 1);
				main.api.getDivSystem().addEffect("renewal2" + ent.getKiller().getName(), ParticleEffect.PORTAL, 2, 2, 2, 1, 200, ent.getKiller().getLocation(), 16, 1);
				main.api.repeat(new SpellTasks(main), "renewal", 0L, 40L, "renewal3" + ent.getKiller().getName(), main, ent.getKiller());
				main.api.schedule(new SpellTasks(main), "cancelRenewal", 400L, "renewalCancel", main, ent.getKiller());
			}
		}
	}
}