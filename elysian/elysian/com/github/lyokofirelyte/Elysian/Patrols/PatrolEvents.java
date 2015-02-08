package com.github.lyokofirelyte.Elysian.Patrols;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Events.PatrolEntityDeathEvent;
import com.github.lyokofirelyte.Divinity.Events.PatrolPlayerDeathEvent;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Divinity.Manager.JSONManager.JSONClickType;
import com.github.lyokofirelyte.Elysian.Commands.ElyProtect;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.Patrols.ElyPatrolChat.Patrol;
import com.github.lyokofirelyte.Spectral.DataTypes.DPS;
import com.github.lyokofirelyte.Spectral.DataTypes.DRF;
import com.github.lyokofirelyte.Spectral.DataTypes.DRI;
import com.github.lyokofirelyte.Spectral.Identifiers.PatrolTask;
import com.github.lyokofirelyte.Spectral.Public.ParticleEffect;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRegion;
import com.google.common.collect.ImmutableMap;

public class PatrolEvents implements Listener {

	private ElyPatrol root;
	private PatrolMobs mobs;
	private ItemStack overbowV1 = DivInvManager.createItem("&3&oOverbow v1", new String[] { "&9Tier 1 Overbow", "&6Effective Against: Skeletons" }, Enchantment.DURABILITY, 10, Material.BOW);
	private ItemStack overbowV2 = DivInvManager.createItem("&3&oOverbow v2", new String[] { "&9Tier 2 Overbow", "&6Effective Against: Spiders" }, Enchantment.DURABILITY, 10, Material.BOW);

	public PatrolEvents(ElyPatrol i){
		root = i;
		mobs = root.mobs;
	}
	
	private void spawnEffect(List<LivingEntity> ents){
		
		switch (ents.get(0).getMetadata("PatrolID").get(0).asString()){
		
			case "phaseZero":
				
				for (LivingEntity ent : ents){
					root.main.api.getDivSystem().playEffect(ParticleEffect.CRIT, 2, 1, 2, 0, 1000, ent.getLocation(), 16);
					ent.getWorld().playSound(ent.getLocation(), Sound.ANVIL_LAND, 3F, 3F);
				}
				
			break;
		}
	}
	
	private void patrolMsg(String message, String phase){
		
		ElyPatrolChat chat = ((ElyMMO) root.main.api.getInstance(ElyMMO.class)).patrols;
		JSONChatMessage msg = ((JSONChatMessage) root.main.divinity.api.createJSON("&3P &7\u2744 ", ImmutableMap.of(
				
			"&2[&aPHASE " + phase.toUpperCase() + "&2] ", ImmutableMap.of(
				JSONClickType.NONE, new String[]{
					"&b&oIndicates the current phase of the active patrol fight."
				}
			),

			"&6System" + "&f: &3" + message, ImmutableMap.of(
				JSONClickType.NONE, new String[]{
					"&7&oA global patrol message. All patrol chats will see this."
				}
			)
			
		)));
		
		for (Patrol patrol : chat.getAllPatrols()){
			chat.sendMessage(patrol, msg);
		}
	}
	
	public void checkPlayers(){
		
		for (Player p : new ArrayList<Player>(root.getPlayerList(DPS.PLAYERS))){
			if (!p.isOnline()){
				root.getPlayerList(DPS.PLAYERS).remove(p);
			}
		}
	}
	
	private void killNear(Location l, int radius){
		for (Entity e : DivinityUtilsModule.getNearbyEntities(l, radius)){
			if (e instanceof Player == false && e instanceof FallingBlock == false && e instanceof Item == false){
				root.main.api.getDivSystem().playEffect(ParticleEffect.LARGE_SMOKE, 2, 2, 2, 0, 1000, l, 16);
				e.remove();
			}
		}
	}
	
	private void killAllIds(String patrolId){
		
		if (root.containsKey("PatrolEntity" + patrolId)){
			for (LivingEntity e : root.getLivingList("PatrolEntity" + patrolId)){
				if (!e.isDead()){
					root.main.api.getDivSystem().playEffect(ParticleEffect.LARGE_SMOKE, 2, 2, 2, 0, 1000, e.getLocation(), 16);
					e.remove();
				}
			}
		}
		
		root.set("PatrolEntity" + patrolId, new ArrayList<LivingEntity>());
	}
	
