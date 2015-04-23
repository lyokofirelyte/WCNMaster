package com.github.lyokofirelyte.ServerInstances;

import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BungeeListener implements Listener {

	@Getter @Setter
	private ServerInstances main;
	
	public BungeeListener(ServerInstances i){
		setMain(i);
	}
	
	@EventHandler
	public void onServerMessage(PluginMessageEvent e){
		
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
	    String subchannel = in.readUTF();
	    
	    switch (subchannel){
	    
	    	case "add_server":
	    		
	    		String serverName = in.readUTF();
				main.getProxy().getServers().put(serverName, new BungeeServerInfo(serverName, new InetSocketAddress(main.nextPort()), "none", true));
				e.setCancelled(true);

	    	break;
	    	
			case "rem_server":
				
				serverName = in.readLine();
				main.getProxy().getServers().remove(serverName);
				e.setCancelled(true);
				
			break;
	    }
	}
}