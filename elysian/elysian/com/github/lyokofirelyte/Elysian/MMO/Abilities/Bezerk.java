package com.github.lyokofirelyte.Elysian.MMO.Abilities;

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class Bezerk {
	
	public Elysian main;

	public Bezerk(Elysian i) {
		main = i;
	}

	public void r(Player p, DivinityPlayer dp){
		dp.set(MMO.IS_BEZERK, !dp.getBool(MMO.IS_BEZERK));
		dp.s("Bezerk " + (dp.getBool(MMO.IS_BEZERK) + "").replace("true", "&aactive! Right-click on a mob to bleed it out!").replace("false", "&cinactive."));
	}
	
	public void l(Player p, DivinityPlayer dp, LivingEntity e){
		
		if (dp.getLong(MMO.BEZERK_CD) <= System.currentTimeMillis()){
			chop(p, dp, e);
			dp.set(MMO.BEZERK_CD, System.currentTimeMillis() + (180000 - (dp.getLevel(ElySkill.AXES)*1000)));
		} else {
			dp.err("Bezerk on cooldown! &6" + ((System.currentTimeMillis() - dp.getLong(MMO.BEZERK_CD))/1000)*-1 + " &c&oseconds remain.");
		}
	}
	
	private void chop(Player p, DivinityPlayer dp, LivingEntity e){
		dp.set(MMO.IS_BEZERKING, true);
		Random rand = new Random();
		main.api.repeat(this, "checkHealth", 0L, 20L, "bezerk " + p.getName(), dp, e, "bezerk " + p.getName(), rand);
	}
	
	public void checkHealth(DivinityPlayer dp, LivingEntity e, String taskName, Random rand){
		
		if (e.isDead()){
			dp.set(MMO.IS_BEZERKING, false);
			main.api.cancelTask(taskName);
			dp.s("Bezerk has ended - the entity has died!");
		} else {
			int amt = rand.nextInt(4);
			try {
				e.damage(amt);
			} catch (Exception ee){}
			dp.s("Bleed hit! Entity took &6" + amt + " &bdamage!");
		}
	}
}