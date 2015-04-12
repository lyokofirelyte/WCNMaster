package com.github.lyokofirelyte.GameServer.Listener;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class SQLQueue implements Runnable, AutoRegister<SQLQueue> {

	@Getter
	private SQLQueue type = this;
	
	@Getter
	private List<String> queue;
	
	@Setter
	private Empyreal main;
	
	public SQLQueue(Empyreal main){
		setMain(main);
	}
	
	@Override
	public void run(){
		if (queue.size() > 0){
			for (String thing : queue){
				main.getInstance(EmpyrealSQL.class).getType().write(thing);
			}
		}
	}
}