/*package com.github.lyokofirelyte.Elysian.Games.TeamPVP;

import java.util.List;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;

public class TeamPVPData {

	private Elysian main;
	private TeamPVP root;
	
	public TeamPVPData(TeamPVP i){
		root = i;
		main = root.main;
	}
	
	public interface TeamPVPGame {
		public List<TeamPVPPlayer> getPlayers();
		public TeamPVPPlayer getPlayer(String name);
		public boolean hasPlayer(String name);
		public boolean isInProgress();
		public boolean ready();
		public void start();
		public void addPlayer(TeamPVPPlayer p);
		public void teleportPlayers(boolean lobby);
		public void addPoint(TeamPVPPlayer p);
		public void setInProgress(boolean a);
		public void setReady(boolean a);
		public void delete();
		public void msg(String msg);
		public void finish(String[] winners);
		public String name();
	}
	
	public interface TeamPVPPlayer {
		public void delete();
		public void addPoint();
		public void setDead(boolean b);
		public void setPartner(TeamPVPPlayer p);
		public int getPoints();
		public boolean isDead();
		public TeamPVPPlayer getPartner();
		public TeamPVPGame getGame();
		public DivinityPlayer toDp();
		public String name();
	}
}*/