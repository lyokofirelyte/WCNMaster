package com.github.lyokofirelyte.Elysian;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Modules.DefaultPlayer;
import com.github.lyokofirelyte.Empyreal.Modules.GamePlayer;

public class ElyPlayer extends DefaultPlayer implements GamePlayer<ElyPlayer> {

	public ElyPlayer(UUID u, Empyreal i) {
		super(u, i);
	}

	private static final long serialVersionUID = 1L;

	@Getter
	private ElyPlayer type = this;
	
	@Getter
	private Map<String, Object> converted = new HashMap<String, Object>();
	
	@Override
	public void s(String msg) {}
}