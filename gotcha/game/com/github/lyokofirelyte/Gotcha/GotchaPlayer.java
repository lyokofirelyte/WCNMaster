package com.github.lyokofirelyte.Gotcha;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class GotchaPlayer implements GamePlayer<GotchaPlayer> {
	
	@Getter @Setter
	private GotchaPlayer type = this;
	
	@Getter @Setter
	private UUID UUID;
	
	@Getter @Setter
	private String name;
	
	@Getter @Setter
	private List<String> perms = new ArrayList<String>();
	
	@Getter @Setter
	private int score = 0;
	
	@Getter @Setter
	private long cooldown = -1;
	
	public Player getPlayer(){
		return Bukkit.getPlayer(UUID);
	}
	
	public GotchaPlayer(Player p){
		setUUID(p.getUniqueId());
		setName(p.getName());
	}

	@Override
	public void s(String msg){
		Utils.s(Bukkit.getPlayer(getUUID()), "&aGotcha &2// &a" + msg);
	}

}