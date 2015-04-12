package com.github.lyokofirelyte.Creator.Events.Listeners.Player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.lyokofirelyte.Creator.VTParser;
import com.github.lyokofirelyte.Creator.VariableTriggers;
import com.github.lyokofirelyte.Creator.Events.CommandNotFoundEvent;
import com.github.lyokofirelyte.Creator.Events.Listeners.System.CommandNotFound;
import com.github.lyokofirelyte.Creator.Identifiers.AR;
import com.github.lyokofirelyte.Creator.Identifiers.VTMap;
import com.github.lyokofirelyte.Creator.Utils.VTUtils;

public class PlayerCommand extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerCommand(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerCommand.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onCmd(PlayerCommandPreprocessEvent e){
		
		String path = e.getMessage().replaceFirst("\\/", ""); 
		path = path.split(" ")[0];
		
		if (!containsKey(path + ".Script")){

			if (Bukkit.getHelpMap().getHelpTopic(path) == null && Bukkit.getHelpMap().getHelpTopic("/" + path) == null){
				if (((CommandNotFound) main.getInstance(CommandNotFound.class)).getList("Worlds").contains(e.getPlayer().getLocation().getWorld().getName())){
					main.event(new CommandNotFoundEvent(e.getPlayer(), path, e.getPlayer().getLocation()));
					e.setCancelled(true);
				}
			}
			
		} else {

			//if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
				if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
					
					if (getBool("Cancelled")){
						e.setCancelled(true);
					}
					
					if (getLong(path + ".ActiveCooldown") <= System.currentTimeMillis()){
						
						if (getStr(path + ".Permission").equals("none") || e.getPlayer().hasPermission(getStr(path + ".Permission"))){
							
							if (getBool(path + ".Cancelled")){
								e.setCancelled(true);
							}
							
							new VTParser(main, "PlayerCommand.yml", path, getList(path + ".Script"), e.getPlayer().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
							set(path + ".ActiveCooldown", (System.currentTimeMillis() + getLong(path + ".Cooldown")*1000L));
							cooldown();
							
						} else if (!getStr("NoPermissionMessage").equals("none")){
							VTUtils.s(e.getPlayer(), "&c&o" + getStr("NoPermissionMessage"));
						}
					}
				}
			//}
		}
	}

	private HashMap<String, String> getCustoms(PlayerCommandPreprocessEvent e){
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("<cmdline>", e.getMessage().replace(e.getMessage().split(" ")[0], ""));
		map.put("<cmdargcount>", e.getMessage().split(" ").length-1 + "");
		map.put("<cmdname>", e.getMessage().split(" ")[0].replace("\\/", ""));
		
		for (int i = 0; i < e.getMessage().split(" ").length-1; i++){
			map.put("<cmdarg:" + (i+1) + ">", e.getMessage().split(" ")[i+1]);
			map.put("<cmdarg" + (i+1) + ">", e.getMessage().split(" ")[i+1]);
		}
		
		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}