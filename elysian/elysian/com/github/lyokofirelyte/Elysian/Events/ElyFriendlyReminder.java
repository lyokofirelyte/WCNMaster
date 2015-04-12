package com.github.lyokofirelyte.Elysian.Events;

import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;

import org.apache.commons.math3.util.Precision;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

/**
 * 
 * @author I_WUV_UR_TACO
 *
 */

public class ElyFriendlyReminder implements Listener, AutoRegister<ElyFriendlyReminder> {
	
	private Elysian main;
	
	@Getter
	private ElyFriendlyReminder type = this;
	
	private Map<DPI, List<String>> messages = new THashMap<DPI, List<String>>();
	
	public ElyFriendlyReminder(Elysian i) {
		main = i;
		messages.put(DPI.FR_FK_TOGGLE, Arrays.asList(
				"If you kill an animal please erm, 'Repopulate' them!", 
				"Please make sure to breed more if you kill animals!",
				"Food is good, breeding is better! Remember to repopulate!",
				"Don't forget to repopulate!"
		));
		messages.put(DPI.FR_CH_TOGGLE, Arrays.asList(
				"Creeper problems? Make sure to fill that crater!", 
				"Help the server stay gorgeous! Fill creeper holes!",
				"Explosive! Make sure to fill those holes!",
				"Did you know that creeper holes are refillable?"
		));
		messages.put(DPI.FR_CR_TOGGLE, Arrays.asList(
				"Have fun! But make sure to re-plant!",
				"Make sure to put those seeds in the ground!",
				"Those seeds go in the ground ya know!",
				"Sharing is caring! But so is re-planting!"
		));
		messages.put(DPI.FR_TR_TOGGLE, Arrays.asList(
				"Nobody likes deforestation! Replant!",
				"Everyone likes trees! Replace those saplings!",
				"Saplings can grow trees! Shocking right!?",
				"Tree: I'll be back! Make it happen by replanting!"
		));
	}
	
	private boolean Check(DivinityPlayer dp, DPI dpi ) {
		return !dp.getBool(dpi) && dp.getLong(DPI.valueOf(dpi.toString().replace("TOGGLE", "COOLDOWN"))) <= System.currentTimeMillis();
	}
	
	private void Msg( DivinityPlayer dp, DPI dpi ) {
		dp.s("&a" + ( messages.containsKey(dpi) ? messages.get(dpi).get(new Random().nextInt(messages.get(dpi).size())) : "Not Found!"));
		dp.set(dpi.toString().replace("TOGGLE", "COOLDOWN"), System.currentTimeMillis() + 5 * 6000L);
	}
	
	private void CnM ( DivinityPlayer dp, DPI dpi ) {
		if(Check( dp, dpi))
			Msg(dp, dpi);
	}
	
	public void Toggle( DivinityPlayer dp, DPI dpi ) {
		dp.set(dpi, !dp.getBool(dpi));
	}
	
	public String getStatus( DivinityPlayer dp, DPI dpi ) {
		String E = "&2Enabled", D = "&4Disabled";
		return !dp.getBool(dpi) ? E : D;
	}
	
	/*Event driven methods*/
	
	@EventHandler
	private void onJoin( PlayerJoinEvent e ) {
		for(DPI dpi : messages.keySet()) {
			main.api.getDivPlayer(e.getPlayer()).set(dpi.toString().replace("TOGGLE", "COOLDOWN"), System.currentTimeMillis());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onFriendlyKill( EntityDeathEvent e ) {
		if(e.getEntity() instanceof Animals) {
			if(e.getEntity().getKiller() instanceof Player) {
				CnM( main.api.getDivPlayer(e.getEntity().getKiller()), DPI.FR_FK_TOGGLE );
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onCreeperExplosion(EntityExplodeEvent e) {
		List<Entity> entities = e.getEntity().getNearbyEntities(8, 4, 8);
		for(Entity E: entities) {
			if(E.getType() == EntityType.PLAYER) {
				CnM(main.api.getDivPlayer((Player) E), DPI.FR_CH_TOGGLE);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak (BlockBreakEvent e) {
		
		if(e.getBlock().getType().equals(Material.CROPS)) {
			CnM(main.api.getDivPlayer(e.getPlayer()), DPI.FR_CR_TOGGLE);
		}
		
		else if(e.getBlock().getType().equals(Material.LOG)) {
			if(Check(main.api.getDivPlayer(e.getPlayer()), DPI.FR_TR_TOGGLE)) {
				Player p = e.getPlayer();
				Location l = e.getBlock().getLocation();
				String loc = l.toVector().getBlockX() + "," + l.toVector().getBlockZ();
				float x = Precision.round(l.toVector().getBlockX(), -3);
				float z = Precision.round(l.toVector().getBlockZ(), -3);
				int y = l.toVector().getBlockY();
				File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
				List<String> results = new ArrayList<String>();
				
				if (file.exists()){
					YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
					results = new ArrayList<String>(yaml.getStringList("History." + p.getWorld().getName() + "." + y));
				}

				if (results.size() == 0){
					Msg(main.api.getDivPlayer(e.getPlayer()), DPI.FR_TR_TOGGLE);
				}
			}
		}
	}
}
