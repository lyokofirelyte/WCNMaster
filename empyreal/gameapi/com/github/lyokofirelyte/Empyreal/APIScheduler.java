package com.github.lyokofirelyte.Empyreal;

import org.bukkit.Bukkit;

public enum APIScheduler {

	DELAY("DELAY"),
	REPEAT("REPEAT");
	
	APIScheduler(String type){
		this.type = type;
	}
	
	Empyreal main;
	String type;
	
	public void start(Empyreal i, String name, long delay, Runnable runnable){
		main = i;
		main.getTasks().put(name, Bukkit.getScheduler().scheduleSyncDelayedTask(i, runnable, delay));
	}
	
	public void start(Empyreal i, String name, long delay, long interval, Runnable runnable){
		main = i;
		main.getTasks().put(name, Bukkit.getScheduler().scheduleSyncRepeatingTask(i, runnable, delay, interval));
	}
	
	public boolean stop(String name){
		
		if (main.getTasks().containsKey(name)){
			Bukkit.getScheduler().cancelTask(main.getTasks().get(name));
			main.getTasks().remove(name);
			return true;
		}
		
		return false;
	}
}