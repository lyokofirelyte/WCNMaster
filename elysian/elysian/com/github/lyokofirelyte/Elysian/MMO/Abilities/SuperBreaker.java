package com.github.lyokofirelyte.Elysian.MMO.Abilities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Commands.ElyProtect;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Spectral.DataTypes.DRF;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class SuperBreaker {

	public Elysian main;
	
	public SuperBreaker(Elysian i) {
		main = i;
	}

	public void r(Player p, DivinityPlayer dp, MMO isVar){
		if (!dp.getBool(MMO.IS_MINING) && !dp.getBool(MMO.IS_DIGGING)){
			dp.set(isVar, !dp.getBool(isVar));
			dp.s("Super breaker " + (dp.getBool(isVar) + "").replace("true", "&aactive! Left click a block to rek it!").replace("false", "&cinactive."));
		}
	}

	public void l(Player p, DivinityPlayer dp, Block b, MMO isVar, MMO isVar2, MMO cdVar, ElySkill skill){
		
		ElyProtect pro = (ElyProtect) main.api.getInstance(ElyProtect.class);
		String result = pro.isInAnyRegion(b.getLocation());
		
		if (pro.hasFlag(result, DRF.BLOCK_BREAK)){
			if (!pro.hasRegionPerms(p, result)){
				dp.err("No permissions for this area!");
				return;
			}
		}
		
		if (dp.getLong(cdVar) <= System.currentTimeMillis()){
			dp.set(isVar2, true);
			mine(p, dp);
			dp.set(cdVar, System.currentTimeMillis() + (600000 - (dp.getLevel(skill)*1000)));
		} else if (!dp.getBool(isVar2)){
			dp.err("Super breaker or turbo drill on cooldown! &6" + ((System.currentTimeMillis() - dp.getLong(cdVar))/1000)*-1 + " &c&oseconds remain.");
			dp.set(isVar, false);
		}
	}
	
	private void mine(Player p, DivinityPlayer dp){
		
		ItemStack i = p.getItemInHand();
		ItemMeta im = i.getItemMeta();
		dp.set(MMO.SAVED_ENCHANTS, i != null && i.hasItemMeta() && i.getItemMeta().hasEnchants() ? i.getItemMeta().getEnchants() : new THashMap<Enchantment, Integer>());
		
		List<Enchantment> enchants = Arrays.asList(Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH, Enchantment.DURABILITY);
		
		if (i.hasItemMeta() && i.getItemMeta().hasLore()){
			im.getLore().add(main.AS("&3&oSuperbreaker active!"));
		} else {
			im.setLore(Arrays.asList(main.AS("&3&oSuperbreaker active!")));
		}
		
		i.setItemMeta(im);
		
		for (Enchantment e : enchants){
			i.addUnsafeEnchantment(e, 10);
		}
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 3), true);
		main.api.schedule(this, "removeEnchants", 200L, "sb" + p.getName(), p, dp, i, im);
	}
	
	@SuppressWarnings("unchecked")
	public void removeEnchants(Player p, DivinityPlayer dp, ItemStack i, ItemMeta im){
		
		if (p.isOnline()){
			List<String> lore = im.getLore();
			lore.remove(main.AS("&3&oSuperbreaker active!"));
			im.setLore(lore);
			i.setItemMeta(im);
			
			for (Enchantment e : i.getItemMeta().getEnchants().keySet()){
				i.removeEnchantment(e);
			}
			
			if (((Map<Enchantment,Integer>)dp.getRawInfo(MMO.SAVED_ENCHANTS)).size() > 0){
				for (Enchantment e : ((Map<Enchantment,Integer>)dp.getRawInfo(MMO.SAVED_ENCHANTS)).keySet()){
					i.addUnsafeEnchantment(e, ((Map<Enchantment, Integer>)dp.getRawInfo(MMO.SAVED_ENCHANTS)).get(e));
				}
			}
			
			
			dp.set(MMO.IS_SUPER_BREAKING, false);
			dp.set(MMO.IS_MINING, false);
			
			dp.set(MMO.SAVED_ENCHANTS, new THashMap<Enchantment, Integer>());
		}
	}
}