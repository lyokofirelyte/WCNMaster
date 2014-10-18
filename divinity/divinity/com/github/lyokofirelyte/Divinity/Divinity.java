package com.github.lyokofirelyte.Divinity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;

public class Divinity extends JavaPlugin {
	
	public API api;
    public Map<List<String>, Object> commandMap = new HashMap<>();
    
	@Override
	public void onEnable(){
		api = new API(this);
	}
	
	@Override
	public void onDisable(){
		
		try {
			api.sheets.fetch(true, false);
			api.getDivSystem().getMarkkit().save(new File(DivinityManager.sysDir + "markkit.yml"));
			api.divManager.save();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bukkit.getScheduler().cancelTasks(this);
	}
}