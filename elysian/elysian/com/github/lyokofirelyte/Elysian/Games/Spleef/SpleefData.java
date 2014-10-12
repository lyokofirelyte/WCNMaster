package com.github.lyokofirelyte.Elysian.Games.Spleef;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
public @interface SpleefData {
	
	public String[] appliesTo() default {""};
	
	public enum SpleefDataType {
		
		PLAYER("PLAYER"),
		SYSTEM("SYSTEM"),
		GAME("GAME");
		
		SpleefDataType(String type){
			this.type = type;
		}
		
		String type;
		
		public String s(){
			return type;
		}
	}
	
	public enum SpleefGameData {

		PLAYERS("PLAYERS"),
		MAX("MAX"),
		MIN("MIN"),
		PLAYER_START_1("PLAYER_START_1"),
		PLAYER_START_2("PLAYER_START_2"),
		MATERIAL("MATERIAL");
		
		SpleefGameData(String type){
			this.type = type;
		}
		
		String type;
		
		@SpleefData(appliesTo = {"GAME", DivinityManager.gamesDir + "spleef"})
		public String s(){
			return type;
		}
	}
	
	public enum SpleefPlayerData {

		TOTAL_SCORE("TOTAL_SCORE"),
		TOTAL_WINS("TOTAL_WINS"),
		TOTAL_LOSSES("TOTAL_LOSSES");
		
		SpleefPlayerData(String type){
			this.type = type;
		}
		
		String type;
		
		@SpleefData(appliesTo = {"PLAYER", DivinityManager.dir})
		public String s(){
			return type;
		}
	}
	
	public interface SpleefGame extends SpleefInfo {
		public List<SpleefPlayer> involvedPlayers();
		public boolean isEnabled();
		public void setEnabled(boolean enable);
		public void bc(String message);
		public void teleportPlayers();
	}
	
	public interface SpleefPlayer extends SpleefInfo {
		public boolean inGame();
		public int getPoints();
		public SpleefPlayer opponent();
		public SpleefGame currentGame();
		public SpleefPlayer getInvite();
		public DivinityPlayer toDp();
		public void setInvite(SpleefPlayer player);
		public void setOpponent(SpleefPlayer player);
		public void setCurrentGame(SpleefGame game);
		public void setInGame(boolean inGame);
		public void addPoint();
		public void setPoints(int points);
	}
	
	public interface SpleefInfo {
		public void putt(Enum<?> enumm, Object o);
		public Object gett(Enum<?> enumm);
	}
	
	public interface SpleefSystem {
		public Location getLobby();
		public Map<SpleefGame, Integer> getCounts();
		public void setLobby(Location l);
		public void setCount(SpleefGame g, int i);
	}
}