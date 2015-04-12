package com.github.lyokofirelyte.Elysian;

import java.util.ArrayList;

import lombok.Getter;

import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Elysian.api.ElyTask;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinitySystem;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElyMMOCleanup implements Runnable, AutoRegister<ElyMMOCleanup> {
	
	private Elysian main;
	
	@Getter
	private ElyMMOCleanup type = this;
	
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