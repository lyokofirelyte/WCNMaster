package com.github.lyokofirelyte.Elysian.Events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Public.ParticleEffect;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class ElyTP implements Listener, AutoRegister {
	
	private Elysian main;
	
	public ElyTP(Elysian i){
		main = i;
	}
	
	@EventHandler
	public void onActualTP(PlayerTeleportEvent e){
		
		if (!main.api.getDivPlayer(e.getPlayer()).getStr(DPI.RING_LOC).equals("none")){
			e.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onTP(DivinityTeleportEvent e){
		
		if (e.isCancelled()){
			return;
		}
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		List<String> prevLocs = dp.getList(DPI.PREVIOUS_LOCATIONS);
		Vector tv = e.getTo().toVector();
		Player p = e.getPlayer();
		
		if (dp.getBool(DPI.IN_COMBAT) && !main.api.perms(p, "wa.rank.citizen", false)){
			main.s(p, "&c&oYou can't escape during combat until you reach Citizen!");
			p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
			return;
		}
		
		if (!main.api.perms(p, "wa.staff.mod2", true)){
			for (Player player : Bukkit.getOnlinePlayers()){
				if (main.api.getDivPlayer(player).getBool(DPI.TP_BLOCK)){
					Location vv = player.getLocation();
					if (tv.getBlockX() <= vv.getBlockX()+7 && tv.getBlockX() >= vv.getBlockX()-7){
						if (tv.getBlockZ() <= vv.getBlockZ()+7 && tv.getBlockZ() >= vv.getBlockZ()-7){
							if (tv.getBlockY() <= vv.getBlockY()+7 && tv.getBlockY() >= vv.getBlockY()-7){
								main.s(p, "&c&oTeleblock in place at that area. Teleport cancelled.");
								p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
								return;
							}
						}
					}
				}
			}
		}
		
		while (prevLocs.size() > 10){
			prevLocs.remove(0);
		}
		
		Location f = e.getFrom();
		Vector v = f.toVector();
		
		prevLocs.add(f.getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ() + " " + f.getYaw() + " " + f.getPitch());
		
		effects(p);
		tp(p, e.getTo());
	}
	
	private void effects(Player p){
		if (!main.api.getDivPlayer(p).getBool(DPI.VANISHED) && main.api.getDivPlayer(p).getBool(DPI.PARTICLES_TOGGLE)){
			main.api.getDivPlayer(p).lockEffect("tp" + p.getName(), ParticleEffect.HAPPY_VILLAGER, 1, 1, 1, 1, 200, 16, 1L);
		}
	}
	
	public void tp(Player p, Location to){
		p.teleport(to);
		Vector toVector = to.toVector();
		main.s(p, "&oArrived at &6" + toVector.getBlockX() + ", " + toVector.getBlockY() + ", " + toVector.getBlockZ() + "&b&o.");
		main.api.schedule(main.api.getDivPlayer(p), "clearEffects", 20L, "clear" + p.getName());
	}
}