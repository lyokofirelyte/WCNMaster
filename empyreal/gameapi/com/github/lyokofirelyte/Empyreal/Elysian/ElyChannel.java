package com.github.lyokofirelyte.Empyreal.Elysian;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.DPI;

public enum ElyChannel {

	MEMBERS_ONLY("MEMBERS_ONLY", "wa.member", "&3Elysian &7\u2744", "&b"),
	STAFF("STAFF", "wa.staff.intern", "&4\u273B", "&c&o"),
	ALLIANCE("ALLIANCE", "wa.alliance.<?>", "&aAlliance &2\u2744", "&a", DPI.ALLIANCE_TOGGLE),
	SURVIVAL_WORLD("world", "wa.member", "&2World &a\u2744", "&2"),
	NETHER("world_nether", "wa.member", "&4Nether &c\u2744", "&4"),
	END("world_the_end", "wa.member", "&eEnd &6\u2744", "&e"),
	CUSTOM("CUSTOM", "wa.staff.admin", "", "");
	
	ElyChannel(String name, String permission, String header, String color){
		info[0] = name;
		info[1] = permission;
		info[2] = header;
		info[3] = color;
	}
	
	ElyChannel(String name, String permission, String header, String color, DPI toggle){
		info[0] = name;
		info[1] = permission;
		info[2] = header;
		info[3] = color;
		this.toggle = toggle;
	}

	DPI toggle = DPI.ELY;
	String[] info = new String[4];
	
	public void send(String sender, String message, Empyreal api){
		for (Player player : Bukkit.getOnlinePlayers()){
			DivinityPlayer to = api.getDivPlayer(player);
			if (to.getList(DPI.PERMS).contains(info[1]) && (!hasToggle() || (hasToggle() && to.getBool(toggle)))){
				if (!info[0].startsWith("world") || player.getWorld().getName().equals(info[0])){
					player.sendMessage(api.AS(info[2] + " " + sender + "&f: " + info[3] + message));
				}
			}
		}
	}
	
	public void send(String sender, String message, String header, String color, String perm, Empyreal api){
		info[1] = perm;
		info[2] = header;
		info[3] = color;
		send(sender, message, api);
	}
	
	public void send(String sender, String message, String perm, Empyreal api){
		info[1] = perm;
		send(sender, message, api);
	}
	
	public String getName(){
		return info[0];
	}
	
	public String getPerm(){
		return info[1];
	}
	
	public String getHeader(){
		return info[2];
	}
	
	public String getColor(){
		return info[3];
	}
	
	public DPI getToggle(){
		return toggle;
	}
	
	public boolean hasToggle(){
		return !toggle.equals(DPI.ELY);
	}
}