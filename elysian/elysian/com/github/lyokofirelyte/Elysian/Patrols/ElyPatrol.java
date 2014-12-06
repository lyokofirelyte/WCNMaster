package com.github.lyokofirelyte.Elysian.Patrols;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.ElyLogger;
import com.github.lyokofirelyte.Spectral.DataTypes.DPS;
import com.github.lyokofirelyte.Spectral.DataTypes.DRF;
import com.github.lyokofirelyte.Spectral.DataTypes.DRI;
import com.github.lyokofirelyte.Spectral.DataTypes.DivHashMap;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.DivGame;
import com.github.lyokofirelyte.Spectral.Public.ParticleEffect;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRegion;

public class ElyPatrol extends DivHashMap<DPS, Object> implements AutoRegister, Runnable, DivGame {

	public Elysian main;
	
	public ElyLogger logger;
	public PatrolEvents events;
	public PatrolMobs mobs;
	
	private boolean isFinding = false;
	
	public ElyPatrol(Elysian i){
		main = i;
		mobs = new PatrolMobs(this);
		events = new PatrolEvents(this);
	}
	
	@Override
	public void run(){
		
		logger = (ElyLogger) main.api.getInstance(ElyLogger.class);
		
		if (!isFinding){
			isFinding = true;
			checkArea();
		}
	}
	
	public void checkArea(){
		
		boolean OK = false;
		Location finalLoc = null;
		Random rand = new Random();
		
		do {
			int x = rand.nextInt(4001) * (rand.nextInt(2) == 0 ? 1 : -1);
			int z = rand.nextInt(4001) * (rand.nextInt(2) == 0 ? 1 : -1);
			for (int i = 254; i > 10; i--){
				finalLoc = new Location(Bukkit.getWorld("world"), x, i, z);
				if (finalLoc != null && !finalLoc.getBlock().getType().equals(Material.AIR) && !finalLoc.getBlock().getType().equals(Material.WATER) && !finalLoc.getBlock().getType().equals(Material.STATIONARY_WATER)){
					Location thisLocUp = finalLoc.clone();
					thisLocUp.setY(finalLoc.getY()+1);
					if (thisLocUp.getBlock().getType().equals(Material.AIR)){
						thisLocUp.setY(finalLoc.getY()+2);
						if (thisLocUp.getBlock().getType().equals(Material.AIR)){
							OK = true;
							set(DPS.CENTER, finalLoc);
							break;
						}
					}
				}
			}
		} while (!OK);

		set(DPS.ENTRANCE_CRYSTAL, finalLoc.getWorld().spawnEntity(finalLoc, EntityType.ENDER_CRYSTAL));
		
		DivinityRegion region = main.api.getDivRegion("patrol_entrance");
		region.quickSet(true, DRF.FIRE_SPREAD, DRF.MELT, DRF.TNT_EXPLODE, DRF.TP_IN, DRF.TP_OUT);
		region.set(DRI.PERMS, Arrays.asList("wa.staff.admin"));
		region.set(DRI.PRIORITY, 100);
		region.set(DRI.HEIGHT, 400);
		region.set(DRI.LENGTH, 40);
		region.set(DRI.WIDTH, 40);
		region.set(DRI.AREA, 8000);
		region.set(DRI.WORLD, "world");
		region.set(DRI.MAX_BLOCK, (finalLoc.getBlockX()+30) + " " + finalLoc.getBlockY()+200 + " " + (finalLoc.getBlockZ()+30));
		region.set(DRI.MIN_BLOCK, (finalLoc.getBlockX()-30) + " " + (finalLoc.getBlockY()-200) + " " + (finalLoc.getBlockZ()-30));
		region.set(DRI.DISABLED, false);
		
		main.api.getDivSystem().remEffect("patrol_entrance");
		main.api.getDivSystem().addEffect("patrol_entrance", ParticleEffect.PORTAL, 5, 5, 5, 1, 2000, finalLoc, 20, 30L);
		
		main.api.repeat(events, "entranceTask", 0L, 200L, "patrolTask_entranceTask");
		isFinding = false;
		
		main.api.repeat(events, "checkPlayers", 0L, 100L, "patrolTask_checkPlayers");
		DivinityUtilsModule.bc("A new Patrol Enclave has been spotted near &6" + finalLoc.getBlockX() + "&b, &6" + (finalLoc.getBlockY()-5) + "&b, &6" + finalLoc.getBlockZ() + "&b!");
	}

	@Override
	public Object[] registerSubClasses(){
		return new Object[]{
			events,
			mobs
		};
	}

	@Override
	public DivinityGame toDivGame(){
		return null;
	}
}