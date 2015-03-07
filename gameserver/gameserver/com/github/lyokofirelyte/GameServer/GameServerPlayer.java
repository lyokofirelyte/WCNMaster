package com.github.lyokofirelyte.GameServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

import com.github.lyokofirelyte.Empyreal.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils;

public class GameServerPlayer implements GamePlayer<GameServerPlayer> {
	
	@Getter @Setter
	private GameServerPlayer type = this;
	
	@Getter @Setter
	private UUID UUID;
	
	@Getter @Setter
	private String name;
	
	@Getter @Setter
	private List<String> perms = new ArrayList<String>();
	
	public Player getPlayer(){
		return Bukkit.getPlayer(UUID);
	}
	
	public GameServerPlayer(Player p){
		setUUID(p.getUniqueId());
		setName(p.getName());
	}

	@Override
	public void s(String msg){
		Utils.s(Bukkit.getPlayer(getUUID()), "&eGameServer &6// &e" + msg);
	}
}