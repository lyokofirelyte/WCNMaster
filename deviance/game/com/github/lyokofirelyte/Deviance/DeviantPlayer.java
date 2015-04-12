package com.github.lyokofirelyte.Deviance;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class DeviantPlayer implements GamePlayer<DeviantPlayer> {
	
	@Getter
	private DeviantPlayer type = this;
	
	@Getter @Setter
	private UUID UUID;
	
	@Getter @Setter
	private String name;
	
	@Getter @Setter
	private List<String> perms = new ArrayList<String>();

	public DeviantPlayer(Player p){
		setUUID(p.getUniqueId());
		setName(p.getName());
	}
	
	@Override
	public Player getPlayer(){
		return Bukkit.getPlayer(UUID);
	}

	@Override
	public void s(String msg){
		Utils.s(Bukkit.getPlayer(UUID), msg);
	}
}