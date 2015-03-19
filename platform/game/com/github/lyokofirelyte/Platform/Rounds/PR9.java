package com.github.lyokofirelyte.Platform.Rounds;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Platform.Change;
import com.github.lyokofirelyte.Platform.Platform;
import com.github.lyokofirelyte.Platform.PlatformPlayer;
import com.github.lyokofirelyte.Platform.Events.RoundEndEvent;

public class PR9 extends PlatformRound {

	public PR9(Platform i) {
		super(i);
	}
	
	@Override
	public void start() {
		
		long delay = 0L;
		Random rand = new Random();
		Map<Integer, Integer> grids = new HashMap<Integer, Integer>();
		
		for (int x = 0; x < 17; x++){

			int pick = rand.nextInt(main.gameData.getRows().size()-1)+1;
			
			while (grids.containsValue(pick)){
				pick = rand.nextInt(main.gameData.getRows().size()-1)+1;
				if (!grids.containsValue(pick)){
					break;
				}
			}
			
			grids.put(x, pick);
			
			main.pah.changeLater(Change.ROW, grids.get(x), Material.WOOL, 4, delay);
			main.pah.changeLater(Change.COLUMN, grids.get(x), Material.WOOL, 4, delay);
			
			delay+= 40L;
			
			main.pah.changeLater(Change.ROW, grids.get(x), Material.WOOL, 14, delay);
			main.pah.changeLater(Change.COLUMN, grids.get(x), Material.WOOL, 14, delay);
			
			delay+= 40L;
			
			main.pah.changeLater(Change.ROW, grids.get(x), Material.AIR, 0, delay);
			main.pah.changeLater(Change.COLUMN, grids.get(x), Material.AIR, 0, delay);
		}
		
		delay+= 50L;
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){ restate(); }}, delay);
	}
	
	@SuppressWarnings("deprecation")
	public void restate(){
		
		main.gMsg("&3The first omega round awaits. You'd better pay attention.");
		main.gameData.setActive(false);
		
		for (Player p : Bukkit.getOnlinePlayers()){
			PlatformPlayer pp = main.getApi().getGamePlayer(p.getUniqueId(), PlatformPlayer.class).getType();
			pp.setLives(pp.getLives() + 1);
			main.msg(pp.getName(), "&aYou were given an extra life!");
		}
		
		long delay = 0L;
		
		for (Location l : main.gameData.getGridSeperators()){
			if (l.getBlock().getType().equals(Material.WOOL)){
				main.pah.changeLater(l, Material.AIR, 0, delay);
				l.getWorld().playEffect(l, Effect.STEP_SOUND, Material.WOOL.getId());
				delay+= 1L;
			}
		}
		
		main.pah.changeLater(Change.GRID, 1, Material.WOOL, 0, delay);
		
		for (int y = 2; y < 65; y++){
			main.pah.changeLater(Change.GRID, y, Material.AIR, 0, delay);
			delay+= 2L;
		}
		
		delay+= 20L;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){ end(); }}, delay);
	}
	
	@Override
	public void end(){
		Bukkit.getPluginManager().callEvent(new RoundEndEvent(null, Round.TEN));
	}
}