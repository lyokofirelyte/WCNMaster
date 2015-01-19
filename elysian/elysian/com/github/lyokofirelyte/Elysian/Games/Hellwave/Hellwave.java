package com.github.lyokofirelyte.Elysian.Games.Hellwave;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;
import com.github.lyokofirelyte.Spectral.Identifiers.DivGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;

public class Hellwave implements AutoSave, AutoRegister, DivGame{
	public Elysian main;
	HellwaveCommands hcmd;
	
	public Hellwave(Elysian i){
		main = i;
		hcmd = new HellwaveCommands(this);
	}
	
	
	
	
	
	
	@Override
	public Object[] registerSubClasses() {
		return new Object[]{hcmd};
	}

	@Override
	public DivinityGame toDivGame() {
		return main.api.getDivGame("hellwave", "hellwave");
	}

	@Override
	public void save() {}

	@Override
	public void load() {}

}
