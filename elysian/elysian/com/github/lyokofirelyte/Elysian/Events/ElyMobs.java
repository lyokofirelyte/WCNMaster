package com.github.lyokofirelyte.Elysian.Events;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Commands.ElyProtect;
import com.github.lyokofirelyte.Elysian.MMO.Magics.SpellTasks;
import com.github.lyokofirelyte.Elysian.api.ElyChannel;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.Gui.DivInvManager;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;

public class ElyMobs implements Listener, AutoRegister<ElyMobs> {
	
	private Elysian main;
	
	@Getter
	private ElyMobs type = this;
	
	public ElyMobs(Elysian i){
		main = i;
	}
	
	private String[] s(String s, String t){
		return new String[]{s, t};
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e){
	
		if (e.getEntity() instanceof LivingEntity && e.getEntity() instanceof Player == false){
			
			LivingEntity entity =  ((LivingEntity)e.getEntity());
			double hp = ((Damageable) entity).getHealth();
			double percent = (hp/((Damageable) entity).getMaxHealth())*100;
			String[] mult = percent >= 80 ? s("&a", "5") : percent >= 60 ? s("&a", "4") : percent >= 40 ? s("&e", "3") : percent >= 20 ? s("&c", "2") : s("&4", "1");
			entity.setCustomName(main.AS(mult[0] + StringUtils.repeat("\u2744", Integer.parseInt(mult[1]))));
			entity.setCustomNameVisible(true);
			main.api.schedule(this, "checkHealth", 200L, "mobNameCheck", entity);
			
		} else if (e.getEntity() instanceof Player){
			
			Player p = (Player) e.getEntity();
			LivingEntity attacker = null;
			
			if (main.api.getDivPlayer(p).getBool(DPI.IN_GAME)){
				return;
			}
			
			if (e.getDamager() instanceof Player){
				
				Player damager = (Player)e.getDamager();
				
				if (main.api.getDivPlayer(damager).getBool(DPI.DUEL_INVITE) && main.api.getDivPlayer(p).getBool(DPI.DUEL_INVITE)){
					return;
				}
				
				e.setCancelled(true);
				
			} else if (e.getDamager() instanceof Projectile){
				
				Projectile proj = (Projectile) e.getDamager();
				
				if (proj.getShooter() instanceof Player){
					
					Player damager = (Player) proj.getShooter();
					
					if (!main.api.getDivPlayer(damager).getStr(DPI.DUEL_PARTNER).equals(p.getName())){
						e.setCancelled(true);
					}
					
				} else if (proj.getShooter() instanceof LivingEntity){
					attacker = (LivingEntity) proj.getShooter();
				}
				
			} else if (e.getDamager() instanceof LivingEntity){
				attacker = (LivingEntity) e.getDamager();
			}
			
			if (attacker != null && attacker.hasMetadata("PatrolID") && (((Damageable) p).getHealth() - e.getDamage()) <= 0){
				main.api.event(new PatrolPlayerDeathEvent(p, attacker.getMetadata("PatrolID").get(0).asString()));
			}
			
			main.api.getDivPlayer(p).set(DPI.IN_COMBAT, true);
		}
	}
	
	public void checkHealth(LivingEntity e){
		if (!e.isDead()){
			e.setCustomNameVisible(false);
		}
	}
	
	public void eggBow(Item i){
		if (!i.isDead()){
			main.api.getDivSystem().playEffect(ParticleEffect.RED_DUST, 0, 10, 0, 1, 100, i.getLocation(), 16);
		} else {
			main.api.cancelTask("eggbow");
		}
	}
	
	private Location getDragonLoc(Entity e){
		
		Location dragonLoc = e.getLocation();
		Location l = null;
		
		for (int i = e.getLocation().getBlockY(); i > 1; i--){
			if (!(l = new Location(dragonLoc.getWorld(), dragonLoc.getBlockX(), i, dragonLoc.getBlockZ())).getBlock().getType().equals(Material.AIR)){
				l.setY(i+1);
				return l;
			}
		}
		
		return dragonLoc;
	}

