package com.github.lyokofirelyte.Divinity.Storage;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityAlliance;

public class DivinityAllianceModule extends DivinityStorageModule implements DivinityAlliance {
	
	public DivinityAllianceModule(String n, API i) {
		super(n, i);
	}

	public boolean exists(){
		return api.divManager.getMap(DivinityManager.allianceDir).containsKey(name());
	}
}