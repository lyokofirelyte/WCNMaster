package com.github.lyokofirelyte.Elysian.Games.Booth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.sk89q.worldedit.regions.CuboidRegion;

public class BoothEvent implements Listener {
	Booth root;
	Elysian main;
	
	public BoothEvent(Booth root){
		this.root = root;
		main = root.main;
	}
	
	@EventHandler
	public void onInteract(BlockPlaceEvent e){
		System.out.println("placed");
	}
	
//	@EventHandler
//	public void onBlockForm(BlockEvent e){
//		System.out.println("testawd");
//		
//		if(e.getBlock().getWorld().getName().equals("BCBW")){
//			System.out.println("test");
//			if(!root.isInBooth(e.getBlock().getLocation(), root.getArena(e.getBlock().getLocation()))){
////				e.setCancelled(true);
//			}
//		}
//		//if(!root.isInBooth(e.getBlock().getLocation())){
//		//	e.setCancelled(true);
//		//}
//		
//	}
//	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e){
		if(e.getPlayer().getLocation().getWorld().getName().equals("BCBW")){
			if(e.getMessage().toLowerCase().startsWith("//")){
				setMask(e.getPlayer());
			}
		}
	}

	public void setMask(Player p){
		if(main.we.getSelection(p) != null && main.we.getSession(p) != null){
			main.we.getSession(p);
			
//			CuboidRegion cr = new CuboidRegion(root.dg.getString("Booths." + root.getArena(main.we.getSelection(p)), pos1, pos2)
		}
		
	}
}

