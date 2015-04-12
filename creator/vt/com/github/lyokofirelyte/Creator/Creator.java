package com.github.lyokofirelyte.Creator;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Modules.GameModule;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class Creator extends JavaPlugin implements GameModule {
	
	VariableTriggers vt;
	Empyreal api;
	
	@Getter
	private String packageName = "Creator";
	
	@Getter
	private String jarName = "Creator-1.0";

	@Override
	public void onEnable(){
		
		api = (Empyreal) Bukkit.getPluginManager().getPlugin("Empyreal");
		api.registerModule(this);
		
		vt = new VariableTriggers();
		vt.enable();
	}
	
	@Override
	public void onDisable(){
		vt.disable();
	}
	
	@Override
	public void onRegister(){
		
	}
	
	@Override
	public void closing(){
		
	}
	
	@Override
	public void onPlayerChat(GamePlayer<?> gp, String msg){
		
	}
	
	@Override
	public void onPlayerJoin(Player p){
		
	}
	
	@Override
	public void onPlayerQuit(Player p){
		
	}
	
	@Override
	public void shutdown(){
		Bukkit.getServer().shutdown();
	}
}