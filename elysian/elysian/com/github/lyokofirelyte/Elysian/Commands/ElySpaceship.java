package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElySpaceship implements Listener, AutoRegister<ElySpaceship> {
	
	private Elysian main;
	
	@Getter
	private ElySpaceship type = this;
	
	public ElySpaceship(Elysian i){
		main = i;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@DivCommand(aliases = {"ss"}, perm = "wa.staff.admin", help = "/ss", desc = "SPAAAACCEEE (ship)")
	public void onSS(final Player p, final String[] args){
		
		final DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (args.length >= 1){
			
			switch(args[0]){
			
				case "tp":

					Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){
						for (FallingBlock b : (List<FallingBlock>)dp.getRawInfo(DPI.GV2)){
							if (b.getPassenger() != null){
								b.eject();
							}
							Location l = b.getLocation();
							b.teleport(new Location(b.getLocation().getWorld(), l.getX()+Double.parseDouble(args[1]), l.getY()+Double.parseDouble(args[2]), l.getZ()+Double.parseDouble(args[3])));
							for (String player : dp.getList(DPI.GV5)){
								if (!main.api.getPlayer(player).isInsideVehicle()){
									b.setPassenger(main.api.getPlayer(player));
									break;
								}
							}
						}
					}}, 40L);
					
				break;
			
				case "passengers":
				
					dp.set(DPI.GV5, new ArrayList<String>());
					
					for (int i = 1; i < args.length; i++){
						if (args.length > 1){
							if (main.api.isOnline(args[i])){
								dp.getList(DPI.GV5).add(args[i]);
								main.s(p, "Passenger list adjusted!");
							}
						}
					}
				
				break;
			
				case "toggle":
					dp.set(DPI.GV1, !dp.getBool(DPI.GV1));
					main.s(p, "Place mode " + dp.getStr(DPI.GV1).replace("true", "active").replace("false", "inactive"));
				break;
				
				case "clear":
					
					Bukkit.getScheduler().cancelTask(main.api.getDivSystem().getInt(DPI.GV1));
					dp.set(DPI.GV2, new ArrayList<FallingBlock>());
					dp.set(DPI.GV3, new ArrayList<String>());
					dp.set(DPI.GV4, new ArrayList<String>());
					dp.set(DPI.GV5, new ArrayList<String>());
					main.s(p, "Cleared!");
					
				break;
				
				case "land":
					
					land(dp);
					
				break;
				
				case "activate":
					
					List<String> relatives = new ArrayList<String>();
					dp.set(DPI.GV2, new ArrayList<FallingBlock>());
					List<Player> passengers = new ArrayList<Player>();
					
					for (String player : dp.getList(DPI.GV5)){
						if (main.api.isOnline(player)){
							passengers.add(main.api.getPlayer(player));
						} else {
							dp.getList(DPI.GV5).remove(player);
						}
					}
					
					for (String s : dp.getList(DPI.GV3)){
						String[] split = s.split(" ");
						int x = Integer.parseInt(split[0]);
						int y = Integer.parseInt(split[1]);
						int z = Integer.parseInt(split[2]);
						String mat = split[3];
						byte type = Byte.parseByte(split[4]);
						
						new Location(p.getWorld(), x, y, z).getBlock().setType(Material.AIR);
						FallingBlock b = p.getWorld().spawnFallingBlock(new Location(p.getWorld(), x, y+10, z), Material.valueOf(mat.toUpperCase()), type);
						((List<FallingBlock>)dp.getRawInfo(DPI.GV2)).add(b);
						
						for (Player player : passengers){
							if (!player.isInsideVehicle()){
								b.setPassenger(player);
								break;
							}
						}
					}
					
					dp.set(DPI.GV4, relatives);
					task(dp, passengers, p);
					
				break;
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void land(DivinityPlayer dp){
		
		Bukkit.getScheduler().cancelTask(main.api.getDivSystem().getInt(DPI.GV1));
		dp.set(DPI.GV3, new ArrayList<String>());
		
		for (FallingBlock b : (List<FallingBlock>)dp.getRawInfo(DPI.GV2)){
			if (!b.isDead()){
				if (b.getPassenger() != null){
					b.eject();
				}
				Location l = new Location(b.getLocation().getWorld(), b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ());
				Material mat = b.getMaterial();
				byte type = b.getBlockData();
				b.remove();
				l.getBlock().setTypeIdAndData(mat.getId(), type, true);
				Vector v = l.toVector();
				dp.getList(DPI.GV3).add(v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ() + " " + mat.name().toLowerCase() + " " + type);
			}
		}
	}
	
	private void task(final DivinityPlayer dp, final List<Player> passengers, final Player p){
		main.api.getDivSystem().set(DPI.GV1, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){ @SuppressWarnings({ "unchecked", "deprecation" })
		public void run(){

			List<FallingBlock> newBlocks = new ArrayList<FallingBlock>();
			
			for (FallingBlock b : (List<FallingBlock>)dp.getRawInfo(DPI.GV2)){
				if (!b.isDead()){
					Location l = b.getLocation();
					newBlocks.add(p.getWorld().spawnFallingBlock(new Location(b.getWorld(), l.getX(), l.getY()+3, l.getZ()), b.getMaterial(), b.getBlockData()));
					b.remove();
				}
			}
			
			dp.set(DPI.GV2, newBlocks);
			
			int x = 0;
			
			for (FallingBlock b : newBlocks){
				if (x < passengers.size()){
					try {
						b.setPassenger(passengers.get(x));
						x++;
					} catch (Exception e){}
				}
			}
			
		}}, 200L, 200L));
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		
		if (e.getPlayer().getItemInHand().getType().equals(Material.STICK)){
			DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
			if (dp.getList(DPI.GV5).size() > 0){
				if (e.getAction() == Action.RIGHT_CLICK_AIR){
					for (FallingBlock b : ((List<FallingBlock>)dp.getRawInfo(DPI.GV2))){
						b.setVelocity(e.getPlayer().getLocation().getDirection().multiply(2));
					}
				} else {
					for (FallingBlock b : ((List<FallingBlock>)dp.getRawInfo(DPI.GV2))){
						b.setVelocity(new Vector(0, 0, 0));
					}
				}
			}
		} else if (e.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_SWORD) && e.getAction() == Action.RIGHT_CLICK_AIR){
			DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
			if (dp.getList(DPI.GV5).size() > 0){
				land(dp);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (dp.getBool(DPI.GV1)){
			Vector v = e.getBlock().getLocation().toVector();
			Block b = e.getBlock();
			if (dp.getList(DPI.GV3).contains(v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ() + " " + b.getType().name().toLowerCase() + " " + b.getData())){
				try {
					dp.getList(DPI.GV3).remove(v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ() + " " + b.getType().name().toLowerCase() + " " + b.getData());
				} catch (Exception ee){}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (dp.getBool(DPI.GV1)){
			Vector v = e.getBlock().getLocation().toVector();
			Block b = e.getBlock();
			if (!dp.getList(DPI.GV3).contains(v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ() + " " + b.getType().name().toLowerCase() + " " + b.getData())){
				dp.getList(DPI.GV3).add(v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ() + " " + b.getType().name().toLowerCase() + " " + b.getData());
			}
		}
	}
}