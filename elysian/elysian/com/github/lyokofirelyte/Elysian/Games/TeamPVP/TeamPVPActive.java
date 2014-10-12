package com.github.lyokofirelyte.Elysian.Games.TeamPVP;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.TeamPVP.TeamPVPData.TeamPVPGame;
import com.github.lyokofirelyte.Elysian.Games.TeamPVP.TeamPVPData.TeamPVPPlayer;

public class TeamPVPActive implements Listener {

	private Elysian main;
	private TeamPVP root;
	
	public TeamPVPActive(TeamPVP i){
		root = i;
		main = root.main;
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e){
		
		for (TeamPVPGame game : root.values()){
			if (game.isInProgress() && game.hasPlayer(e.getEntity().getName())){
				TeamPVPPlayer you = game.getPlayer(e.getEntity().getName());
				you.setDead(true);
				if (you.getPartner().isDead()){
					for (TeamPVPPlayer player : game.getPlayers()){
						if (!player.equals(you.getPartner())){
							player.addPoint();
							int points = player.getPoints() + player.getPartner().getPoints();
							int yourPoints = you.getPoints() + you.getPartner().getPoints();
							if (points > yourPoints+3){
								game.finish(new String[]{player.name(), player.getPartner().name()});
							}
							return;
						}
					}
					game.setReady(true);
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onRespawn(PlayerRespawnEvent e){
		
		for (TeamPVPGame game : root.values()){
			if (game.isInProgress() && game.hasPlayer(e.getPlayer().getName())){
				String[] loc = root.toDivGame().getString("Arenas." + game.name() + ".lobby").split(" ");
				Location lobbyLoc = new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
				if (!game.ready()){
					main.api.event(new DivinityTeleportEvent(e.getPlayer(), lobbyLoc));
				} else {
					game.teleportPlayers(false);
				}
				break;
			}
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onHit(EntityDamageByEntityEvent e){

		if (!e.isCancelled() && e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			Player attacked = (Player) e.getEntity();
			Player attacker = (Player) e.getDamager();
			for (TeamPVPGame game : root.values()){
				if (game.isInProgress() && game.hasPlayer(attacked.getName()) && game.hasPlayer(attacker.getName())){
					TeamPVPPlayer you = game.getPlayer(attacker.getName());
					TeamPVPPlayer them = game.getPlayer(attacked.getName());
					if (you.getPartner().name().equals(them.name())){
						e.setCancelled(true);
					}
					break;
				}
			}
		}
	}
}