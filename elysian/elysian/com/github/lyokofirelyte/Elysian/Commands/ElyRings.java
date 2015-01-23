package com.github.lyokofirelyte.Elysian.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
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

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Gui.GuiRingFuel;
import com.github.lyokofirelyte.Elysian.Gui.GuiRingFuelSafe;
import com.github.lyokofirelyte.Elysian.Gui.GuiRings;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.DRS;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRing;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityStorage;

public class ElyRings implements Listener, AutoRegister {

	private Elysian main;
	
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
					
					ring.set(DRS.CENTER, l.getWorld().getName() + " " + v.getBlockX() + " " + (v.getBlockY()-1) + " " + v.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
					ring.set(DRS.MAT_ID, i.getType().getId());
					ring.set(DRS.BYTE_ID, i.getData().getData());
					ring.set(DRS.DEST, "none");
					ring.set(DRS.IS_ALLIANCE_OWNED, args.length >= 3);
					ring.set(DRS.ALLIANCE, (args.length >= 3 && main.api.doesRegionExist(args[2].toLowerCase()) ? args[2].toLowerCase() : "none"));
					
					main.s(p, "Added!");
					
				} else {
					main.s(p, "&c&oThat ring already exists!");
				}
				
			break;
			
			case "remove":
				
				if (main.api.doesRingExist(args[1])){
					main.divinity.api.divManager.getMap(DivinityManager.ringsDir).remove(args[1]);
					new File("./plugins/Divinity/rings/" + args[1].toLowerCase() + ".yml").delete();
					main.s(p, "&c&oDeleted!");
				} else {
					main.s(p, "&c&oThat ring does not exist!");
				}
				
			break;
			
			case "help":
				