	@EventHandler
	public void onMobDeath(final EntityDeathEvent e){
		
		if (e.getEntity().getKiller() instanceof Player){
			
			Player p = (Player) e.getEntity().getKiller();
			
			List<Material> bigDrops = Arrays.asList(
				Material.DRAGON_EGG,
				Material.SKULL_ITEM,
				Material.SKULL,
				Material.NETHER_STAR
			);
			
			for (ItemStack i : e.getDrops()){
				if (bigDrops.contains(i.getType())){
					DivinityUtilsModule.bc(p.getDisplayName() + " &bhas received a &6" + i.getType().toString() + " &bdrop!");
					if (!i.getType().equals(Material.DRAGON_EGG)){
						main.api.getDivSystem().addEffect("renewal" + p.getName(), ParticleEffect.RED_DUST, 0, 10, 0, 1, 200, e.getEntity().getLocation(), 16, 1);
						main.api.getDivSystem().addEffect("renewal2" + p.getName(), ParticleEffect.PORTAL, 2, 2, 2, 1, 200, e.getEntity().getLocation(), 16, 1);
						main.api.repeat(new SpellTasks(main), "renewal", 0L, 40L, "renewal3" + p.getName(), main, p);
						main.api.schedule(new SpellTasks(main), "cancelRenewal", 400L, "renewalCancel", main, p);
					}
				}
			}
			
			updateMobs(p, e.getEntity().getType().toString());
		}
		
		if (e.getEntity().getType().equals(EntityType.ENDER_DRAGON)){
			main.api.getDivSystem().set(DPI.ENDERDRAGON_DEAD, false);
			ItemStack egg = DivInvManager.createItem("&a&oDRAGON EGG", new String[]{ "&3&oIt's... so shiny!" }, Enchantment.DURABILITY, 10, Material.DRAGON_EGG, 1);
			Item i = e.getEntity().getWorld().dropItem(getDragonLoc(e.getEntity()), egg);
			main.api.repeat(this, "eggBow", 0L, 1L, "eggbow", i);
		}
		
		if (e.getEntity().getType().equals(EntityType.HORSE) && e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player){
			Location l = e.getEntity().getLocation();
			String coords = "&6" + l.getBlockX() + "&7, &6" + l.getBlockY() + "&7, &6" + l.getBlockZ();
			ElyChannel.STAFF.send("&6System", e.getEntity().getKiller().getDisplayName() + " &c&okilled a horse at " + coords + "&c.", main.api);
		}
		
		if (e.getEntity().getWorld().getName().equals("world")){
			
			if (e.getEntity() instanceof Monster && e.getEntity().getKiller() instanceof Player){
				
				new Thread(new Runnable(){ public void run(){
					
					Player p = (Player) e.getEntity().getKiller();
					
					for (String loc : DivinityUtilsModule.strCircle(e.getEntity().getLocation(), 15, 10, false, false, 0)){
						String[] l = loc.split(" ");
						Location location = new Location(Bukkit.getWorld(l[0]), Double.parseDouble(l[1]), Double.parseDouble(l[2]), Double.parseDouble(l[3]));
						if (location.getBlock() != null && location.getBlock().getType().equals(Material.MOB_SPAWNER)){
							return;
						}
					}
					
					payOut(p);
					
				}}).start();
				
			} else if (e.getEntity() instanceof Player && e.getEntity().getKiller() instanceof Player){
				
				Player dead = (Player)e.getEntity();
				Map<String, String> replacements = new THashMap<String, String>();
				
				DivinityPlayer killer = main.api.getDivPlayer((Player)e.getEntity().getKiller());
				DivinityPlayer deadDP = main.api.getDivPlayer((Player)e.getEntity());
				
				if (deadDP.getBool(DPI.IN_GAME)){
					return;
				}
				
				if (killer.equals(deadDP)){
					return;
				}
				
				List<String> wins = killer.getList(DPI.DUEL_WINS);
				
				for (String win : wins){
					if (win.split(" ")[0].equals(dead.getName())){
						replacements.put(win, win.replace(win.split(" ")[1], (Integer.parseInt(win.split(" ")[1])+1) + ""));
					}
				}
				
				if (replacements.size() > 0){
					for (String replace : replacements.keySet()){
						wins.remove(replace);
						wins.add(replacements.get(replace));
					}
				} else {
					wins.add(dead.getName() + " " + 1);
				}
				
				main.s(main.api.getPlayer(killer.getName()), "Well done! You've defeated " + dead.getDisplayName() + "&b.");
				main.s(dead, "&c&oBetter luck next time...");
				
				killer.set(DPI.DUEL_PARTNER, "none");
				deadDP.set(DPI.DUEL_PARTNER, "killed");
				
				DivinityUtilsModule.bc(dead.getDisplayName() + " &e&owas brutally murdered in a duel with " + killer.getStr(DPI.DISPLAY_NAME));
			}
			
			if (e.getEntity().hasMetadata("PatrolID")){
				main.api.event(new PatrolEntityDeathEvent(e));
			}
		}
	}
	
