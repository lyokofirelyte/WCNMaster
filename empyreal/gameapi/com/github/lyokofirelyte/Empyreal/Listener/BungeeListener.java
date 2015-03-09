package com.github.lyokofirelyte.Empyreal.Listener;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Command.CommandEmpyreal;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BungeeListener implements AutoRegister<BungeeListener>, PluginMessageListener {
	
	@Getter @Setter
	public BungeeListener type = this;
	
	@Setter
	private Empyreal main;
	
	public BungeeListener(Empyreal i){
		setMain(i);
		i.updateServerName();
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		
		if (channel.equals("BungeeCord")){
			
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
		    String subchannel = in.readUTF();
		    
		    switch (subchannel.toLowerCase()){
		    	
		    	case "getservers":
		    		Utils.s(player, in.readUTF());
		    	break;
		    	
		    	case "playerlist":
		    		
		    		String server = in.readUTF();
		    		Utils.s(player, in.readUTF());
		    		
		    	break;
		    	
		    	case "shutdown":
		    		
		    		String who = in.readUTF();
		    		
		    		if (who.equals("ALL") || who.equals(main.getServerName())){
		    			Bukkit.getServer().shutdown();
		    		}
		    		
		    	break;
		    	
		    	case "broadcast_reboot":
		    		
		    		AutoRegister<CommandEmpyreal> cmd = main.getInstance(CommandEmpyreal.class);
		    		cmd.getType().setShutdownInProgress(true);
		    		
		    		String rebootMsg = in.readUTF();
		    		
		    		for (int i = 0; i < 3; i++){
		    			Utils.bc("&4" + rebootMsg);
		    		}
		    		
		    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title 'Reboot in 5 minutes!'");
		    		
		    	break;
		    }
		}
	}
}