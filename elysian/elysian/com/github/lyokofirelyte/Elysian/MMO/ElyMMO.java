package com.github.lyokofirelyte.Elysian.MMO;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;

import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Commands.ElyEffects;
import com.github.lyokofirelyte.Elysian.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Events.ElyLogger;
import com.github.lyokofirelyte.Elysian.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Elysian.Events.SkillExpGainEvent;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.Bezerk;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.Chaos;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.LifeForce;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SkyBlade;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SoulSplit;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SuperBreaker;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.TreeFeller;
import com.github.lyokofirelyte.Elysian.MMO.Magics.SpellEvents;
import com.github.lyokofirelyte.Elysian.MMO.Magics.SpellTasks;
import com.github.lyokofirelyte.Empyreal.APIScheduler;
import com.github.lyokofirelyte.Empyreal.Command.GameCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.Elysian.ElySkill;
import com.github.lyokofirelyte.Empyreal.Gui.DivInvManager;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;

public class ElyMMO extends THashMap<Material, MXP> implements Listener, AutoRegister<ElyMMO> {
	
	private static final long serialVersionUID = 1L;
	
	public Elysian main;
	public TreeFeller treeFeller;
	public SuperBreaker superBreaker;
	public SkyBlade skyBlade;
	public LifeForce life;
	public ElyAutoRepair repair;
	public SoulSplit soulSplit;
	public SpellEvents spellEvents;
	public SpellTasks spellTasks;
	public Bezerk bezerk;
	public Chaos chaos;
	
	@Getter
	private ElyMMO type = this;
	
	public Map<String, List<Item>> noPickup = new THashMap<>();
	public Map<SmallFireball, String> potions = new THashMap<>();
	
	public ElyMMO(Elysian i) {
		main = i;
		treeFeller = new TreeFeller(main);
		superBreaker = new SuperBreaker(main);
		skyBlade = new SkyBlade(main);
		life = new LifeForce(main);
		soulSplit = new SoulSplit(main);
		bezerk = new Bezerk(main);
		chaos = new Chaos(main);
		fillMap();
		
		APIScheduler.DELAY.start(main.gameAPI, "ElyMMO", 200L, new Runnable(){
			public void run(){
				repair = main.gameAPI.getInstance(ElyAutoRepair.class).getType();
				spellEvents = main.gameAPI.getInstance(SpellEvents.class).getType();
				spellTasks = main.gameAPI.getInstance(SpellTasks.class).getType();
			}
		});
	}

