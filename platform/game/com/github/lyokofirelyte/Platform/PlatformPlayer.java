package com.github.lyokofirelyte.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class PlatformPlayer implements GamePlayer<PlatformPlayer> {
	
	@Getter @Setter
	private PlatformPlayer type = this;
	
	@Getter @Setter
	private UUID UUID;
	
	@Getter @Setter
	private String name;
	
	@Getter @Setter
	private List<String> perms = new ArrayList<String>();
	
	@Getter @Setter
	public int lives = 4;
	
	@Getter @Setter
	public int score;
	
	@Getter @Setter
	public int combo = 1;

	
	public Player getPlayer(){
		return Bukkit.getPlayer(UUID);
	}
	
	public PlatformPlayer(Player p){
		setUUID(p.getUniqueId());
		setName(p.getName());
	}

	@Override
	public void s(String msg){
		Utils.s(Bukkit.getPlayer(getUUID()), "&aPlatform &2// &a" + msg);
	}

}