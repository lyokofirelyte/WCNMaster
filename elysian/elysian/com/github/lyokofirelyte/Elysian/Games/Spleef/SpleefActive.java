package com.github.lyokofirelyte.Elysian.Games.Spleef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefGame;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefPlayer;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefPlayerData;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

import static com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefModule.*;

public class SpleefActive implements Listener {

	private Spleef main;
	
	public SpleefActive(Spleef i) {
		main = i;
	}

	@EventHandler
	public void onFall(PlayerMoveEvent e){
		
		if (doesPlayerExist(e.getPlayer().getName())){
		
			SpleefPlayer sp = getSpleefPlayer(e.getPlayer().getUniqueId());
			
			if (sp.inGame()){
				DivinityPlayer dp = sp.toDp();
				DivinityPlayer them = sp.opponent().toDp();
				SpleefGame game = sp.currentGame();
				if (e.getTo().getBlockY() < Integer.parseInt(((String) game.gett(SpleefGameData.MIN)).split(" ")[2])){
					sp.opponent().addPoint();
					main.commandMain.reset(game);
					if (sp.opponent().getPoints() >= 3 && sp.opponent().getPoints() > sp.getPoints()+1){
						dp.set(SpleefPlayerData.TOTAL_LOSSES, dp.getInt(SpleefPlayerData.TOTAL_LOSSES)+1);
						them.set(SpleefPlayerData.TOTAL_WINS, them.getInt(SpleefPlayerData.TOTAL_WINS)+1);
						them.set(SpleefPlayerData.TOTAL_SCORE, them.getInt(SpleefPlayerData.TOTAL_SCORE)+sp.opponent().getPoints());
						sp.opponent().setPoints(0);
						sp.setPoints(0);
						sp.setInGame(false);
						sp.opponent().setInGame(false);
						DivinityUtilsModule.bc(Bukkit.getPlayer(sp.opponent().toDp().uuid()).getDisplayName() + " &bhas defeated " + Bukkit.getPlayer(sp.toDp().uuid()).getDisplayName() + " &bin a 1v1 spleef round!");
						for (SpleefPlayer player : game.involvedPlayers()){
							Player p = Bukkit.getPlayer(player.toDp().uuid());
							main.main.api.event(new DivinityTeleportEvent(p, new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()+9, p.getLocation().getZ())));
							p.getInventory().clear();
							for (ItemStack i : player.toDp().getStack(DPI.BACKUP_INVENTORY)){
								p.getInventory().addItem(i);
							}
						}
						game.involvedPlayers().clear();
						main.module.data.remove(sp.opponent());
						main.module.data.remove(sp);
					} else {
						game.bc("Point scored by " + Bukkit.getPlayer(sp.opponent().toDp().uuid()).getDisplayName() + "&b!");
						game.teleportPlayers();
						main.commandMain.reset(game);
					}
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onBreak(BlockBreakEvent e){
		
		if (doesPlayerExist(e.getPlayer().getName())){
			
			SpleefPlayer sp = getSpleefPlayer(e.getPlayer().getUniqueId());
			 
			if (sp.inGame()){
				if (e.getBlock().getType().equals(Material.valueOf((String) sp.currentGame().gett(SpleefGameData.MATERIAL)))){
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
				}
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		
		if (doesPlayerExist(e.getPlayer().getName())){
		
			SpleefPlayer sp = getSpleefPlayer(e.getPlayer().getUniqueId());
			
			if (sp.inGame()){
				Location l = e.getPlayer().getLocation();
				onFall(new PlayerMoveEvent(e.getPlayer(), l, new Location(l.getWorld(), l.getX(), 0, l.getY())));
			}
		}
	}
}