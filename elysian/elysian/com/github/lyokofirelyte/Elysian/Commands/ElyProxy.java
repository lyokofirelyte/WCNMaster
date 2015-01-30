package com.github.lyokofirelyte.Elysian.Commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class ElyProxy implements AutoRegister, PluginMessageListener {

	private Elysian main;
	
	public ElyProxy(Elysian i){
		main = i;
	}
	
	@DivCommand(aliases = "sv", desc = "Elysian Server Command", help = "/sv <server>, /sv <server> <player>, /sv list", min = 1, perm = "wa.staff.mod2")
	public void onSV(Player p, String[] args){
		
		if (args[0].equals("list")){
			main.api.requestServerList(p.getName());
		} else if (args[0].equals("all")){
			
		} else {
			String send = args.length == 1 ? p.getName() : args[1];
			main.api.sendToServer(send, args[0]);
		}
		
		main.api.sendPluginMessageAll("test", "lol");
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message){
		
		if (channel.equals("BungeeCord")){
			
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
		    String subchannel = in.readUTF();
		    
		    switch (subchannel.toLowerCase()){
		    
		    	case "ip":
		    		
		    		String ip = in.readUTF();
		    		int port = in.readInt();
		    		
		    		DivinityPlayer dp = main.api.getDivPlayer(player);
		    		Player sendTo = Bukkit.getPlayer(dp.getStr(DPI.IP_LOOKUP));

		    		main.s(sendTo, "&6Location Overview: " + player.getDisplayName());
		    		main.s(sendTo, "IP: &3" + ip);
					main.s(sendTo, "Port: &3" + port);
					main.s(sendTo, "Country: &3" + main.divinity.api.playerLocation.getCountry(ip));
					main.s(sendTo, "City: &3" + main.divinity.api.playerLocation.getCity(ip));
					main.s(sendTo, "Postal Code: &3" + main.divinity.api.playerLocation.getPostal(ip));
					
		    	break;
		    	
		    	case "getservers":
		    		main.s(player, in.readUTF());
		    	break;
		    	
		  		case "test":
		  			System.out.println("LUL");
		  		break;
		    }
		}
	}
}