package com.github.lyokofirelyte.Platform.Rounds;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Empyreal.Utils.Utils;
import com.github.lyokofirelyte.Platform.Platform;
import com.github.lyokofirelyte.Platform.PlatformPlayer;

public class PR0 extends PlatformRound {

	public PR0(Platform i) {
		super(i);
	}

	@Override
	public void start(){
		
		if (main.gameData.isPaused() || main.gameData.isStopped()){
			return;
		}
		
		main.gameData.setCurrentRound(main.gameData.getCurrentRound() + 1);
		main.gameData.setActive(true);
	}
	
	@Override
	public void end(){
		
		if (main.gameData.isPaused() || main.gameData.isStopped()){
			main.gMsg("&cThe game has been haulted!");
			Bukkit.getScheduler().cancelTasks(main);
			return;
		}
		
		for (Player p : Bukkit.getOnlinePlayers()){
			
			PlatformPlayer pp = main.getApi().getGamePlayer(p.getUniqueId(), PlatformPlayer.class).getType();
			
			if (pp.getLives() > 0){
				pp.setScore(pp.getScore() + (10*pp.getCombo()));
				Utils.s(p, "&aYou were awarded &3" + 10*pp.getCombo() + " &apoints.");
			} else {
				Utils.s(p, "&7&oYou didn't get any points due to having no lives.");
			}
			
			if (pp.getCombo() < 5){
				pp.setCombo(pp.getCombo() + 1);
			}
		}
		
		main.updateScores();
		
		main.gameData.setActive(false);
		main.gMsg("&9Next round in 8 seconds!");
	}
}