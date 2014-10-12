package com.github.lyokofirelyte.Elysian.Games.TeamPVP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.TeamPVP.TeamPVPData.TeamPVPGame;
import com.github.lyokofirelyte.Elysian.Games.TeamPVP.TeamPVPData.TeamPVPPlayer;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoRegister;
import com.github.lyokofirelyte.Spectral.Identifiers.AutoSave;
import com.github.lyokofirelyte.Spectral.Identifiers.DivGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;

public class TeamPVP extends THashMap<String, TeamPVPGame> implements AutoSave, AutoRegister, DivGame {
	
	protected Elysian main;
	public TeamPVPData data;
	public TeamPVPCommand command;
	public TeamPVPActive active;
	
	public TeamPVP(Elysian i){
		main = i;
		data = new TeamPVPData(this);
		command = new TeamPVPCommand(this);
		active = new TeamPVPActive(this);
	}
	
	public TeamPVPGame getGame(final String name){
		
		if (!containsKey(name)){
			
			put(name, new TeamPVPGame(){
				
				private boolean ready = false;
				private boolean inProgress = false;
				private String gameName = name;
				private List<TeamPVPPlayer> players = new ArrayList<TeamPVPPlayer>();
				
				public List<TeamPVPPlayer> getPlayers(){
					return players;
				}
				
				public TeamPVPPlayer getPlayer(String name){
					
					for (TeamPVPPlayer player : players){
						if (player.name().equalsIgnoreCase(name)){
							return player;
						}
					}
					
					return null;
				}
				
				public boolean ready(){
					return ready;
				}
				
				public boolean hasPlayer(String name){
					return getPlayer(name) != null;
				}
				
				public String name(){
					return gameName;
				}

				public boolean isInProgress(){
					return inProgress;
				}
				
				public void setReady(boolean a){
					ready = a;
				}

				public void start(){
					
					players.get(0).setPartner(players.get(1));
					players.get(1).setPartner(players.get(0));
					players.get(2).setPartner(players.get(3));
					players.get(3).setPartner(players.get(2));
					inProgress = true;
					
					msg("Game start!");
					teleportPlayers(false);
					//TODO kits
				}
				
				public void finish(String[] winners){
					
					DivinityUtilsModule.bc("&6" + winners[0] + " &band &6" + winners[1] + " &bhave won a TeamPVP match!");
					
					for (TeamPVPPlayer p : players){
						if (main.api.isOnline(p.name())){
							main.api.getPlayer(p.name()).getInventory().clear();
						}
					}
					
					teleportPlayers(true);
					delete();
				}

				public void teleportPlayers(boolean lobby){
					
					if (!lobby){
						
						String[] loc = toDivGame().getString("Arenas." + gameName + ".team1spawn").split(" ");
						Location team1 = new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
					
						loc = toDivGame().getString("Arenas." + gameName + ".team2spawn").split(" ");
						Location team2 = new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
						
						List<TeamPVPPlayer> t1 = Arrays.asList(players.get(0), players.get(1));
						List<TeamPVPPlayer> t2 = Arrays.asList(players.get(2), players.get(3));
						
						for (TeamPVPPlayer p : t1){
							if (main.api.isOnline(p.name())){
								Location newLoc = team1.clone();
								newLoc.setX(newLoc.getX() + new Random().nextInt(5));
								newLoc.setZ(newLoc.getZ() + new Random().nextInt(5));
								main.api.event(new DivinityTeleportEvent(main.api.getPlayer(p.name()), newLoc));
							}
						}
						
						for (TeamPVPPlayer p : t2){
							if (main.api.isOnline(p.name())){
								Location newLoc = team2.clone();
								newLoc.setX(newLoc.getX() + new Random().nextInt(5));
								newLoc.setZ(newLoc.getX() + new Random().nextInt(5));
								main.api.event(new DivinityTeleportEvent(main.api.getPlayer(p.name()), newLoc));
							}
						}
						
					} else {
						
						String[] loc = toDivGame().getString("Arenas." + gameName + ".lobby").split(" ");
						Location lobbyLoc = new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
						
						for (TeamPVPPlayer p : players){
							if (main.api.isOnline(p.name())){
								main.api.event(new DivinityTeleportEvent(main.api.getPlayer(p.name()), lobbyLoc));
							}
						}
					}
				}

				public void addPoint(TeamPVPPlayer p){
					p.addPoint();
				}
				
				public void setInProgress(boolean a){
					inProgress = a;
				}
				
				public void addPlayer(TeamPVPPlayer p){
					players.add(p);
					if (players.size() >= 4){
						start();
					}
				}
				
				public void msg(String msg){
					for (TeamPVPPlayer p : players){
						if (main.api.isOnline(p.name())){
							p.toDp().s(msg);
						}
					}
				}
				
				public void delete(){
					remove(name);
				}
				
			});
		}
		
		return get(name);
	}
	
	public TeamPVPPlayer createPlayer(final String name){
		
		return new TeamPVPPlayer(){
			
			private String playerName = name;
			private TeamPVPPlayer partner;
			private TeamPVPGame game;
			private int points = 0;
			private boolean dead = false;
			
			public void setDead(boolean d){
				dead = d;
			}

			public void delete(){
				game.getPlayers().remove(name);
			}

			public void setPartner(TeamPVPPlayer p){
				partner = p;
			}
			
			public void addPoint(){
				points += 1;
			}
			
			public boolean isDead(){
				return dead;
			}
			
			public int getPoints(){
				return points;
			}

			public TeamPVPPlayer getPartner(){
				return partner;
			}
			
			public TeamPVPGame getGame(){
				return game;
			}

			public DivinityPlayer toDp(){
				return main.api.getDivPlayer(name);
			}

			public String name(){
				return playerName;
			}
			
		};
	}

	@Override
	public void save(){}

	@Override
	public void load(){}

	public DivinityGame toDivGame(){
		return main.api.getDivGame("teampvp", "teampvp");
	}
	
	public Object[] registerSubClasses(){
		return new Object[]{
			command,
			active
		};
	}
}