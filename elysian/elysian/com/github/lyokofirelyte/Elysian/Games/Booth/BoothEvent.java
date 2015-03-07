package com.github.lyokofirelyte.Elysian.Games.Booth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

import com.github.lyokofirelyte.Elysian.Elysian;

public class BoothEvent implements Listener {

	Booth root;
	Elysian main;
	
	public BoothEvent(Booth root){
		this.root = root;
		main = root.main;
	}
	
	@EventHandler
	public void onBlockForm(BlockFormEvent e){
		//if(!root.isInBooth(e.getBlock().getLocation())){
		//	e.setCancelled(true);
		//}
		
	}
}