	private void updateMobs(Player p, String type){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		List<String> mobs = dp.getList(DPI.MOB_COUNTS);
		List<String> newMobs = new ArrayList<String>(mobs);
		boolean found = false;
		type = type.substring(0, 1) + type.toLowerCase().substring(1);
		
		dp.set(DPI.LAST_ELYSS_KILL, type);
		
		for (String mob : mobs){
			if (mob.split(" ")[0].equalsIgnoreCase(type)){
				newMobs.remove(mob);
				newMobs.add(mob.split(" ")[0] + " " + (Integer.parseInt(mob.split(" ")[1]) + 1));
				found = !found;
				break;
			}
		}
		
		if (!found){
			newMobs.add(type + " " + 1);
		}
		
		dp.set(DPI.MOB_COUNTS, newMobs);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		
		e.setDeathMessage(null);

		Vector v = e.getEntity().getLocation().toVector();
		Player p = e.getEntity();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (dp.getBool(DPI.IN_GAME)){
			return;
		}
		
		dp.getList(DPI.PREVIOUS_LOCATIONS).add(p.getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ());

		if (e.getEntity().getLastDamageCause() != null){
			DivinityUtilsModule.bc(dp.getStr(DPI.DISPLAY_NAME) + " &e&odied due to &6&o" + p.getLastDamageCause().getCause().name().toLowerCase());
		} else {
			DivinityUtilsModule.bc(dp.getStr(DPI.DISPLAY_NAME) + " &e&odied due to unknown causes.");
		}
		
		dp.set(DPI.IN_COMBAT, false);
		
		switch (e.getEntity().getWorld().getName()){
			case "world_the_end": case "world": case "world_nether": break;
			default: return;
		}
		
		if (!dp.getBool(DPI.DUEL_INVITE)){
			for (ItemStack i : p.getInventory().getContents()){
				if (i != null && !i.getType().equals(Material.AIR)){
					dp.getStack(DPI.DEATH_CHEST_INV).add(i);
				}
			}
			
			for (ItemStack i : p.getInventory().getArmorContents()){
				if (i != null && !i.getType().equals(Material.AIR)){
					dp.getStack(DPI.DEATH_CHEST_INV).add(i);
				}
			}
			
			dp.set(DPI.DEATH_CHEST_LOC, p.getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ());
			p.getLocation().getBlock().setType(Material.CHEST);
			e.getDrops().clear();
			
			main.s(e.getEntity(), "&7&oYour items are in a chest at your death location.");
			main.s(e.getEntity(), "&7&oRecent death chests have been merged with this one.");
		}
		
		if (dp.getBool(DPI.DEATHLOCS_TOGGLE)){
			main.s(p, "&7&oYou died at: &6&o" + dp.getStr(DPI.DEATH_CHEST_LOC).replace(" ", "&7, "));
		}
	}
	
