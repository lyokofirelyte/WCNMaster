package com.github.lyokofirelyte.Elysian.MMO.Abilities;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Elysian.api.ElySkill;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;

public class SoulSplit {

	public Elysian main;
	
	public SoulSplit(Elysian i) {
		main = i;
	}

	public void start(Player p, DivinityPlayer dp){
		dp.set(MMO.IS_SOUL_SPLITTING, true);
		dp.set(MMO.VAMP_BAR, 1 - dp.getLevel(ElySkill.VAMPYRISM));
		dp.set(MMO.VAMP_MULT, 1);
		main.api.event(new ScoreboardUpdateEvent(p));
		p.setWalkSpeed(0.8F);
		p.setPlayerTime(18000L, false);
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 10));
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 2));
		dp.s("You feel one with the night!");
		main.api.schedule(this, "stop", 400L + (dp.getLevel(ElySkill.VAMPYRISM)*10L), "vamp" + p.getName(), p, dp);
		main.api.repeat(this, "effects", 0L, 1L, "vampEffects" + p.getName(), p);
	}
	
	public void stop(Player p, DivinityPlayer dp){
		if (p.isOnline()){
			dp.set(MMO.IS_SOUL_SPLITTING, false);
			p.setWalkSpeed(0.2F);
			p.resetPlayerTime();
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
			p.removePotionEffect(PotionEffectType.JUMP);
			dp.s("&oYou feel your powers wear off...");
			main.api.cancelTask("vampEffects" + p.getName());
		}
	}
	
	public void effects(Player p){
		if (p.isOnline()){
			p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 2);
		} else {
			main.api.cancelTask("vampEffects" + p.getName());
		}
	}
}