package com.github.lyokofirelyte.Divinity.Storage;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;

public class DivinityGameModule extends DivinityStorageModule implements DivinityGame {

	public DivinityGameModule(String gameType, String n, API i) {
		super(gameType, n, i);
	}
	
	public String getFullPath(){
		return DivinityManager.gamesDir + gameName() + "/";
	}
	
	public String getAppenededPath(){
		return gameName() + "/";
	}
}