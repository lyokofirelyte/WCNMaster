package com.github.lyokofirelyte.Elysian.Patrols;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gnu.trove.map.hash.THashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.lyokofirelyte.Elysian.Commands.ElyProtect;
import com.github.lyokofirelyte.Spectral.DataTypes.DRF;

public class PatrolMobs {

	private ElyPatrol root;
	private Map<PotionEffectType, Integer> currentEffect = new THashMap<PotionEffectType, Integer>();
	
	public PatrolMobs(ElyPatrol i){
		root = i;
	}
	
	public List<LivingEntity> spawn(String id, Location where, EntityType type, double health, int amount, ItemStack weapon, LivingEntity mount, Map<PotionEffectType, Integer> effects, ItemStack... armor){
		
		List<LivingEntity> ents = new ArrayList<LivingEntity>();
		List<LivingEntity> oldEnts = root.containsKey("PatrolEntity" + id) ? root.getLivingList("PatrolEntity" + id) : new ArrayList<LivingEntity>();
		
		if (((ElyProtect) root.main.api.getInstance(ElyProtect.class)).isInRegion(where, "patrol_global")){
			root.main.api.getDivRegion("patrol_global").set(DRF.MOB_SPAWN, false);
		}
		
		for (int x = 0; x < amount; x++){
			LivingEntity ent = (LivingEntity) where.getWorld().spawnEntity(where, type);
			ItemStack[] items = new ItemStack[4];
			
			for (int i = 0; i < 4; i++){
				if (armor.length > i){
					items[i] = armor[i];
				} else {
					items[i] = new ItemStack(Material.AIR);
				}
			}
			
			ent.setMaxHealth(health);
			ent.setHealth(health);
			ent.getEquipment().setItemInHand(weapon);
			ent.getEquipment().setArmorContents(items);
			
			if (effects != null){
				for (PotionEffectType potion : effects.keySet()){
					ent.addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE, effects.get(potion), true));
				}
			}
			
			if (mount != null){
				ent.setPassenger(mount);
			}
			
			ent.setMetadata("PatrolID", new FixedMetadataValue(root.main, id));
			ents.add(ent);
			
			if (!oldEnts.contains(ent)){
				oldEnts.add(ent);
			}
		}
		
		root.set("PatrolEntity" + id, oldEnts);
		root.main.api.getDivRegion("patrol_global").set(DRF.MOB_SPAWN, true);
		return ents;
	}
	
	public List<LivingEntity> spawn(String id, Location where, EntityType type, double health, int amount, ItemStack weapon, ItemStack... armor){
		return spawn(id, where, type, health, amount, weapon, null, new THashMap<PotionEffectType, Integer>(), armor);
	}
	
	public List<LivingEntity> spawn(String id, Location where, EntityType type, double health, int amount, ItemStack weapon){
		return spawn(id, where, type, health, amount, weapon, null, new THashMap<PotionEffectType, Integer>());
	}
	
	public List<LivingEntity> spawn(String id, Location where, EntityType type, double health, int amount){
		return spawn(id, where, type, health, amount, new ItemStack(Material.AIR), null, new THashMap<PotionEffectType, Integer>());
	}
	
	public List<LivingEntity> spawn(String id, Location where, EntityType type){
		return spawn(id, where, type, 20, 1, new ItemStack(Material.AIR), null, new THashMap<PotionEffectType, Integer>());
	}
	
	public ItemStack[] fullDiamond(){
		return new ItemStack[]{
			new ItemStack(Material.DIAMOND_HELMET),
			new ItemStack(Material.DIAMOND_CHESTPLATE),
			new ItemStack(Material.DIAMOND_LEGGINGS),
			new ItemStack(Material.DIAMOND_BOOTS)
		};
	}
	
	public ItemStack[] fullIron(){
		return new ItemStack[]{
			new ItemStack(Material.IRON_HELMET),
			new ItemStack(Material.IRON_CHESTPLATE),
			new ItemStack(Material.IRON_LEGGINGS),
			new ItemStack(Material.IRON_BOOTS)
		};
	}
	
	public ItemStack[] fullGold(){
		return new ItemStack[]{
			new ItemStack(Material.GOLD_HELMET),
			new ItemStack(Material.GOLD_CHESTPLATE),
			new ItemStack(Material.GOLD_LEGGINGS),
			new ItemStack(Material.GOLD_BOOTS)
		};
	}
	
	public ItemStack[] fullChain(){
		return new ItemStack[]{
			new ItemStack(Material.CHAINMAIL_HELMET),
			new ItemStack(Material.CHAINMAIL_CHESTPLATE),
			new ItemStack(Material.CHAINMAIL_LEGGINGS),
			new ItemStack(Material.CHAINMAIL_BOOTS)
		};
	}
	
	public ItemStack[] fullLeather(){
		return new ItemStack[]{
			new ItemStack(Material.LEATHER_HELMET),
			new ItemStack(Material.LEATHER_CHESTPLATE),
			new ItemStack(Material.LEATHER_LEGGINGS),
			new ItemStack(Material.LEATHER_BOOTS)
		};
	}
	
	public Location getRandomLoc(Location start, int radiusX, int radiusY, int radiusZ){
		
		Random rand = new Random();
		int x = rand.nextInt(radiusX+1) * (rand.nextInt(2) == 1 ? 1 : -1);
		int y = rand.nextInt(radiusY+1) * (rand.nextInt(2) == 1 ? 1 : -1);
		int z = rand.nextInt(radiusZ+1) * (rand.nextInt(2) == 1 ? 1 : -1);
		
		return new Location(start.getWorld(), (start.getBlockX() + x), (start.getBlockY() + y), (start.getBlockZ() + z));
	}
	
	public Location getRandomLoc(Location start, int radius){
		return getRandomLoc(start, radius, radius, radius);
	}
	
	public void clearCurrEffect(){
		currentEffect = new THashMap<PotionEffectType, Integer>();
	}
	
	public Map<PotionEffectType, Integer> modCurrentEffect(PotionEffectType type, int amp, boolean add){
		
		if (add){
			currentEffect.put(type, amp);
		} else if (currentEffect.containsKey(type)){
			currentEffect.remove(type);
		}
		
		return currentEffect;
	}
}