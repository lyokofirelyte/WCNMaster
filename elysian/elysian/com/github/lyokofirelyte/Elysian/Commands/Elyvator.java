package com.github.lyokofirelyte.Elysian.Commands;

import org.bukkit.event.Listener;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;

public class Elyvator implements AutoRegister, Listener {

	private Elysian main;
	
	public Elyvator(Elysian i){
		main = i;
	}
	
	
}