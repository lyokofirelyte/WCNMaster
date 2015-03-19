package com.github.lyokofirelyte.GameServer;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class GenericListener implements AutoRegister<GenericListener>, Listener {
	
	@Getter
	private GenericListener type = this;
	
	public GenericListener(GameServer i){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle false");
		Bukkit.setDefaultGameMode(GameMode.SURVIVAL);
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e){
		if (!e.getPlayer().isOp() && !e.getMessage().startsWith("/o")){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDecay(BlockFadeEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onGravity(EntityChangeBlockEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFlow(BlockFromToEvent e){
		e.setCancelled(true);
	}

	@EventHandler
	public void onFlow(BlockPhysicsEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e){
		e.setCancelled(true);
	}

	@EventHandler
	public void onWeather(WeatherChangeEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBuild(BlockPlaceEvent e){
		if (!e.getPlayer().isOp()){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFire(BlockBurnEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFire(BlockIgniteEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		if (!e.getPlayer().isOp()){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPVP(EntityDamageByEntityEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(ItemSpawnEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPick(PlayerPickupItemEvent e){
		e.setCancelled(true);
	}
}