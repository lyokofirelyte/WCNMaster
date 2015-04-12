package com.github.lyokofirelyte.CreativeServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class CreativePlayer implements GamePlayer<CreativePlayer> {
	
	@Getter @Setter
	private CreativePlayer type = this;
	
	@Getter @Setter
	private UUID UUID;
	
	@Getter @Setter
	private String name;
	
	@Getter @Setter
	private List<String> perms = new ArrayList<String>();
	
	public Player getPlayer(){
		return Bukkit.getPlayer(UUID);
	}
	
	public CreativePlayer(Player p){
		setUUID(p.getUniqueId());
		setName(p.getName());
	}

	@Override
	public void s(String msg){
		Utils.s(Bukkit.getPlayer(getUUID()), "&aCreative &2// &a" + msg);
	}
}