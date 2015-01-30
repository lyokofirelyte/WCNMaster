package com.github.lyokofirelyte.Divinity;

import gnu.trove.map.hash.THashMap;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class Divinity extends JavaPlugin {
	
	public API api;
    public Map<List<String>, Object> commandMap = new THashMap<>();
    
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