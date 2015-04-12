package com.github.lyokofirelyte.Elysian;

import lombok.Getter;

import com.github.lyokofirelyte.Elysian.api.ElyChannel;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElyAutoSave implements Runnable, AutoRegister<ElyAutoSave> {
	
	private Elysian main;
	
	@Getter
	private ElyAutoSave type = this;
	
	public ElyAutoSave(Elysian i){
		main = i;
	}
	
	@Override
	public void run(){
		long startTime = new Long(System.currentTimeMillis());
		main.api.getDivSystem().saveMarkkit();
		
		for (DivinityStorageModule m : main.api.getOnlineModules().values()){
			m.save();
		}
		
		ElyChannel.STAFF.send("&6System", "&7Auto-save complete (" + (System.currentTimeMillis()-startTime) + "ms)", main.api);
	}
}