package com.github.lyokofirelyte.Elysian;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import com.github.lyokofirelyte.Empyreal.JSONMap;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Elysian.ElyChannel;
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
		new Thread(new Runnable(){
			public void run(){
				long currTime = new Long(System.currentTimeMillis());
				
				List<JSONMap<String, Object>> list = new ArrayList<JSONMap<String, Object>>();
				
				for (DivinityStorageModule dp : main.api.getOnlineModules().values()){
					if (dp.getTable().equals("users")){
						dp.set(DPI.DIS_ENTITY, "none");
						dp.set(DPI.IS_DIS, false);
					}
					list.add(dp);
				}
				
				main.api.getInstance(EmpyrealSQL.class).getType().saveMapsToDatabase(list);
				
				System.out.println("Save Complete @ " + ((System.currentTimeMillis() - currTime)/1000) + " seconds");
				
				ElyChannel.STAFF.send("&6System", "&7Auto-save complete (" + (System.currentTimeMillis()-currTime) + "ms)", main.api);
			}
		}).start();

	}
}