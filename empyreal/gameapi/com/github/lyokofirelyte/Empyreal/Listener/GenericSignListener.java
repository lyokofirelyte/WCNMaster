package com.github.lyokofirelyte.Empyreal.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import lombok.Getter;
import lombok.Setter;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;
import com.github.lyokofirelyte.Empyreal.Utils.Utils;

public class GenericSignListener implements AutoRegister<GenericSignListener>, Listener {

	@Getter @Setter
	private GenericSignListener type = this;
	
	@Getter
	private Empyreal main;
	
	public GenericSignListener(Empyreal i){
		main = i;
	}
	
	@EventHandler
	public void onSign(SignChangeEvent e){
		for (int i = 0; i < 4; i++){
			if (e.getLine(i) != null && !e.getLine(i).equals("")){
				e.setLine(i, Utils.AS(e.getLine(i)));
			}
		}
	}
}