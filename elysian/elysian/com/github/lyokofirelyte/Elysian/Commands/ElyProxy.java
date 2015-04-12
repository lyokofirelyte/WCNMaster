package com.github.lyokofirelyte.Elysian.Commands;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.PlayerLocation;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class ElyProxy implements AutoRegister<ElyProxy>, PluginMessageListener {

	private Elysian main;
	
	@Getter
	private ElyProxy type = this;
	
	public ElyProxy(Elysian i){
		main = i;
	}
	
	@DivCommand(aliases = "sv", desc = "Elysian Server Command", help = "/sv <server>, /sv <server> <player>, /sv list", min = 1, perm = "wa.staff.mod2")
	public void onSV(Player p, String[] args){
		
		if (args[0].equals("list")){
			main.api.requestServerList(p.getName());
		} else if (args[0].equals("all") && args.length == 2){
			main.api.sendAllToServer(args[1]);
		} else {
			String send = args.length == 1 ? p.getName() : args[1];
			main.api.sendToServer(send, args[0]);
		}
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
					main.s(sendTo, "Country: &3" + main.api.getInstance(PlayerLocation.class).getType().getCountry(ip));
					main.s(sendTo, "City: &3" + main.api.getInstance(PlayerLocation.class).getType().getCity(ip));
					main.s(sendTo, "Postal Code: &3" + main.api.getInstance(PlayerLocation.class).getType().getPostal(ip));
					
		    	break;
		    	
		    	case "getservers":
		    		main.s(player, in.readUTF());
		    	break;
		    	
		    	case "getserver":
		    		main.api.getDivSystem().set(DPI.SERVER_NAME, in.readUTF());
		    		System.out.println("Get server returned!");
		    	break;
		    	
		    	case "lastserver":
		    		main.api.getDivSystem().set(DPI.LAST_SERVER, in.readUTF());
		    		System.out.println("Last server updated!");
		    	break;
		    	
		    	case "PlayerList":
		    		
		    		String server = in.readUTF();
		    		String[] playerList = in.readUTF().split(", ");
		    		
		    	break;
		    	
		    	case "x":
		    		
		    		String msg = in.readUTF();
		    		
		    		for (Player staff : Bukkit.getOnlinePlayers()){
		    			if (main.api.perms(staff, "wa.staff.intern", true) || main.api.perms(staff, "ely.staff", true)){
		    				staff.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4) -X- ( &c" +  msg));
		    			}
		    		}
		    		
		    	break;
		    }
		}
	}
}