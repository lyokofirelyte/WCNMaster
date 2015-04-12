package com.github.lyokofirelyte.Creator.Events.Listeners.System;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import com.github.lyokofirelyte.Creator.VTParser;
import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Events.ConsoleCommandEvent;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.github.lyokofirelyte.Creator.Identifiers.VTMap;
import com.github.lyokofirelyte.Creator.Utils.VTUtils;

public class ConsoleCommand extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public ConsoleCommand(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/system", "ConsoleCommand.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onCmd(ConsoleCommandEvent e){
		
		String path = e.getType().split(" ")[0];
		
		if (!containsKey(path + ".Script")){
			VTUtils.s(e.getSender(), "You haven't defined that console command in your script file!");
		} else {
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getLong(path + ".ActiveCooldown") <= System.currentTimeMillis()){
					new VTParser(main, "ConsoleCommand.yml", path, getList(path + ".Script"), new Location(Bukkit.getWorlds().get(0), 0, 0, 0), getCustoms(e), "Console").start();
					set(path + ".ActiveCooldown", (System.currentTimeMillis() + getLong(path + ".Cooldown")*1000L));
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(ConsoleCommandEvent e){
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("<cmdline>", e.getType().replace(e.getType().split(" ")[0], ""));
		map.put("<cmdargcount>", e.getType().split(" ").length + "");
		map.put("<cmdname>", e.getType().split(" ")[0]);
		
		for (int i = 0; i < e.getType().split(" ").length; i++){
			map.put("<cmdarg:" + (i+1) + ">", e.getType().split(" ")[i]);
			map.put("<cmdarg" + (i+1) + ">", e.getType().split(" ")[i]);
		}
		
		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}