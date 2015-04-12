package com.github.lyokofirelyte.Elysian.Commands;

import lombok.Getter;

import org.bukkit.event.Listener;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class Elyvator implements AutoRegister<Elyvator>, Listener {

	private Elysian main;
	
	@Getter
	private Elyvator type = this;
	
	public Elyvator(Elysian i){
		main = i;
	}
	
	
}