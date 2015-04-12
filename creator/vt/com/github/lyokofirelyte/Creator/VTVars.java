package com.github.lyokofirelyte.Creator;

import com.github.lyokofirelyte.Creator.Identifiers.VTMap;

public class VTVars extends VTMap<Object, Object> {

	private VariableTriggers main;
	
	public VTVars(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/system", "vars.yml");
		load();
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}