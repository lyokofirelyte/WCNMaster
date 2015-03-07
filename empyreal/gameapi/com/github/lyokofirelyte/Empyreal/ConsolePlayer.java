package com.github.lyokofirelyte.Empyreal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class ConsolePlayer implements GamePlayer<ConsolePlayer> {

	
	@Getter @Setter
	private ConsolePlayer type;
	
	@Getter @Setter
	private UUID UUID = new UUID(0, 100);
	
	@Getter @Setter
	private String name = "System";
	
	@Getter @Setter
	private List<String> perms = new ArrayList<String>();
	
	@Getter
	private Player player;
	
	@Override
	public void s(String msg){
		Bukkit.getServer().getConsoleSender().sendMessage(Utils.AS(msg));
	}
}