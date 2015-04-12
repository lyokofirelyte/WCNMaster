package com.github.lyokofirelyte.Creator;

import com.github.lyokofirelyte.Creator.Identifiers.VTMap;

public class VTSettings extends VTMap<Object, Object> {

	private VariableTriggers main;
	
	public VTSettings(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/system", "settings.yml");
		load();
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}