	//Material -> Skill -> [XP, LevelRequirement]
	public void fillMap(){
		sm(Material.LOG, ElySkill.WOODCUTTING, 125, 0);
		sm(Material.LOG_2, ElySkill.WOODCUTTING, 138, 15);
		sm(Material.LEAVES, ElySkill.WOODCUTTING, 168, 30);
		sm(Material.LEAVES_2, ElySkill.WOODCUTTING, 200, 45);
		sm(Material.STONE, ElySkill.MINING, 15, 0);
		sm(Material.NETHERRACK, ElySkill.MINING, 15, 0);
		sm(Material.HARD_CLAY, ElySkill.MINING, 15, 0);
		sm(Material.STAINED_CLAY, ElySkill.MINING, 15, 0);
		sm(Material.NETHER_BRICK, ElySkill.MINING, 17, 5);
		sm(Material.ENDER_STONE, ElySkill.MINING, 17, 5);
		sm(Material.ICE, ElySkill.MINING, 17, 5);
		sm(Material.PACKED_ICE, ElySkill.MINING, 18, 7);
		sm(Material.QUARTZ_ORE, ElySkill.MINING, 20, 10);
		sm(Material.IRON_ORE, ElySkill.MINING, 35, 15);
		sm(Material.COAL_ORE, ElySkill.MINING, 50, 30);
		sm(Material.REDSTONE_ORE, ElySkill.MINING, 65, 40);
		sm(Material.GOLD_ORE, ElySkill.MINING, 80, 55);
		sm(Material.OBSIDIAN, ElySkill.MINING, 85, 60);
		sm(Material.MYCEL, ElySkill.MINING, 90, 65);
		sm(Material.LAPIS_ORE, ElySkill.MINING, 95, 70);
		sm(Material.DIAMOND_ORE, ElySkill.MINING, 155, 85);
		sm(Material.EMERALD_ORE, ElySkill.MINING, 180, 90);
		
		sm(Material.DIRT, ElySkill.DIGGING, 12, 0);
		sm(Material.GRASS, ElySkill.DIGGING, 15, 5);
		sm(Material.SAND, ElySkill.DIGGING, 20, 10);
		sm(Material.SNOW_BLOCK, ElySkill.DIGGING, 25, 20);
		sm(Material.SOUL_SAND, ElySkill.DIGGING, 30, 25);
		sm(Material.CLAY, ElySkill.DIGGING, 35, 40);
		sm(Material.GRAVEL, ElySkill.DIGGING, 50, 55);
		sm(Material.GLOWSTONE, ElySkill.DIGGING, 70, 60);
		
		sm(Material.LONG_GRASS, ElySkill.FARMING, 15, 0);
		sm(Material.CROPS, ElySkill.FARMING, 17, 0);
		sm(Material.PUMPKIN, ElySkill.FARMING, 38, 10);
		sm(Material.MELON_BLOCK, ElySkill.FARMING, 50, 30);
		sm(Material.SUGAR_CANE_BLOCK, ElySkill.FARMING, 65, 40);
		sm(Material.COCOA, ElySkill.FARMING, 80, 55);
		sm(Material.NETHER_WARTS, ElySkill.FARMING, 85, 60);
		sm(Material.CARROT, ElySkill.FARMING, 95, 70);
		sm(Material.POTATO, ElySkill.FARMING, 125, 85);
		sm(Material.CACTUS, ElySkill.FARMING, 150, 90);
		sm(Material.RED_ROSE, ElySkill.FARMING, 200, 95);
		sm(Material.RED_MUSHROOM, ElySkill.FARMING, 250, 96);
		sm(Material.BROWN_MUSHROOM, ElySkill.FARMING, 250, 96);
		sm(Material.VINE, ElySkill.FARMING, 300, 97);
		sm(Material.WATER_LILY, ElySkill.FARMING, 325, 98);
		
		sm(Material.ARROW, ElySkill.CRAFTING, 15, 0);
		sm(Material.STICK, ElySkill.CRAFTING, 15, 0);
		sm(Material.WORKBENCH, ElySkill.CRAFTING, 20, 0);
		sm(Material.FURNACE, ElySkill.CRAFTING, 25, 5);
		sm(Material.WOOD, ElySkill.CRAFTING, 30, 10);
		sm(Material.COBBLESTONE_STAIRS, ElySkill.CRAFTING, 33, 20);
		sm(Material.MELON_BLOCK, ElySkill.CRAFTING, 50, 25);
		sm(Material.IRON_BLOCK, ElySkill.CRAFTING, 65, 27);
		sm(Material.COAL_BLOCK, ElySkill.CRAFTING, 90, 30);
		sm(Material.REDSTONE_BLOCK, ElySkill.CRAFTING, 93, 35);
		sm(Material.LAPIS_BLOCK, ElySkill.CRAFTING, 95, 45);
		sm(Material.IRON_SWORD, ElySkill.CRAFTING, 100, 50);
		sm(Material.DIAMOND_SWORD, ElySkill.CRAFTING, 116, 55);
		sm(Material.DIAMOND_BLOCK, ElySkill.CRAFTING, 150, 65);
		sm(Material.EMERALD_BLOCK, ElySkill.CRAFTING, 200, 70);
		sm(Material.CAKE, ElySkill.CRAFTING, 220, 75);
		sm(Material.ANVIL, ElySkill.CRAFTING, 350, 85);
		sm(Material.BEACON, ElySkill.CRAFTING, 375, 90);
		
		tool(Material.BOW, ElySkill.ARCHERY, 0);

		tool(Material.WOOD_SWORD, ElySkill.ATTACK, 0);
		tool(Material.STONE_SWORD, ElySkill.ATTACK, 15);
		tool(Material.IRON_SWORD, ElySkill.ATTACK, 25);
		tool(Material.GOLD_SWORD, ElySkill.ATTACK, 30);
		tool(Material.DIAMOND_SWORD, ElySkill.ATTACK, 45);
		
		tool(Material.WOOD_AXE, ElySkill.ATTACK, 50);
		tool(Material.STONE_AXE, ElySkill.ATTACK, 65);
		tool(Material.IRON_AXE, ElySkill.ATTACK, 75);
		tool(Material.GOLD_AXE, ElySkill.ATTACK, 80);
		tool(Material.DIAMOND_AXE, ElySkill.ATTACK, 92);
		
		tool(Material.WOOD_PICKAXE, ElySkill.ATTACK, 59);
		tool(Material.STONE_PICKAXE, ElySkill.ATTACK, 69);
		tool(Material.IRON_PICKAXE, ElySkill.ATTACK, 79);
		tool(Material.GOLD_PICKAXE, ElySkill.ATTACK, 89);
		tool(Material.DIAMOND_PICKAXE, ElySkill.ATTACK, 99);
		
		tool(Material.WOOD_SPADE, ElySkill.ATTACK, 0);
		tool(Material.STONE_SPADE, ElySkill.ATTACK, 5);
		tool(Material.IRON_SPADE, ElySkill.ATTACK, 10);
		tool(Material.GOLD_SPADE, ElySkill.ATTACK, 20);
		tool(Material.DIAMOND_SPADE, ElySkill.ATTACK, 25);
		
		tool(Material.WOOD_AXE, ElySkill.AXES, 0);
		tool(Material.STONE_AXE, ElySkill.AXES, 15);
		tool(Material.IRON_AXE, ElySkill.AXES, 25);
		tool(Material.GOLD_AXE, ElySkill.AXES, 30);
		tool(Material.DIAMOND_AXE, ElySkill.AXES, 45);
		
		tool(Material.WOOD_AXE, ElySkill.WOODCUTTING, 0);
		tool(Material.STONE_AXE, ElySkill.WOODCUTTING, 15);
		tool(Material.IRON_AXE, ElySkill.WOODCUTTING, 25);
		tool(Material.GOLD_AXE, ElySkill.WOODCUTTING, 30);
		tool(Material.DIAMOND_AXE, ElySkill.WOODCUTTING, 45);
		
		tool(Material.WOOD_PICKAXE, ElySkill.MINING, 0);
		tool(Material.STONE_PICKAXE, ElySkill.MINING, 15);
		tool(Material.IRON_PICKAXE, ElySkill.MINING, 25);
		tool(Material.GOLD_PICKAXE, ElySkill.MINING, 30);
		tool(Material.DIAMOND_PICKAXE, ElySkill.MINING, 45);
		
		tool(Material.WOOD_SPADE, ElySkill.DIGGING, 0);
		tool(Material.STONE_SPADE, ElySkill.DIGGING, 15);
		tool(Material.IRON_SPADE, ElySkill.DIGGING, 25);
		tool(Material.GOLD_SPADE, ElySkill.DIGGING, 30);
		tool(Material.DIAMOND_SPADE, ElySkill.DIGGING, 45);
	}
	
	private void tool(Material tool, ElySkill skill, int level){
		if (containsKey(tool)){
			get(tool).addTool(skill, level, tool);
		} else {
			put(tool, new MXP(tool, skill, 0, 0));
			get(tool).addTool(skill, level, tool);
		}
	}
	
