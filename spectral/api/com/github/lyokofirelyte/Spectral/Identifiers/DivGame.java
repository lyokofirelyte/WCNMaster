package com.github.lyokofirelyte.Spectral.Identifiers;

import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;

/**
 * An interface representing a DivinityGame that may have sub-classes to register.
 */
public interface DivGame {
	
	public Object[] registerSubClasses();
	public DivinityGame toDivGame();
}