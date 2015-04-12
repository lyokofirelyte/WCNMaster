/*package com.github.lyokofirelyte.Elysian.Games.Spleef;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;

import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityGame;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorage;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class Spleef implements AutoRegister<Spleef> {

	public Elysian main;
	public SpleefModule module;
	public SpleefCommandMain commandMain;
	public SpleefActive active;
	
	@Getter
	private Spleef type = this;
	
	public Spleef(Elysian i){
		main = i;
		module = new SpleefModule(this);
		commandMain = new SpleefCommandMain(this);
		active = new SpleefActive(this);
	}
	
	public void update(){
		for (SpleefStorage s : module.data.values()){
			if (s.toGame() != null){
				if (!main.divinity.api.divManager.data.containsKey(DivinityManager.gamesDir + "spleef/")){
					main.divinity.api.divManager.data.put(DivinityManager.gamesDir + "spleef/", new THashMap<String, DivinityStorageModule>());
				}
				main.divinity.api.divManager.data.get(DivinityManager.gamesDir + "spleef/").put(s.name(), s.toDivStorage());
			}
		}
	}
	
	public DivinityGame toDivGame(String dataName){
		return main.api.getDivGame("spleef", dataName);
	}

	public DivinityGame toDivGame() {
		return null;
	}

	public void load() {
		
		update();
		
		for (DivinityStorage game : main.divinity.api.divManager.getMap(DivinityManager.gamesDir + "spleef/").values()){
			SpleefStorage s = new SpleefStorage(main, SpleefDataType.GAME, game.name());
			for (SpleefGameData data : SpleefGameData.values()){
				s.put(data, game.getStr(data));
			}
			module.data.put(game.name(), s);
		}
		
	}
	
	public Object[] registerSubClasses(){
		return new Object[]{
			module,
			commandMain,
			active
		};
	}
}*/