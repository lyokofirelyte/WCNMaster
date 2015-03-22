package com.github.lyokofirelyte.MM;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Utils;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class MMPlayer implements GamePlayer<MMPlayer>{

	@Getter @Setter
	private MMPlayer type = this;
	
	@Getter @Setter
	private UUID UUID;
	
	@Getter @Setter
	private String name;
	
	@Getter @Setter
	private List<String> perms = new ArrayList<String>();
	
	@Getter @Setter
	private int score = 0;
	
	@Getter @Setter
	private String kit = "";
	
	public Player getPlayer(){
		return Bukkit.getPlayer(UUID);
	}
	
	public MMPlayer(Player p){
		setName(p.getName());
		setUUID(p.getUniqueId());
	}

	@Override
	public void s(String msg) {
		Utils.s(Bukkit.getPlayer(getUUID()), "&aMM &2// &a" + msg);
	}

	public void err(String msg) {
		Utils.s(Bukkit.getPlayer(getUUID()), "&aMM &2// &c" + msg);
	}
	
	public void prepareInventory(){
		new MMClasses().setClass(Bukkit.getPlayer(getUUID()), getKit());
	}
	
	public void giveRoundFourExtra(){
		new MMClasses().addBonus(Bukkit.getPlayer(getUUID()), getKit());
	}
}
