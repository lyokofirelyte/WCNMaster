package com.github.lyokofirelyte.Elysian;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyChannel;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;

public class ElyAutoSave implements Runnable, AutoRegister {
	
	private Elysian main;
	
	public ElyAutoSave(Elysian i){
		main = i;
	}
	
	@Override
	public void run(){
		
		long startTime = new Long(System.currentTimeMillis());
		
		main.api.getDivSystem().saveMarkkit();
		
		for (AutoSave saveClass : main.divinity.api.saveClasses.values()){
			saveClass.save();
		}
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (main.api.getDivPlayer(p).getBool(MMO.IS_MINING) || main.api.getDivPlayer(p).getBool(MMO.IS_DIGGING)){
				return;
			}
		}

		main.api.saveAllFiles();
		ElyChannel.STAFF.send("&6System", "&7Auto-save complete (" + (System.currentTimeMillis()-startTime) + "ms)", main.api);
	}
}