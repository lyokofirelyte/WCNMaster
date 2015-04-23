package com.github.lyokofirelyte.ServerInstances;

import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;

public class ServerInstances extends Plugin {
	
	private int currPort = 10000;

	@Override @SneakyThrows
	public void onEnable(){
		getProxy().getPluginManager().registerListener(this, new BungeeListener(this));
	}
	
	@Override
	public void onDisable(){}
	
	public int nextPort(){
		currPort += 1;
		return currPort;
	}
}