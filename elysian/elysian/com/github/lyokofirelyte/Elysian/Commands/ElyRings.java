package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Gui.GuiRingFuel;
import com.github.lyokofirelyte.Elysian.Gui.GuiRingFuelSafe;
import com.github.lyokofirelyte.Elysian.Gui.GuiRings;
import com.github.lyokofirelyte.Elysian.api.RingsType;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Database.DRS;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityRing;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Gui.DivInvManager;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;

public class ElyRings implements Listener, AutoRegister<ElyRings> {

	private Elysian main;
	
	@Getter
	private ElyRings type = this;
	
	public ElyRings(Elysian i){
		main = i;
	}
	
	@SuppressWarnings("deprecation")
	@DivCommand(aliases = {"rings"}, desc = "Elysian Ring Transport System Command", help = "/rings help", player = true, min = 1)
	public void onRings(Player p, String[] args){
		
		args[1] = args[1].toLowerCase();
		
		switch (args[0]){
		
			case "add":
				
				if (!main.api.doesRingExist(args[1])){
					
					ItemStack i = p.getItemInHand();
					DivinityRing ring = main.api.getDivRing(args[1]);
					Location l = p.getLocation().getBlock().getLocation();
					Vector v = l.toVector();

					if (args.length >= 3 && args[2].equals("server")){
						ring.set(DRS.IS_SERVER_RING, true);
					} else {
						ring.set(DRS.CENTER, l.getWorld().getName() + " " + v.getBlockX() + " " + (v.getBlockY()-1) + " " + v.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
						ring.set(DRS.MAT_ID, i.getType().getId());
						ring.set(DRS.BYTE_ID, i.getData().getData());
						ring.set(DRS.DEST, "none");
						ring.set(DRS.IS_ALLIANCE_OWNED, args.length >= 3);
						ring.set(DRS.ALLIANCE, (args.length >= 3 && main.api.doesRegionExist(args[2].toLowerCase()) ? args[2].toLowerCase() : "none"));
					}
					
					main.s(p, "Added!");
					
				} else {
					main.s(p, "&c&oThat ring already exists!");
				}
				
			break;
			
			case "remove":
				
				if (main.api.doesRingExist(args[1])){
					main.api.getOnlineModules().remove("RING_" + args[1].toLowerCase());
					main.s(p, "&c&oDeleted!");
				} else {
					main.s(p, "&c&oThat ring does not exist!");
				}
				
			break;
			
			case "help":
				
				main.s(p, "/rings add <name>, /rings remove <name>, /rings add <alliance> <alliance>, /rings add <name> server");
				
			break;
		}
	}
	
	private void calculateAlliance(final Player p, final DivinityRing currentRing, final DivinityRing destRing, Location destLoc){
		
		final DivinityPlayer dp = main.api.getDivPlayer(p);
		List<Material> mats = new ArrayList<Material>(Arrays.asList(Material.COAL, Material.COAL_BLOCK, Material.COAL_ORE));
		List<ItemStack> toRemove = new ArrayList<ItemStack>();
		int amt = 0;
		
		if (destRing.isAllianceOwned()){
			for (ItemStack i : destRing.getStack(DRS.FUEL)){
				if (i != null && mats.contains(i.getType())){
					amt += i.getAmount()*(i.getType().equals(Material.COAL_BLOCK) ? 9 : 1);
					toRemove.add(i);
					if (amt >= 64){
						break;
					}
					if (i.getAmount() <= 0){
						i.setType(Material.AIR);
					}
				}
			}
		} else {
			amt = 64;
		}
		
		if (amt < 64){
			main.s(p, "&c&oInsufficient fuel in the destination ring (" + amt + "/64)!");
		} else {
			
			itemloop:
			for (ItemStack i : toRemove){
				for (int x = 0; x < new Integer(i.getAmount()); x++){
					amt--;
					i.setAmount(i.getAmount() - 1);
					if (amt == 0){
						break itemloop;
					}
				}
			}

			final Vector v = destRing.getCenterLoc().toVector().subtract(currentRing.getCenterLoc().toVector());
			Location pLoc = p.getLocation();
			final int destX = destRing.getCenterLoc().getBlockX();
			final int destY = destRing.getCenterLoc().getBlockY();
			final int destZ = destRing.getCenterLoc().getBlockZ();
			final boolean greaterX = pLoc.getBlockX() < destX;
			final boolean greaterZ = pLoc.getBlockZ() < destZ;
			List<Entity> ents = p.getNearbyEntities(5D,  5D, 5D);
			ents.add(p);
			
			for (Entity e : ents){
				if (e instanceof Player){
					Player player = (Player) e;
					DivinityPlayer divPlayer = main.api.getDivPlayer(player);
					divPlayer.set(DPI.RING_LOC, p.getLocation());
					divPlayer.set(DPI.DISABLED, true);
					player.setFlySpeed(0);
					player.setGameMode(GameMode.SPECTATOR);
					main.api.repeat(this, "fly", 0L, 5L, "ring_task_" + player.getName(), player, divPlayer, v, destX, destY, destZ, greaterX, greaterZ);
				}
			}
		}
	}
	
	public void fly(Player p, DivinityPlayer dp, Vector v, int destX, int destY, int destZ, boolean greaterX, boolean greaterZ){
		
		boolean cont1 = false;
		boolean cont2 = false;
		
		p.setVelocity(v.clone().multiply(0.001));
		
		if (greaterX){
			if (p.getLocation().getBlockX() >= destX || destX - p.getLocation().getBlockX() <= 7){
				cont1 = true;
			}
		} else {
			if (p.getLocation().getBlockX() <= destX || p.getLocation().getBlockX() - destX <= 7){
				cont1 = true;
			}
		}
		
		if (greaterZ){
			if (p.getLocation().getBlockZ() >= destZ || destZ - p.getLocation().getBlockZ() <= 7){
				cont2 = true;
			}
		} else {
			if (p.getLocation().getBlockZ() <= destZ || p.getLocation().getBlockZ() - destZ <= 7){
				cont2 = true;
			}
		}
		
		if (cont1 && cont2){
			dp.set(DPI.RING_LOC, "none");
			main.api.cancelTask("ring_task_" + p.getName());
			main.s(p, "Thank you for flying Air Elysian.");
			p.setGameMode(GameMode.SURVIVAL);
			p.teleport(new Location(p.getWorld(), destX, destY+1, destZ, p.getLocation().getYaw(), p.getLocation().getPitch()));
			p.setVelocity(new Vector(0, 0, 0));
			p.setFlySpeed(0.2f);
			dp.set(DPI.DISABLED, false);
		}
	}

	public void calculate(Player p, Vector v, String destination, String ring, boolean tp){
		
		if (tp){
			p.setGameMode(GameMode.SURVIVAL);
		}
		
		List<Player> players = new ArrayList<Player>(Arrays.asList(p));
		
		for (Entity e : p.getNearbyEntities(5D, 5D, 5D)){
			if (e instanceof Player == false && (e instanceof Monster || e instanceof Player == false)){
				e.remove();
			} else if (e instanceof Player){
				players.add((Player) e);
			}
		}

		DivinityRing currentRing = main.api.getDivRing(ring);
		DivinityRing dest = main.api.getDivRing(destination);
		
		String[] destString = dest.getCenter();
		Location destLoc = new Location(Bukkit.getWorld(destString[0]), d(destString[1]), d(destString[2]), d(destString[3]), f(destString[4]), f(destString[5]));
		
		if (currentRing.getCenterLoc().getWorld().getName().equals(dest.getCenterLoc().getWorld().getName()) && tp && (dest.isAllianceOwned() || currentRing.isAllianceOwned())){
			calculateAlliance(p, currentRing, dest, destLoc);
			return;
		}
		
		currentRing.setInOperation(true);
		dest.setInOperation(true);
		
		int startX = v.getBlockX();
		int startY = v.getBlockY();
		int startZ = v.getBlockZ();
		
		List<Location> horLocs = new ArrayList<Location>();
		List<Location> verLocs = new ArrayList<Location>();
		
		for (int i = 0; i < 3; i++){
			horLocs.add(new Location(p.getWorld(), startX + 3, startY + (i*2), startZ));
			horLocs.add(new Location(p.getWorld(), startX - 3, startY + (i*2), startZ));
			verLocs.add(new Location(p.getWorld(), startX, startY + (i*2), startZ + 3));
			verLocs.add(new Location(p.getWorld(), startX, startY + (i*2), startZ - 3));
		}

		for (Location l : horLocs){
			main.api.getDivSystem().addEffect(l.getX() + " " + l.getY() + " " + l.getZ() + "_" + currentRing.getName() + "_ring", ParticleEffect.RED_DUST, 0, 0, 2, 0, 100, l, 16, 5);
		}
		
		for (Location l : verLocs){
			main.api.getDivSystem().addEffect(l.getX() + " " + l.getY() + " " + l.getZ() + "_" + currentRing.getName() + "_ring", ParticleEffect.RED_DUST, 2, 0, 0, 0, 100, l, 16, 5);
		}
		
		main.api.getDivSystem().addEffect(currentRing.getName() + "_ring", ParticleEffect.PORTAL, 5, 5, 5, 1, 5000, currentRing.getCenterLoc(), 16, 10);
		
		if (tp){
			main.api.schedule(this, "lightning", 60L, "bleh", currentRing.getCenterLoc());
			main.api.schedule(this, "release", 120L, "lol", horLocs, verLocs, currentRing, dest, players);
		} else {
			main.api.schedule(this, "finish", 60L, "finish", horLocs, verLocs, currentRing, dest);
		}
	}
	
	public void finish(List<Location> horLocs, List<Location> verLocs, DivinityRing currentRing, DivinityRing dest){
		
		for (Location l : horLocs){
			main.api.getDivSystem().remEffect(l.getX() + " " + l.getY() + " " + l.getZ() + "_" + currentRing.getName() + "_ring");
		}
		
		for (Location l : verLocs){
			main.api.getDivSystem().remEffect(l.getX() + " " + l.getY() + " " + l.getZ() + "_" + currentRing.getName() + "_ring");
		}
		
		main.api.getDivSystem().remEffect(currentRing.getName() + "_ring");
		currentRing.setInOperation(false);
		dest.setInOperation(false);
	}
	
	public void lightning(final Location l){
		
		final Random rand = new Random();
		
		for (int i = 1; i <= 5; i++){
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
				public void run(){
					l.getWorld().strikeLightningEffect(new Location(l.getWorld(), l.getX() + rand.nextInt(3) * (rand.nextInt(2) == 0 ? 1 : -1), l.getY(), l.getZ() + rand.nextInt(3) * (rand.nextInt(2) == 0 ? 1 : -1)));
				}
			}, 10L*i);
		}
	}
	
	public void release(List<Location> horLocs, List<Location> verLocs, DivinityRing currentRing, DivinityRing dest, List<Player> players){
		
		for (Player p : players){
			main.api.event(new DivinityTeleportEvent(p, dest.getCenterLoc()));
		}
		
		for (Location l : horLocs){
			main.api.getDivSystem().remEffect(l.getX() + " " + l.getY() + " " + l.getZ() + "_" + currentRing.getName() + "_ring");
		}
		
		for (Location l : verLocs){
			main.api.getDivSystem().remEffect(l.getX() + " " + l.getY() + " " + l.getZ() + "_" + currentRing.getName() + "_ring");
		}
		
		main.api.getDivSystem().remEffect(currentRing.getName() + "_ring");
		calculate(players.get(0), dest.getCenterLoc().toVector(), currentRing.getName(), dest.getName(), false);
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e){
		
		if (e.getView().getTitle().contains(" Fuel")){
			DivinityRing ring = main.api.getDivRing(e.getView().getTitle().split(" ")[0]);
			List<ItemStack> items = new ArrayList<ItemStack>();
			for (ItemStack i : e.getInventory().getContents()){
				if (i != null && (i.getType().equals(Material.COAL) || i.getType().equals(Material.COAL_BLOCK) || i.getType().equals(Material.COAL_ORE))){
					items.add(i);
				} else if (i != null){
					e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), i);
				}
			}
			ring.set(DRS.FUEL, items);
		}
		
		if (e.getView().getTitle().contains("Deposit to")){
			DivinityRing ring = main.api.getDivRing(e.getView().getTitle().split(" ")[2]);
			List<ItemStack> items = ring.getStack(DRS.FUEL);
			for (ItemStack i : e.getInventory().getContents()){
				if (i != null && (i.getType().equals(Material.COAL) || i.getType().equals(Material.COAL_BLOCK) || i.getType().equals(Material.COAL_ORE))){
					items.add(i);
				} else if (i != null){
					e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), i);
				}
			}
			ring.set(DRS.FUEL, items);
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e){
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
			
			Vector v = e.getClickedBlock().getLocation().toVector();
			String[] clickedLoc = (e.getClickedBlock().getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ()).split(" ");
			
			for (DivinityStorageModule r : main.api.getOnlineModules().values()){
				if (r.getTable().equals("rings")){
					DivinityRing ring = (DivinityRing) r;
					if (!ring.getBool(DRS.IS_SERVER_RING) && ring.getCenter()[0].equals(clickedLoc[0]) && ring.getCenter()[1].equals(clickedLoc[1]) && ring.getCenter()[2].equals(clickedLoc[2]) && ring.getCenter()[3].equals(clickedLoc[3])){
						if (!ring.isInOperation()){
							if (e.getPlayer().isSneaking() && ring.isAllianceOwned()){
								DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
								if (dp.getStr(DPI.ALLIANCE_NAME).equals(ring.getStr(DRS.ALLIANCE))){
									((DivInvManager) main.api.getInstance(DivInvManager.class)).displayGui(e.getPlayer(), new GuiRingFuel(main, ring));
								} else {
									((DivInvManager) main.api.getInstance(DivInvManager.class)).displayGui(e.getPlayer(), new GuiRingFuelSafe(main, ring));
								}
							} else {
								main.s(e.getPlayer(), "Select!");
								((DivInvManager) main.api.getInstance(DivInvManager.class)).displayGui(e.getPlayer(), new GuiRings(main, v, ring.getName(), RingsType.MENU));
							}
						} else {
							main.s(e.getPlayer(), "&c&oRing already in operation!");
						}
						return;
					}
				}
			}
		}
	}
	
	private double d(String s){
		return Double.parseDouble(s);
	}
	
	private float f(String s){
		return Float.parseFloat(s);
	}
	
	private Location l(String w, int x, int y, int z){
		return new Location(Bukkit.getWorld(w), x, y, z);
	}
	
	private Location l(World w, double x, double y, double z){
		return new Location(w, x, y, z);
	}
}