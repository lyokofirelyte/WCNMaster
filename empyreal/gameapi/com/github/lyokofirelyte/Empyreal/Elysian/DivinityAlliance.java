package com.github.lyokofirelyte.Empyreal.Elysian;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Listener.SocketMessageListener.Handler;
import com.github.lyokofirelyte.Empyreal.Listener.SocketObject;

public class DivinityAlliance extends DivinityStorageModule {
	
	public DivinityAlliance(String n, Empyreal i) {
		super(n, i, "alliances");
	}

	public boolean exists(){
		return api.getOnlineModules().containsKey("ALLIANCE_" + getName());
	}
	
	public void save(){
		if (!api.getServerName().equals("GameServer")){
			SocketObject obj = new SocketObject(this, DivinityStorageModule.class, Handler.SAVE_OBJECT_TO_SQL, api.getServerName());
			api.sendObjectToSocket("GameServer", obj);
		} else {
			api.getInstance(EmpyrealSQL.class).getType().saveMapToDatabase("alliances", this);
		}
	}
	
	public void transfer(){
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("../wa/Divinity/alliances/" + getName() + ".yml"));
		
		for (String key : yaml.getKeys(false)){
			set(key, yaml.get(key));
		}
		
		save();
	}
}