	private void sm(Material m, ElySkill s, int x, int l){
		if (containsKey(m)){
			get(m).addSkill(s, l, x);
		} else {
			put(m, new MXP(m, s, l, x));
		}
	}
	
	private Map<Material, Integer> getTools(ElySkill skill){
		
		Map<Material, Integer> map = new THashMap<>();
		
		for (MXP m : values()){
			for (Material mat : m.toolReqs.keySet()){
				for (ElySkill s : m.toolReqs.get(mat).keySet()){
					if (s.equals(skill)){
						map.put(mat, m.toolReqs.get(mat).get(s));
					}
				}
			}
		}
		
		return map;
	}
	
	private Boolean[] canGiveXp(Player p, Material m, ElySkill s, Material itemInHand, String neededItem){
		
		Boolean[] results = new Boolean[]{false, true};
		
		if (containsKey(m) && get(m).hasSkill(s)){
			if (get(m).hasLevel(s, main.api.getDivPlayer(p).getLevel(s))){
				if (neededItem.equals("none") || isHolding(p, neededItem) || (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasLore() && isHolding(p, "arrow"))){
					results[0] = true;
				}
			}
		}
		
		if (containsKey(itemInHand)){
			if (!get(itemInHand).canUseTool(s, itemInHand, main.api.getDivPlayer(p).getLevel(s))){
				results[1] = false;
			}
		}

		return results;
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFall(EntityDamageEvent e){
		
		if (e.getCause() == DamageCause.FALL && e.getEntity() instanceof Player){
			if (((Damageable)e.getEntity()).getHealth() > 0){
				main.api.event(new SkillExpGainEvent((Player)e.getEntity(), ElySkill.ENDURANCE, Integer.parseInt(Math.round(e.getDamage()*5) + "")));
				e.setDamage(e.getDamage() - (e.getDamage()*((main.api.getDivPlayer((Player)e.getEntity()).getLevel(ElySkill.ENDURANCE)*.4)/100)));
			}
		}
	}
	
	public void checkDrax(Player p, DivinityPlayer dp, String label, int color, int maxChance){
		if (new Random().nextInt(maxChance) == 1){
			DivinityUtilsModule.bc(p.getDisplayName() + " &7has found a " + label + " drax shard!");
			ItemStack shard = DivInvManager.createItem(main.AS("&fDraX Shard"), new String[]{main.AS("&c&oUsed to create (hA0s!")}, Material.STAINED_CLAY, 1, color);
			p.getWorld().dropItem(p.getLocation(), shard);
			main.api.getDivSystem().addEffect("renewal" + p.getName(), ParticleEffect.RED_DUST, 0, 10, 0, 1, 200, p.getLocation(), 16, 1);
			main.api.getDivSystem().addEffect("renewal2" + p.getName(), ParticleEffect.PORTAL, 2, 2, 2, 1, 200, p.getLocation(), 16, 1);
			main.api.repeat(new SpellTasks(main), "renewal", 0L, 40L, "renewal3" + p.getName(), main, p);
			main.api.schedule(new SpellTasks(main), "cancelRenewal", 400L, "renewalCancel", main, p);
		}
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onMob(EntityDamageByEntityEvent e){
		
		if (e.getEntity() instanceof Monster && e.getDamager() instanceof Arrow){
			Arrow pro = (Arrow) e.getDamager();
			if (pro.getShooter() instanceof Player){
				DivinityPlayer dp = main.api.getDivPlayer((Player)pro.getShooter());
				e.setDamage(e.getDamage() + (e.getDamage()*((dp.getLevel(ElySkill.ARCHERY)*.4)/100)));
				main.api.event(new SkillExpGainEvent(((Player)pro.getShooter()), ElySkill.ARCHERY, Integer.parseInt(Math.round(e.getDamage()*5) + "")));
			}
		}
		
		if (e.getEntity() instanceof Player == false && e.getDamager() instanceof Player){
			
			Player p = (Player) e.getDamager();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			
			if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)){
				
				switch (p.getItemInHand().getType()){
				
					case ARROW:
						
						if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasLore()){
							if (new Random().nextInt(3) == 1){
								e.getEntity().setFireTicks(200);
							}
							main.api.event(new SkillExpGainEvent(p, ElySkill.CHAOS, Integer.parseInt(Math.round(e.getDamage()*50) + "")));
							e.setDamage(e.getDamage() + (e.getDamage()*((dp.getLevel(ElySkill.CHAOS)*.8)/100)));
						}
						
					break;
				
					case STICK: case FENCE:
						main.api.event(new SkillExpGainEvent(p, ElySkill.FENCING, Integer.parseInt(Math.round(e.getDamage()*7) + "")));
						e.setDamage(e.getDamage() + (e.getDamage()*((dp.getLevel(ElySkill.FENCING)*.8)/100)));
					break;
					
					case DIAMOND_AXE: case IRON_AXE: case WOOD_AXE: case GOLD_AXE: case STONE_AXE:
						
						e.setDamage(e.getDamage() + (e.getDamage()*((dp.getLevel(ElySkill.AXES)*.4)/100)));
						main.api.event(new SkillExpGainEvent(p, ElySkill.AXES, Integer.parseInt(Math.round(e.getDamage()*5) + "")));
						
					break;
					
					default:
						
						e.setDamage(e.getDamage() + (e.getDamage()*((dp.getLevel(ElySkill.ATTACK)*.4)/100)));
						main.api.event(new SkillExpGainEvent(p, ElySkill.ATTACK, Integer.parseInt(Math.round(e.getDamage()*5) + "")));
						
					break;
				}
				
			} else {
				main.api.event(new SkillExpGainEvent(p, ElySkill.ATTACK, Integer.parseInt(Math.round(e.getDamage()*3) + "")));
			}
			
			if (dp.getBool(MMO.IS_SOUL_SPLITTING)){
				main.api.event(new SkillExpGainEvent(p, ElySkill.VAMPYRISM, Integer.parseInt(Math.round(e.getDamage()*7) + "")));
				p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 99999, dp.getInt(MMO.VAMP_MULT)));
				e.setDamage(e.getDamage()*5);
				if (dp.getInt(MMO.VAMP_MULT) < 5){
					dp.set(MMO.VAMP_MULT, dp.getInt(MMO.VAMP_MULT)+1);
				}
			} else {
				if (new Random().nextInt(100) <= dp.getLevel(ElySkill.VAMPYRISM)*0.2){
					if (((Damageable) p).getHealth() < 20){
						if (e.getDamage() + ((Damageable) p).getHealth() > 20){
							p.setHealth(20);
						} else {
							p.setHealth(((Damageable) p).getHealth() + (e.getDamage()/2));
						}
					}
				}
			}
			
		} else if (e.getEntity() instanceof Player && e.getDamager() instanceof Player == false){
			
			Player p = (Player) e.getEntity();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			main.api.event(new SkillExpGainEvent(p, ElySkill.RESISTANCE, Integer.parseInt(Math.round(e.getDamage()*7) + "")));
			
			if (dp.getBool(MMO.IS_SOUL_SPLITTING)){
				p.setFoodLevel(20);
				p.setSaturation(20);
				e.setDamage(0);
				main.api.event(new SkillExpGainEvent(p, ElySkill.VAMPYRISM, Integer.parseInt(Math.round(e.getDamage()*5) + "")));
			} else {	
				e.setDamage(e.getDamage() - (e.getDamage()*((dp.getLevel(ElySkill.RESISTANCE)*.4)/100)));
			}
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e){
		
		if (e.getWhoClicked() instanceof Player){
			
			Player p = (Player) e.getWhoClicked();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			
			if (canGiveXp(p, e.getCurrentItem().getType(), ElySkill.CRAFTING, Material.AIR, "none")[0]){
				main.api.event(new SkillExpGainEvent((Player) e.getWhoClicked(), ElySkill.CRAFTING, get(e.getCurrentItem().getType()).getXP(ElySkill.CRAFTING)*e.getCurrentItem().getAmount()));
				if (new Random().nextInt(100) <= main.api.getDivPlayer(p).getLevel(ElySkill.CRAFTING)*0.2){
					if (dp.getLong(DPI.CRAFT_COOLDOWN) <= System.currentTimeMillis()){
						p.getInventory().addItem(e.getCurrentItem());
						main.s(p, "Extra item crafted! Your current chance is &6" + main.api.getDivPlayer(p).getLevel(ElySkill.CRAFTING)*0.2 + "%&b.");
						dp.set(DPI.CRAFT_COOLDOWN, System.currentTimeMillis() + 7200000L);
					} else {
						dp.err("Extra item not crafted - cooldown of " + ((System.currentTimeMillis() - (dp.getLong(DPI.CRAFT_COOLDOWN))/1000)/60)*-1 + " minutes remain.");
					}
				}
			}
		}
	}
	
	private String getDesc(ElySkill skill){
		
		switch (skill){
		
			default: return "&c&oNo skill desc found.";
		
			case WOODCUTTING: return "&6Chop logs to increase your WC level!";
			case MINING: return "&6Mine various ores of your level to increase!";
			case ATTACK: return "&6Murder mobs with a weapon of your level!";
			case FENCING: return "&6Bash mob's faces in with a stick! It's fun I swear!";
			case DIGGING: return "&6It's like mining except with dirt and snow.";
			case ARCHERY: return "&6Shoot stuff with a bow.";
			case CRAFTING: return "&6Craft some items to increase this level.";
			case VAMPYRISM: return "&6Collect blood by fighting monsters - and unleash the power of the night!";
			case RESISTANCE: return "&6Take a lot of damage - it'll make you take less as you level!";
			case ENDURANCE: return "&6JUMP OFF OF CLIFFS, BUT DON'T DIE!\n&6This decreases fall damage as you level.";
			case BUILDING: return "&6You just place stuff. Pretty easy. What, you want a medal or something?";
			case FARMING: return "&6The best skill to get 99 in. Tear down crops.";
			case PATROL: return "&6Hunt or skill with a group of people and share the XP!";
			case SOLAR: return "&6Destructive spells!";
			case LUNAR: return "&6Group-based healing & help skills!";
			case AXES: return "&6Attack mobs with an axe!";
			case CHAOS: return "&6Attack mobs with your hamdrax. Be careful!";
		}
	}
	
	private String getPerks(ElySkill skill){
		
		switch (skill){
		
			default: return "&c&oNo skill perk found.";
		
			case WOODCUTTING: return "&bLevel 10: &6TREE FELLER (right-click axe)\n&7&oInstantly break an entire tree.\n&7&oEvery level decreases cooldown by 1 second.";
			case MINING: return "&bLevel 10: &6SUPER BREAKER (right-click pick)\n&7&oObtain max mining speed.\n&7&oEvery level decreases cooldown by 1 second.";
			case ATTACK: return "&bLevel 10: &6SKY BLADE (right-click sword)\n&7&oAn AOE monster attack.\n&7&oEvery level decreases cooldown by 1 second.\n&bExtra damage increase (0.3%) per level.";
			case FENCING: return "&bExtra damage increase (0.8%) per level.";
			case DIGGING: return "&6Level 10: &6TURBO DRILL (right-click spade)\n&7&oEvery level decreases cooldown by 1 second.";
			case ARCHERY: return "&6Ability coming soon\n&b0.4% extra damage per level";
			case CRAFTING: return "&60.2% chance per level to craft an extra item.\n&7&o2 hour cooldown on success.";
			case VAMPYRISM: return "&a0.2% per level to heal half of what you hit\n&60.5 extra seconds to soul split per level.\n&7&oEach level makes it harder to fill your blood meter.\n&bDuring soul split:\n&bFood regain on damage taken\n&bHealth increase on damage given\n&6&oVampyire vial recipie:\n&a3x flesh\n&aredstone, apple, redstone\n&ax3 spider eye";
			case RESISTANCE: return "&60.4% less damage taken per level.";
			case ENDURANCE: return "&60.4% less fall damage taken per level.";
			case BUILDING: return "&6You literally get nothing for leveling this skill. Nothing.";
			case FARMING: return "&bLevel 10: &6LIFE FORCE (right-click sapling)\n&7&oPlants a random tree.\n&7&oEvery level decreases cooldown by 1 second.";
			case PATROL: return "&6More Shop Options\nCOMING SOON(TM)";
			case SOLAR: return "&6Level up for new spells!\n&3&o0.4% damage increase per level";
			case LUNAR: return "&6Level up for new spells!";
			case AXES: return "&bLevel 10: &6BEZERK (right-click axe on mob)\n&7&oAdd a bleed effect on a mob.\n&7&oEvery level decreases cooldown by 1 second.\n&60.3% extra damage per level";
			case CHAOS: return "&bLevel 10: &6(hA0s\n&7&ow3Ap0n a (ha0S Pur3 of p0wEr\n&7&oEvery level decreases cooldown by 1 second.\n&60.3% extra damage per level";
		}
	}
	
	@GameCommand(name = "Stats", aliases = {"stats", "gstats"}, desc = "MMO Stat Viewer", help = "/stats <player> or /gstats <skill/total>", player = true, min = 1)
	public void onStats(Player p, String[] args, String cmd){
		
		if (cmd.equals("gstats")){
			gStats(p, args[0]);
		} else {
		
			String snow = "&3\u2744 ";
			
			if (main.api.doesPartialPlayerExist(args[0])){
				
				DivinityPlayer dp = main.api.getDivPlayer(args[0]);
				JSONChatMessage msg = new JSONChatMessage("", null, null);
				main.s(p, "Skill Overview For " + dp.getStr(DPI.DISPLAY_NAME));
				p.sendMessage("");

				for (ElySkill skill : ElySkill.values()){
					
					msg = new JSONChatMessage("", null, null);
					int lvl = dp.getLevel(skill);
					int xp = dp.getXP(skill);
					int neededXp = dp.getNeededXP(skill);
					
					JSONChatExtra extra = new JSONChatExtra(main.AS("&6>> &b" + skill.s() + " &3(&6" + lvl + "&3) (&6" + xp + "&3)"));
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&3" + skill.s() + "\n&f----- " + snow + "&f-----\n&bLVL: &6" + lvl + "&b/&699\n&bXP: &6" + xp + "\n&bNEXT LVL: &6" + neededXp));
					msg.addExtra(extra);
					main.s(p, msg);
					msg = new JSONChatMessage("", null, null);

					extra = new JSONChatExtra(main.AS("&3(&f- &3I &f-&3) "));
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(getDesc(skill)));
					msg.addExtra(extra);
					
					extra = new JSONChatExtra(main.AS("&3(&f- &3X &f-&3) "));
					
					String message = "&3&oXP Generation Methods\n";
					
					for (MXP m : values()){
						if (m.hasSkill(skill)){
							message = m.getXP(skill) > 0 ? message + "&6" + m.getMat().name().toLowerCase() + "&f: " + (lvl >= m.getNeededLevel(skill) ? "&a" : "&c") + "Level " + m.getNeededLevel(skill) + ", &b" + m.getXP(skill) + " xp.\n" : message;
						}
					}
					
					if (!message.equals("&3&oXP Generation Methods\n")){
						extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(message));
						msg.addExtra(extra);
					}
					
					Map<Material, Integer> map = getTools(skill);
					message = "";
					
					for (Material tool : map.keySet()){
						message = (message.equals("") ? "&3&oExtra Drops Tool Requirement" : message) + "\n" + "&b" + tool.toString().toLowerCase() + "&f: " + (lvl >= map.get(tool) ? "&a" : "&c") + map.get(tool);
					}
					
					if (!message.equals("")){
						extra = new JSONChatExtra(main.AS("&3(&f- &3T &f-&3) "));
						extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(message + " "));
						msg.addExtra(extra);
					}
					
					extra = new JSONChatExtra(main.AS("&3(&f- &3P &f-&3)"));
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(getPerks(skill) + "\n&7&oAll gathering skills have a chance to drop double.\n&7&oThis chance increases by 0.4% per level."));
					msg.addExtra(extra);
					main.s(p, msg);
					p.sendMessage("");
				}
				
				main.s(p, "&7&oHover over everything for more info...");
				
			} else {
				main.s(p, "&c&oThat player does not exist.");
			}
		}
	}
	
	private void gStats(Player p, String s){
		
		ElySkill checkSkill = null;
		String skillList = "";
		
		for (ElySkill skill : ElySkill.values()){
			if (skill.s().equalsIgnoreCase(s)){
				checkSkill = skill;
			}
			skillList = skillList.equals("") ? "&6" + skill.s() : skillList + "&7, &6" + skill.s();
		}
		
		if (!s.equalsIgnoreCase("total") && checkSkill == null){
			main.s(p, "&c&oInvalid skill. Choose from...");
			main.s(p, skillList + "&7, &6total");
			return;
		}
		
		Map<Integer, List<DivinityStorageModule>> players = new THashMap<>();
		
		for (DivinityStorageModule dp : main.api.getOnlineModules().values()){
			
			if (dp.getTable().equals("users")){
				int total = 0;
				
				for (ElySkill skill : ElySkill.values()){
					total = (checkSkill != null && checkSkill.equals(skill)) || s.equals("total") ? total + ((DivinityPlayer)dp).getLevel(skill) : total;
				}
				
				if (!players.containsKey(total)){
					players.put(total, new ArrayList<DivinityStorageModule>(Arrays.asList(dp)));
				} else {
					players.get(total).add(dp);
				}
			}
		}
		
		List<Integer> values = new ArrayList<Integer>();
		
		for (Integer i : players.keySet()){
			values.add(i);
		}
		
		Collections.sort(values);
		Collections.reverse(values);
		
		main.s(p, "&3Top 20 Players - &6" + s.toUpperCase());
		
		for (int i = 0; i < (values.size() >= 20 ? 20 : values.size()); i++){

			JSONChatMessage msg = new JSONChatMessage(main.AS("&3\u2744 "), null, null);
			JSONChatExtra extra = null;
			int loops = 0;
			
			for (DivinityStorageModule ds : players.get(values.get(i))){

				String hoverText = "&3Skill Layout";
				int total = 0;
				
				for (ElySkill skill : ElySkill.values()){
					total = (checkSkill != null && checkSkill.equals(skill)) || s.equals("total") ? total + ((DivinityPlayer)ds).getLevel(skill) : total;
					hoverText = hoverText + "\n&b" + skill.s() + "&f: &6" + ((DivinityPlayer)ds).getLevel(skill);
				}
				
				extra = new JSONChatExtra(main.AS(ds.getStr(DPI.DISPLAY_NAME) + " &b(&6" + total + "&b) &3\u2744 "), null, null);
				extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(hoverText));
				msg.addExtra(extra);
				
				loops++;
				
				if (loops >= 3){
					break;
				}
			}
			
			main.s(p, msg);
		}
		
		p.sendMessage(main.AS("&7&oHover for full skill layout."));
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent e){
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		Location l = e.getBlock().getLocation();
		Material itemInHand = p.getItemInHand() != null ? p.getItemInHand().getType() : Material.AIR;
		boolean cont = true;
		
		if (main.api.getDivSystem().getList(MMO.INVALID_BLOCKS).contains(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ())){
			main.api.getDivSystem().getList(MMO.INVALID_BLOCKS).remove(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
			cont = false;
		}
		
		List<ElySkill> skills = Arrays.asList(ElySkill.WOODCUTTING, ElySkill.MINING, ElySkill.DIGGING, ElySkill.FARMING);
		List<String> skillTools = Arrays.asList("_axe", "_pick", "_spade", "none");
		
		for (int i = 0; i < skills.size(); i++){
			
			Boolean[] results = canGiveXp(p, e.getBlock().getType(), skills.get(i), itemInHand, skillTools.get(i));
			
			if (results[0]){
				if (skills.get(i).equals(ElySkill.FARMING)){
					cont = true;
				}
			}
			
			if (cont && results[0]){
				main.api.event(new SkillExpGainEvent(p, skills.get(i), get(e.getBlock().getType()).getXP(skills.get(i))));
				if (results[1]){
					if (new Random().nextInt(101) < (dp.getLevel(skills.get(i))*0.3)){
						p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(e.getBlock().getType()));
					}
				}
			} else {
				checkForDrax(p);
			}
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlace(BlockPlaceEvent e){

		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (isHolding(p, "sapling") && dp.getBool(MMO.IS_LIFE_FORCING)){
			e.setCancelled(true);
			life.l(p, dp, p.getLocation());
		}
		
		Location l = e.getBlock().getLocation();
		main.api.event(new SkillExpGainEvent(e.getPlayer(), ElySkill.BUILDING, 100));
		main.api.getDivSystem().getList(MMO.INVALID_BLOCKS).add(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
	}
	
	public boolean isHolding(Player p, String item){
		return p.getItemInHand() != null && p.getItemInHand().getType().toString().toLowerCase().contains(item.toLowerCase());
	}
	
	public boolean isType(Location l, String item){
		return l.getBlock() != null && l.getBlock().getType().toString().toLowerCase().contains(item.toLowerCase());
	}
	
	public boolean isType(Block b, String item){
		return b != null && b.getType().toString().toLowerCase().contains(item.toLowerCase());
	}
	
	@EventHandler
	public void onHand(PlayerItemHeldEvent e){
		
		boolean cont = false;
		
		if (e.getNewSlot() < 9){
			Inventory inv = e.getPlayer().getInventory();
			ItemStack i = inv.getItem(e.getNewSlot());
			if (i != null){
				if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore()){
					if (i.getItemMeta().getDisplayName().contains("dRaX")){
						e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 5000, false));
						cont = true;
					}
				}
			}
		}
		
		if (!cont){
			checkForDrax(e.getPlayer());
		}
	}
	
	public void checkForDrax(Player p){
		if (!p.getItemInHand().hasItemMeta() || !p.getItemInHand().getItemMeta().hasDisplayName() || p.getItemInHand().getItemMeta().getDisplayName().contains("dRax")){
			if (p.hasPotionEffect(PotionEffectType.FAST_DIGGING)){
				for (PotionEffect eff : p.getActivePotionEffects()){
					if (eff.getType().equals(PotionEffectType.FAST_DIGGING)){
						if (eff.getAmplifier() == 5000){
							p.removePotionEffect(PotionEffectType.FAST_DIGGING);
						}
						break;
					}
				}
			}
		}
	}
	
	//lvl xp xp_needed
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onXp(SkillExpGainEvent e){
		
		if (e.getXp() == 0 || e.isCancelled() || (!e.getPlayer().getWorld().getName().equals("world") && !e.getPlayer().getWorld().getName().equals("world_nether") && !e.getPlayer().getWorld().getName().equals("world_the_end"))){
			checkForDrax(e.getPlayer());
			return;
		}
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (dp.getBool(DPI.IN_GAME)){
			return;
		}
		
		switch (e.getSkill()){
			case ATTACK: case AXES: case FENCING:
				checkDrax(p, dp, "red", 14, 1000);
			break;
			
			case ARCHERY:
				checkDrax(p, dp, "green", 13, 200);
			break;
			
			case MINING:
				checkDrax(p, dp, "white", 0, 2000);
			break;
			
			case DIGGING: case FARMING:
				checkDrax(p, dp, "brown", 12, 2000);
			break;
			
			case WOODCUTTING:
				checkDrax(p, dp, "lime", 5, 500);
			break;
			
			default: break;
		}
		
		if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasLore() && p.getItemInHand().getItemMeta().hasDisplayName()){
			if (p.getItemInHand().getItemMeta().getDisplayName().contains("dRaX")){
				ItemMeta im = p.getItemInHand().getItemMeta();
				List<String> lore = new ArrayList<String>(im.getLore());
				int amtLeft = Integer.parseInt(lore.get(1).substring(4).split("\\/")[0]);
				amtLeft--;
				if (amtLeft <= 0){
					p.setItemInHand(new ItemStack(Material.AIR));
					dp.err("Your drax has broken.");
				} else {
					if (amtLeft == 3500){
						dp.err("Your drax has 50% of its charges remaining.");
					} else if (amtLeft == 700){
						dp.err("Your drax has 10% of its charges remaining.");
					}
					lore.remove(1);
					lore.add(main.AS("&a&o" + amtLeft + "/7000"));
					im.setLore(lore);
					p.getItemInHand().setItemMeta(im);
				}
				p.updateInventory();
			} else {
				checkForDrax(p);
			}
		} else {
			checkForDrax(p);
		}
		
		e.setXp(dp.getBool(DPI.IGNORE_XP) ? e.getXp() : e.getXp()*2); // Added for balancing - current curve way too high.
		
		String[] results = dp.getStr(e.getSkill()).split(" ");
		int level = Integer.parseInt(results[0]);
		
		if (level < 99){
			
			double needed = Double.parseDouble(results[2]);
			int xp = Integer.parseInt(results[1]) + (level >= 70 ? e.getXp() + Math.round(e.getXp()/4) : e.getXp()) + 20;
			dp.set(e.getSkill(), level + " " + xp + " " + needed);
			
			if (!((ElyLogger)main.api.getInstance(ElyLogger.class)).protectedMats.contains(p.getItemInHand().getType()) && !e.getSkill().equals(ElySkill.BUILDING) && p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR) && dp.getBool(DPI.XP_DISP_NAME_TOGGLE)){
				
				boolean cont = true;
				
				if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()){
					
					cont = false;
					
					for (ElySkill skill : ElySkill.values()){
						if (p.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains(skill.toString().toLowerCase())){
							cont = true;
							break;
						}
					}
				}
				
				if (cont){
					ItemMeta im = e.getPlayer().getItemInHand().getItemMeta();
					im.setDisplayName(main.AS("&b&o" + e.getSkill().s() + " &6&o" + xp + "&b&o/&6&o" + Math.round(needed)));
					p.getItemInHand().setItemMeta(im);
				}
			}
			
			dp.set(DPI.LAST_ELYSS_SKILL, e.getSkill().s().substring(0, 1) + e.getSkill().s().toLowerCase().substring(1));
			main.api.event(new ScoreboardUpdateEvent(p, "move"));
			
			if (xp >= needed){
				needed = needed + (needed*.10) + (500*level);
				dp.set(e.getSkill(), (level+1) + " " + xp + " " + needed);
				dp.s("Your &6" + e.getSkill().s() + " &blevel is now &6" + (level+1) + "&b!");
				main.fw(p.getWorld(), p.getLocation(), Type.BALL, DivinityUtilsModule.getRandomColor());
				
				if ((level+1) % 10 == 0 || level == 98){
					DivinityUtilsModule.bc(p.getDisplayName() + " &bhas reached &6" + e.getSkill().s() + " &blevel &6" + (level+1) + "&b!");
				}
				
				if (level == 98){
					dp.s("WELL DONE! You've reached the max level in this skill!");
					((ElyEffects) main.api.getInstance(ElyEffects.class)).playCircleFw(p, DivinityUtilsModule.getRandomColor(), Type.BALL_LARGE, 5, 1, 0, true, false);
				}
			}
		}
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e){
		
		if (e.getEntity() instanceof Player == false && e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player){
			
			Player p = e.getEntity().getKiller();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			
			if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR) && containsKey(p.getItemInHand().getType()) && get(p.getItemInHand().getType()).canUseTool(ElySkill.ATTACK, p.getItemInHand().getType(), main.api.getDivPlayer(p).getLevel(ElySkill.ATTACK))){
				if (new Random().nextInt(100) <= dp.getLevel(ElySkill.ATTACK)*0.4){
					List<ItemStack> drops = new ArrayList<ItemStack>();
					for (ItemStack i : e.getDrops()){
						if (i != null){
							drops.add(i);
						}
					}
					for (ItemStack i : drops){
						p.getWorld().dropItemNaturally(p.getLocation(), i);
					}
				}
			}
			
			if (dp.getInt(MMO.VAMP_BAR) < 100 && !dp.getBool(MMO.IS_SOUL_SPLITTING)){
				dp.set(MMO.VAMP_BAR, dp.getInt(MMO.VAMP_BAR)+2);
				main.api.event(new ScoreboardUpdateEvent(p));
				if (dp.getInt(MMO.VAMP_BAR) == 100){
					dp.s("You're ready to unleash the power of the night!");
					dp.s("You'll need a vampyre vial though...");
				}
			}
		}
	}
	
	@EventHandler
	public void onSplash(PotionSplashEvent e){
		
		ItemStack i = e.getPotion().getItem();
		
		if (i.hasItemMeta() && i.getItemMeta().hasLore() && i.getItemMeta().getLore().contains(main.AS("&c&oDrink up!"))){
			for (LivingEntity ent : e.getAffectedEntities()){
				if (ent instanceof Player){
					DivinityPlayer dp = main.api.getDivPlayer((Player)ent);
					if (dp.getInt(MMO.VAMP_BAR) >= 100){
						soulSplit.start((Player)ent, dp);
					} else {
						dp.err("SoulSplit is not ready!");
						e.setCancelled(true);
					}
				}
			}
		} else if (i.hasItemMeta() && i.getItemMeta().hasLore() && i.getItemMeta().getLore().contains(main.AS("&9&oOh I wonder where you'll go..."))){
			for (LivingEntity ent : e.getAffectedEntities()){
				if (ent instanceof Player){
					Location l = ent.getLocation();
					Random rand = new Random();
					int x = rand.nextInt(2) == 1 ? l.getBlockX() + rand.nextInt(15) : l.getBlockX() - rand.nextInt(15);
					int z = rand.nextInt(2) == 1 ? l.getBlockZ() + rand.nextInt(15) : l.getBlockZ() - rand.nextInt(15);
					for (int ii = l.getBlockY(); ii < 256; ii++){
						if (new Location(l.getWorld(), x, ii, z).getBlock().getType().equals(Material.AIR)){
							main.api.event(new DivinityTeleportEvent((Player)ent, new Location(l.getWorld(), x, ii, z, l.getYaw(), l.getPitch())));
							break;
						}
					}
				}
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		
		for (String user : noPickup.keySet()){
			for (Item i : noPickup.get(user)){
				if (i.equals(e.getItem())){
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEntityEvent e){
		
		if (e.isCancelled()){
			return;
		}
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (isHolding(p, "_sword") && dp.getBool(MMO.IS_SKY_BLADING)){
			skyBlade.l(p, dp);
		}
		
		if (isHolding(p, "_axe") && dp.getBool(MMO.IS_BEZERK)){
			bezerk.l(p, dp, (LivingEntity) e.getRightClicked());
		}
		
		if (isHolding(p, "arrow") && dp.getBool(MMO.IS_CHAOSING)){
			if (e.getRightClicked() instanceof Player == false){
				List<LivingEntity> entList = null;
				if (dp.getRawInfo(DPI.CHAOS_LIST) != null && !dp.getRawInfo(DPI.CHAOS_LIST).equals("none")){
					entList = (List<LivingEntity>) dp.getRawInfo(DPI.CHAOS_LIST);
				} else {
					entList = new ArrayList<LivingEntity>();
				}
				entList.add((LivingEntity) e.getRightClicked());
				dp.set(DPI.CHAOS_LIST, entList);
				dp.s("Add another mob!");
			}
		} else if (dp.getBool(MMO.IS_CHAOS) && isHolding(p, "arrow")){
			chaos.l(p, dp, (LivingEntity) e.getRightClicked()); 
		}
	}
	
	public void laser(Player p, SmallFireball snowball){
		if (!snowball.isDead()){
			ParticleEffect.RED_DUST.display(0, 0, 0, 0, 300, snowball.getLocation(), 16);
		} else {
			main.api.cancelTask(potions.get(snowball));
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent e){
		
		if (e.isCancelled() && e.getAction() != Action.RIGHT_CLICK_AIR){
			return;
		}
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		Block b = e.getClickedBlock() != null ? e.getClickedBlock() : null;
		
		switch (e.getAction()){
		
			default: break;
			
			case RIGHT_CLICK_AIR:
				
				if (isHolding(p, "_axe") && dp.getLevel(ElySkill.WOODCUTTING) >= 10 && !p.isSneaking()){
					treeFeller.r(p, dp);
				}
				
				if (isHolding(p, "_axe") && dp.getLevel(ElySkill.AXES) >= 10 && p.isSneaking()){
					bezerk.r(p, dp);
				}
				
				if (isHolding(p, "_pickaxe") && dp.getLevel(ElySkill.MINING) >= 10){
					superBreaker.r(p, dp, MMO.IS_SUPER_BREAKING);
				}
				
				if (isHolding(p, "_spade") && dp.getLevel(ElySkill.DIGGING) >= 10){
					superBreaker.r(p, dp, MMO.IS_TURBO_DRILLING);
				}
				
				if (isHolding(p, "_sword") && dp.getLevel(ElySkill.ATTACK) >= 10){
					skyBlade.r(p, dp);
				}
				
				if (isHolding(p, "sapling") && dp.getLevel(ElySkill.FARMING) >= 10){
					life.r(p, dp);
				}
				
				if (isHolding(p, "arrow") && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasLore() && dp.getLevel(ElySkill.CHAOS) >= 10){
					chaos.r(p, dp);
				}
				
				// nothing suspicious move along
				if (p.getItemInHand().getType().equals(Material.CAKE) || p.getItemInHand().getType().equals(Material.CAKE_BLOCK)){
					dp.err("This cake is a lie, you should ask for your money back.");
					p.getWorld().dropItem(p.getLocation(), p.getItemInHand());
					p.setItemInHand(new ItemStack(Material.AIR));
					p.playSound(p.getLocation(), Sound.CLICK, 5F, 5F);
				}
				
			break;
			
			case LEFT_CLICK_BLOCK:
				
				if (isType(e.getClickedBlock(), "log") && isHolding(p, "_axe") && dp.getBool(MMO.IS_TREE_FELLING)){
					treeFeller.l(p, dp, b);
				}
				
				if (isHolding(p, "_pickaxe") && dp.getBool(MMO.IS_SUPER_BREAKING)){
					superBreaker.l(p, dp, b, MMO.IS_SUPER_BREAKING, MMO.IS_MINING, MMO.SUPER_BREAKER_CD, ElySkill.MINING);
				}
				
				if (isHolding(p, "_spade") && dp.getBool(MMO.IS_TURBO_DRILLING)){
					superBreaker.l(p, dp, b, MMO.IS_TURBO_DRILLING, MMO.IS_DIGGING, MMO.TURBO_DRILL_CD, ElySkill.DIGGING);
				}
				
			break;
		}
	}
}