	@EventHandler
	public void onExp(PlayerExpChangeEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (!dp.getBool(DPI.EXP_DEPOSIT)){
			if (e.getPlayer().getWorld().getName().equals("world") || e.getPlayer().getWorld().getName().equals("world_the_end") || e.getPlayer().getWorld().getName().equals("world_nether")){
				dp.set(DPI.EXP, dp.getInt(DPI.EXP) + new Integer(e.getAmount()));
				e.setAmount(0);
			}
		} else {
			dp.set(DPI.EXP_DEPOSIT, false);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		
		if (!dp.getBool(DPI.IN_GAME) && e.getPlayer().getBedSpawnLocation() == null){
			if (dp.getList(DPI.HOME).size() > 0){
				String[] h = dp.getList(DPI.HOME).get(0).split(" ");
				main.api.event(new DivinityTeleportEvent(e.getPlayer(), h[1], h[2], h[3], h[4], h[5], h[6]));
			}
		}
	}
	
	@EventHandler
	public void onPortal(EntityCreatePortalEvent e){

		if (e.getEntity().getType() == EntityType.ENDER_DRAGON){		
			e.setCancelled(true);
		}
	}
	
	private void payOut(Player p){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		Random rand = new Random();
		List<String> perms = dp.getList(DPI.PERMS);
		
		int mult = perms.contains("wa.rank.emperor") ? 3 : perms.contains("wa.rank.disctrictman") ? 2 : 1;
		int randomMoneyAmount = rand.nextInt(120) + 7;
		int randomNumber = rand.nextInt(4) + 1;
		int superRandom = rand.nextInt(1000);
		
		dp.set(DPI.MOB_MONEY, dp.getInt(DPI.MOB_MONEY) + (randomNumber == 4 ? randomMoneyAmount*mult : 5));
		dp.set(DPI.MOB_MONEY, dp.getInt(DPI.MOB_MONEY) + (superRandom == 500 ? 1000 : 0));
	}
	
	@DivCommand(aliases = {"exp", "xp"}, help = "/exp <take, store, send> <amount> <player (optional)>", desc = "Elysian EXP Storing System", player = true)
	public void onExp(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		int amt = args.length >= 2 && DivinityUtilsModule.isInteger(args[1]) ? Integer.parseInt(args[1]) : 0;
		
		if (args.length == 0){
			main.s(p, "Stored XP: &6" + dp.getInt(DPI.EXP) + "&b.");
			main.s(p, "&7&o1395 = level 30. Only take out what you need, as you can't put it back!");
		} else if (args.length == 2){
		
			if (args[0].equals("take")){
				
				if (dp.getInt(DPI.EXP) >= amt){
					dp.set(DPI.EXP, dp.getInt(DPI.EXP)-amt);
					dp.set(DPI.EXP_DEPOSIT, true);
					p.giveExp(amt);
					dp.set(DPI.EXP_DEPOSIT, false);
				} else {
					main.s(p, "&c&oNot enough stored xp!");
				}
				
			
				
				
			/*} else if (args[0].equals("store")){
				
				if (p.getTotalExperience() >= amt){
					int restore = new Integer(p.getTotalExperience() - amt);
					dp.set(DPI.EXP, dp.getInt(DPI.EXP) + amt);
					p.setTotalExperience(0);
					p.giveExp(restore);
				} else {
					dp.err("Not enough xp!");
				}*/
				
				
			}else {
				main.s(p, main.help("exp", this));
			}
			
		}else if(args.length == 3){
			
			if(args[0].equals("send")){
				if(main.api.doesPartialPlayerExist(args[2])){
					if(DivinityUtilsModule.isInteger(args[1])){
						int exp = Integer.parseInt(args[1]);
						
						if(exp < 0){
							main.s(p, "That number is negative!");
							return;
						}
						
						if(dp.getInt(DPI.EXP) >= exp){
							DivinityPlayer p2 = main.api.getDivPlayer(args[2]);
							dp.set(DPI.EXP, dp.getInt(DPI.EXP) - exp);
							p2.set(DPI.EXP, p2.getInt(DPI.EXP) + exp);
	
							main.s(p, "&6" + exp + " &bexperience sent to " + p2.getStr(DPI.DISPLAY_NAME));
							if(p2.isOnline()){
								p2.s("&6" + exp + " &bexperience received from " + dp.getStr(DPI.DISPLAY_NAME));
							}
						}else{
							main.s(p, "You do not have enough experience");
						}
						
						
						
					}else{
						main.s(p, "That is not a number!");
					}
					
				}else{
					main.s(p, "Player not found!");
				}
				
				
			}
			
		}else {
			main.s(p, main.help("exp", this));
		}
	}
	
	@DivCommand(aliases = {"duel", "pvp"}, help = "/pvp on, /pvp off", desc = "Elysian PVP System", player = true, min = 1)
	public void onDuel(Player p, String[] args){
		
		ElyProtect pro = (ElyProtect) main.api.getInstance(ElyProtect.class);
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (!dp.getBool(DPI.PVP_CHOICE) || pro.isInRegion(p.getLocation(), "newspawn")){

			dp.set(DPI.DUEL_INVITE, args[0].equals("on") ? true : false);
			main.s(p, "PVP " + (dp.getBool(DPI.DUEL_INVITE) + "").replace("true", "&aactivated").replace("false", "&cdeactivated") + ".");
			
			if (!dp.getBool(DPI.PVP_CHOICE)){
				dp.set(DPI.PVP_CHOICE, true);
				dp.set(DPI.DISABLED, false);
				((ElyJoinQuit) main.api.getInstance(ElyJoinQuit.class)).onJoin(new PlayerJoinEvent(p, "test"));
			}
		} else {
			main.s(p, "&c&oYou can only toggle this while at spawn!");
		}
	}
}