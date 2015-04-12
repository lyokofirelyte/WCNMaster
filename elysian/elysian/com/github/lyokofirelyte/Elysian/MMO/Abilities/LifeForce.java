package com.github.lyokofirelyte.Elysian.MMO.Abilities;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Elysian.api.ElySkill;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;

public class LifeForce {

	public Elysian main;
	
	public LifeForce(Elysian i) {
		main = i;
	}

	public void r(Player p, DivinityPlayer dp){
		dp.set(MMO.IS_LIFE_FORCING, !dp.getBool(MMO.IS_LIFE_FORCING));
		dp.s("Life force " + (dp.getBool(MMO.IS_LIFE_FORCING) + "").replace("true", "&aactive! Plant this to grow a random tree!").replace("false", "&cinactive."));
	}
	
	public void l(Player p, DivinityPlayer dp, Location l){
		
		if (dp.getLong(MMO.LIFE_FORCE_CD) <= System.currentTimeMillis()){
			dp.set(MMO.IS_LIFE_FORCING, true);
			life(dp, l, p.getItemInHand());
			dp.set(MMO.LIFE_FORCE_CD, System.currentTimeMillis() + (180000 - (dp.getLevel(ElySkill.FARMING)*1000)));
		} else {
			dp.err("Life force is on cooldown! &6" + ((System.currentTimeMillis() - dp.getLong(MMO.LIFE_FORCE_CD))/1000)*-1 + " &c&oseconds remain.");
		}
	}
	
	private void life(DivinityPlayer dp, Location l, ItemStack i){	
		l.getWorld().generateTree(l, TreeType.values()[new Random().nextInt(TreeType.values().length)]);
		dp.set(MMO.IS_LIFE_FORCING, false);
	}
}