	/*public void modHealth(Player lifeGuardian){
		
		for (Player p : root.getPlayerList(DPS.PLAYERS)){
			p.setHealth(p.getMaxHealth() - 2);
			p.setMaxHealth(p.getMaxHealth() - 2);
		}
		
		lifeGuardian.setMaxHealth(lifeGuardian.getMaxHealth() + 6);
		lifeGuardian.setHealth(lifeGuardian.getMaxHealth());
	}*/
	
	@EventHandler(ignoreCancelled = false)
	public void onExplode(EntityExplodeEvent e){
		
		if (e.getEntityType().equals(EntityType.ENDER_CRYSTAL)){
			patrolMsg("Yeah, those explode. Good going.", "zero");
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PatrolPlayerDeathEvent e){
		
		switch (e.getPatrolId()){
		
			case "phaseZero":
				
				Location init = new Location(e.getLoc().getWorld(), e.getLoc().getX() + 100, e.getLoc().getY(), e.getLoc().getZ() - 100);
				Location finalLoc = null;
				
				for (int i = 255; i > 0; i--){
					
					finalLoc = init.clone();
					finalLoc.setY(i);
					
					if (finalLoc != null && !finalLoc.getBlock().getType().equals(Material.AIR)){
						finalLoc.setY(i+1);
						break;
					}
				}
				
				e.getPlayer().teleport(finalLoc);
				root.main.s(e.getPlayer(), "&c&oOh dear, you're a bit lost now!");
				
			break;
			
			case "phaseOne":
				
				root.main.s(e.getPlayer(), "&c&oWe'll handle this later."); //TODO
				
			break;
		}
	}
	
	@EventHandler
	public void onDeath(PatrolEntityDeathEvent e){
		
		Random rand = new Random();
		root.set(e.getPatrolId(), (root.getInt(e.getPatrolId()) + 1));
		
		switch (e.getPatrolId()){
		
			case "phaseZero":
				
				root.main.api.getDivSystem().playEffect(ParticleEffect.EXPLODE, 1, 1, 1, 0, 500, e.getLoc(), 16);
				e.getLoc().getWorld().playSound(e.getLoc(), Sound.EXPLODE, 3F, 3F);
				
				/*for (Player p : Bukkit.getOnlinePlayers()){
					if (((ElyProtect) root.main.api.getInstance(ElyProtect.class)).isInRegion(p.getLocation(), "patrol_entrance")){
						BarAPI.setMessage(p, root.main.AS("&3PHASE ZERO KILL COUNT"), root.getInt(e.getPatrolId()) > 100 ? 100 : root.getInt(e.getPatrolId()));
					}
				}*/ // Bar API is broken ATM
				
				if (rand.nextInt(30) == 5){
					
					root.main.api.cancelTask("patrolTask_entranceTask");
					killAllIds(e.getPatrolId());
					patrolMsg("The crypt has unlocked! You feel &3dizzy...", "zero");
					List<Player> players = new ArrayList<Player>();
					
					for (Player p : Bukkit.getOnlinePlayers()){
						if (!players.contains(p)){
							if (((ElyProtect) root.main.api.getInstance(ElyProtect.class)).isInRegion(p.getLocation(), "patrol_entrance")){
								players.add(p);
							}
						}
					}
					
					for (Player p : players){
						p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 20), false);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 20), false);
						p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 20), false);
						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1, 20), false);
						p.setPlayerTime(18000L, false);
						p.setHealth(20);
						p.setFoodLevel(20);
						p.setSaturation(20);
					}
					
					root.put(DPS.PLAYERS, players);
					root.main.api.getDivSystem().remEffect("patrol_entrance");
					root.main.api.schedule(this, "phaseOne", 200L, "patrolTask_phaseOneTask", players);
				}
				
			break;
			
			case "phaseOne":
				
				root.main.api.getDivSystem().playEffect(ParticleEffect.MOB_SPELL, 1, 1, 1, 0, 1000, e.getLoc(), 16);
				e.getLoc().getWorld().playSound(e.getLoc(), Sound.EXPLODE, 3F, 3F);
				
				/*for (Player p : root.getPlayerList(DPS.PLAYERS)){
					BarAPI.setMessage(p, root.main.AS("&3PHASE ONE LIFE"), 100 - (root.getInt(e.getPatrolId())*10));
				}*/ // BarAPI is broken ATM
				
				if (root.getInt(e.getPatrolId()) >= 12){
					patrolMsg("Complete!", "one");
				}
				
			break;
		}
	}
	
	public void phaseOne(List<Player> players){
		
		DivinityRegion region = root.main.api.getDivRegion("patrol_global");
		region.set(DRF.TAKE_DAMAGE, true);
		
		for (Player p : players){
			if (p.isOnline()){
				p.teleport(mobs.getRandomLoc(new Location(Bukkit.getWorld("patrol"), 100, 80, 100), 5, 0, 5));
			}
		}
		
		root.set(DPS.PLAYERS, players);
		root.main.api.getDivSystem().addEffect("patrol_phaseOne_enchantment", ParticleEffect.ENCHANTMENT_TABLE, 5, 5, 5, 0, 100, new Location(Bukkit.getWorld("patrol"), 100, 4, 100), 16, 5);
		root.main.api.getDivRegion("patrol_entrance").set(DRI.DISABLED, true);
		root.main.api.schedule(this, "phaseOneP1", 100L, "patrolTask_phaseOneP1Task", region);
	}
	
	public void phaseOneP1(DivinityRegion region){
		
		Player selected = root.getPlayerList(DPS.PLAYERS).get(new Random().nextInt(root.getPlayerList(DPS.PLAYERS).size()));
		root.main.api.getDivPlayer(selected).lockEffect("patrolLifeGuardian", ParticleEffect.MOB_SPELL, 0, 1, 0, 10, 20, 16, 15);
		patrolMsg(selected.getDisplayName() + " &3is the life guardian!", "one");
		patrolMsg("The LG gains extra health each &3round!", "one");
		patrolMsg("&c&oHowever, the health comes from the &c&orest of you!", "one");
		patrolMsg("&aGood news though, the LG can heal &aothers!", "one");
		//modHealth(selected);
		
		region.set(DRF.TAKE_DAMAGE, false);
		mobs.clearCurrEffect();
		
		for (int i = 0; i < 5; i++){
			LivingEntity ent = mobs.spawn("phaseOne", mobs.getRandomLoc(selected.getLocation(), 5, 0, 5), EntityType.SKELETON, 100, 1, overbowV2, null, null, mobs.fullIron()).get(0);
			mobs.spawn("phaseOne", mobs.getRandomLoc(selected.getLocation(), 5, 0, 5), EntityType.CAVE_SPIDER, 10, 1, new ItemStack(Material.DIAMOND_SWORD), ent, mobs.modCurrentEffect(PotionEffectType.JUMP, 2, true), mobs.fullDiamond());
		}
	}
	
	@PatrolTask(duration = 200L)
	public void entranceTask(){
		
		Player one = null;
		int i = 0;
		
		for (LivingEntity e : new ArrayList<LivingEntity>(root.getLivingList("PatrolEntityphaseZero"))){
			if (e.isDead()){
				root.getLivingList("PatrolEntityphaseZero").remove(e);
			}
		}
		
		int size = root.getLivingList("PatrolEntityphaseZero").size();
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (((ElyProtect) root.main.api.getInstance(ElyProtect.class)).isInRegion(p.getLocation(), "patrol_entrance")){
				if (((ElyMMO) root.main.api.getInstance(ElyMMO.class)).patrols.doesPatrolExistWithPlayer(p)){
					i++;
					one = p;
				} else {
					root.main.s(p, "&c&oPlease join a patrol to use this area. Type &6/patrol&c&o.");
				}
			}
		}
		
		if (i > 0 && size < (i*2*5)){
			for (int x = 0; x < i; x++){
				spawnEffect(mobs.spawn("phaseZero", root.mobs.getRandomLoc(one.getLocation(), 5, 0, 5), EntityType.SKELETON, 50, 2, overbowV1, null, mobs.modCurrentEffect(PotionEffectType.FIRE_RESISTANCE, 1, true), mobs.fullChain()));
			}
		}
	}
}