				main.s(p, "/rings add <name>, /rings remove <name>, /rings add <alliance> <alliance>.");
				
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
			if (p.getLocation().getBlockX() >= destX || destX - p.getLocation().getBlockX() <= 5){
				cont1 = true;
			}
		} else {
			if (p.getLocation().getBlockX() <= destX || p.getLocation().getBlockX() - destX <= 5){
				cont1 = true;
			}
		}
		
		if (greaterZ){
			if (p.getLocation().getBlockX() >= destZ || destZ - p.getLocation().getBlockZ() <= 5){
				cont2 = true;
			}
		} else {
			if (p.getLocation().getBlockX() <= destZ || p.getLocation().getBlockX() - destZ <= 5){
				cont2 = true;
			}
		}
		
		if (cont1 && cont2){
			main.api.cancelTask("ring_task_" + p.getName());
			main.s(p, "Destination reached! (ish)");
			p.setGameMode(GameMode.SURVIVAL);
			p.teleport(new Location(p.getWorld(), destX, destY, destZ, p.getLocation().getYaw(), p.getLocation().getPitch()));
			p.setVelocity(new Vector(0, 0, 0));
			p.setFlySpeed(0.2f);
			dp.set(DPI.DISABLED, false);
			dp.set(DPI.RING_LOC, "none");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void calculate(Player p, Vector v, String destination, String ring, boolean tp){
		
		 for (Entity e : p.getNearbyEntities(5D, 5D, 5D)){
			 if (e instanceof Player == false && (e instanceof Monster || e instanceof Player == false)){
				 e.remove();
			 }
		 }

		DivinityRing currentRing = main.api.getDivRing(ring);
		DivinityRing dest = main.api.getDivRing(destination);
		
		String[] destString = dest.getCenter();
		String startWorld = p.getWorld().getName();
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
	
		Map<Integer, List<Location>> locs = new THashMap<Integer, List<Location>>();
		Map<Integer, List<FallingBlock>> blocks = new THashMap<Integer, List<FallingBlock>>();
		
		for (int i = 0; i < 3; i++){
			locs.put(i, new ArrayList<Location>());
			blocks.put(i, new ArrayList<FallingBlock>());
			
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ));
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ));
			locs.get(i).add(l(startWorld, startX, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX, startY-(i*2), startZ-3));
			
			locs.get(i).add(l(startWorld, startX+2, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX+2, startY-(i*2), startZ-3));
			
			locs.get(i).add(l(startWorld, startX-2, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX-2, startY-(i*2), startZ-3));
			
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ+2));
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ-2));
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ-3));
			
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ+2));
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ-2));
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ-3));
		}
		
		for (List<Location> loc : locs.values()){
			for (Location l : loc){
				l.getBlock().setType(Material.AIR);
				l(l.getWorld(), l.getX(), l.getY()-1, l.getZ()).getBlock().setType(Material.AIR);
			}
		}
		
		for (Location l : locs.get(0)){
			FallingBlock b = l.getWorld().spawnFallingBlock(l, currentRing.getMatId(), currentRing.getMatByte());
			b.setVelocity(new Vector(0, 1, 0));
			blocks.get(0).add(b);
		}
		
		p.getWorld().playSound(p.getLocation(), Sound.BLAZE_HIT, 5F, 5F);
		main.api.repeat(this, "checkLocs", 0L, 1L, "checkLocs" + locs.get(0).get(0).getY()+6, blocks.get(0), (double)locs.get(0).get(0).getY()+6, locs.get(0), p, tp, destLoc);
		main.api.schedule(this, "scheduleLocs", 5L, "scheduleLocs", locs.get(1), currentRing.getMatId(), currentRing.getMatByte(), locs.get(0).get(0).getY()+4, tp, destLoc, p);
		main.api.schedule(this, "scheduleLocs", 15L, "scheduleLocs2", locs.get(2), currentRing.getMatId(), currentRing.getMatByte(), locs.get(0).get(0).getY()+2, tp, destLoc, p);
		main.api.schedule(this, "endLocs", 90L, "ending", p, destLoc, tp, currentRing, dest);
		main.api.schedule(this, "release", 100L, "release", currentRing, dest, tp, p, locs);
	}
	
	@SuppressWarnings("deprecation")
	public void release(DivinityRing r1, DivinityRing r2, boolean tp, Player p, Map<Integer, List<Location>> map){
		
		if (!tp){
			r1.setInOperation(false);
			r2.setInOperation(false);
		}
		
		for (Location l : map.get(2)){
			l.getBlock().setTypeIdAndData(r1.getMatId(), r1.getMatByte(), true);
			l(l.getWorld(), l.getX(), l.getY()+1, l.getZ()).getBlock().setTypeIdAndData(r1.getMatId(), r1.getMatByte(), true);
		}
		
		for (int i = 2; i > -1; i--){
			for (Location l : map.get(i)){
				Location upperL = new Location(l.getWorld(), l.getX(), l.getY() + 6, l.getZ());
				int id = upperL.getBlock().getTypeId();
				byte byt = upperL.getBlock().getData();
				upperL.getBlock().setType(Material.AIR);
				upperL.getWorld().spawnFallingBlock(upperL, id, byt);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void scheduleLocs(List<Location> locs, int matId, byte id, double y, boolean tp, Location dest, Player p){
		
		List<FallingBlock> bList = new ArrayList<FallingBlock>();
		p.getWorld().playSound(p.getLocation(), Sound.BLAZE_HIT, 5F, 5F);
		
		for (Location l : locs){
			FallingBlock b = l.getWorld().spawnFallingBlock(l, matId, id);
			b.setVelocity(new Vector(0, 1, 0));
			bList.add(b);
		}
		
		main.api.repeat(this, "checkLocs", 0L, 1L, "checkLocs" + y, bList, (double)y, locs, p, tp, dest);
	}
	
	@SuppressWarnings("deprecation")
	public void checkLocs(List<FallingBlock> locs, double max, List<Location> blockLocs, Player p, boolean tp, Location dest){
		
		int dead = 0;
		
		for (FallingBlock b : locs){
			if (!b.isDead() && b.getLocation().getY() >= max){
				Location l = b.getLocation();
				int id = b.getMaterial().getId();
				byte by = b.getBlockData();
				b.remove();
				new Location(l.getWorld(), l.getX(), max, l.getZ()).getBlock().setTypeIdAndData(id, by, true);
			} else if (b.isDead() || b == null){
				dead++;
			}
		}
		
		if (dead >= locs.size()){
			main.api.cancelTask("checkLocs" + max);
		}
	}

	public void endLocs(Player p, Location dest, boolean tp, DivinityRing currentRing, DivinityRing destRing){
		
		if (tp){
			((ElyEffects) main.api.getInstance(ElyEffects.class)).playCircleFw(p, Color.WHITE, Type.BALL_LARGE, 5, 1, 0, true, false);
			main.api.schedule(this, "endLocs2", 5L, "ending2", p, dest, currentRing, destRing);
		}
	}

	public void endLocs2(Player p, Location dest, DivinityRing r1, DivinityRing r2){
		
		for (Entity e : p.getNearbyEntities(3D, 3D, 3D)){
			if (e instanceof ItemStack == false && e instanceof FallingBlock == false){
				e.teleport(new Location(dest.getWorld(), dest.getX() + new Random().nextInt(3), dest.getY()+1, dest.getZ() + new Random().nextInt(3), e.getLocation().getYaw(), e.getLocation().getPitch()));
			}
		}
		
		p.teleport(new Location(dest.getWorld(), dest.getX() + new Random().nextInt(3), dest.getY()+1, dest.getZ() + new Random().nextInt(3), p.getLocation().getYaw(), p.getLocation().getPitch()));
		((ElyEffects) main.api.getInstance(ElyEffects.class)).playCircleFw(p, Color.WHITE, Type.BALL_LARGE, 5, 1, 0, true, false);
		calculate(p, r2.getCenterLoc().toVector(), r1.name(), r2.name(), false);
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
			
			for (DivinityStorage r : main.divinity.api.divManager.getMap(DivinityManager.ringsDir).values()){
				DivinityRing ring = (DivinityRing) r;
				if (ring.getCenter()[0].equals(clickedLoc[0]) && ring.getCenter()[1].equals(clickedLoc[1]) && ring.getCenter()[2].equals(clickedLoc[2]) && ring.getCenter()[3].equals(clickedLoc[3])){
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
							((DivInvManager) main.api.getInstance(DivInvManager.class)).displayGui(e.getPlayer(), new GuiRings(main, v, ring.name()));
						}
					} else {
						main.s(e.getPlayer(), "&c&oRing already in operation!");
					}
					return;
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