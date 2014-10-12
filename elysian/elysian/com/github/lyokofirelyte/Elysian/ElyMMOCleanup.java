package com.github.lyokofirelyte.Elysian;

import java.util.ArrayList;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Spectral.DataTypes.ElyTask;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;

public class ElyMMOCleanup implements Runnable, AutoRegister {
	
	private Elysian main;
	
	public ElyMMOCleanup(Elysian i){
		main = i;
	}

	@Override
	public void run(){
		DivinitySystem dp = main.api.getDivSystem();
		dp.set(MMO.INVALID_BLOCKS, new ArrayList<String>());
	}
	
	public void stop(){
		main.cancelTask(ElyTask.MMO_BLOCKS);
	}
}