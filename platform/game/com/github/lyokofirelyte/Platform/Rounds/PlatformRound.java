package com.github.lyokofirelyte.Platform.Rounds;

import com.github.lyokofirelyte.Platform.Platform;

public abstract class PlatformRound {

	public Platform main;
	
	public PlatformRound(Platform i){
		main = i;
	}

	public abstract void start();
	
	public abstract void end();
}