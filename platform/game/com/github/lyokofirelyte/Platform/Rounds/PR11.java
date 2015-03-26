 package com.github.lyokofirelyte.Platform.Rounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.github.lyokofirelyte.Platform.Change;
import com.github.lyokofirelyte.Platform.Platform;
import com.github.lyokofirelyte.Platform.Events.RoundEndEvent;

public class PR11 extends PlatformRound {

	public PR11(Platform i) {
		super(i);
	}

	@Override
	public void start() {
		
		long delay = 0L;
		Random rand = new Random();
		
		List<Integer> grids = new ArrayList<Integer>();
		
		int safeSection = rand.nextInt(64);
		int sel = 0;
		
		for (int i = 0; i < 64; i++){
			if (i != safeSection){
				grids.add(i);
			}
		}
		
		Collections.shuffle(grids);
		
		while (grids.size() > sel){
			
			for (int y = 0; y < 4; y++, sel++){
				main.pah.changeLater(Change.GRID, grids.get(sel), Material.WOOL, 4, delay);
			}
				
			delay+= 40L;
				
			for (int y = 0; y < 4; y++, sel++){
				main.pah.changeLater(Change.GRID, grids.get(grids.size() > sel ? sel : grids.size()-1), Material.WOOL, 14, delay);
			}
				
			delay+= 40L;
			
			for (int y = 0; y < 4; y++, sel++){
				main.pah.changeLater(Change.GRID, grids.get(grids.size() > sel ? sel : grids.size()-1), Material.AIR, 0, delay);
			}
				
			delay+= 70L;
		}
				
		delay+= 40L;
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){ restate(); }}, delay);
	}
	
	public void restate(){
		
		main.gMsg("&bEnd of game so far! Hope you enjoyed.");
		main.gameData.setActive(false);
		
		long delay = 60L;
		
		for (int y = 1; y < 65; y++){
			main.pah.changeLater(Change.GRID, y, Material.STAINED_GLASS, 4, delay);
			delay+= 2L;
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){ 
			
			main.gMsg("&bReturning to lobby in 10 seconds...");
			main.setSecondsLeft(10);
			
		}}, delay);
		
		delay += 200L;

		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){ 
			
			end();
			
		}}, delay);
	}
	
	@Override
	public void end(){
		main.getApi().sendAllToServer("GameServer");
	}
}