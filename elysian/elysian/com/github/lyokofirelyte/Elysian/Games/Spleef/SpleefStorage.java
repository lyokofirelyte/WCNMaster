/*package com.github.lyokofirelyte.Elysian.Games.Spleef;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefGame;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityGame;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorage;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityStorageModule;

public class SpleefStorage extends THashMap<Enum<?>, Object> {
	
	private static final long serialVersionUID = 1L;
	
	private Elysian main;
	private String name;
	private SpleefDataType type = SpleefDataType.SYSTEM;
	
	public SpleefStorage(Elysian i, SpleefDataType dataType, String name){
		this.name = name;
		type = dataType;
		main = i;
		((Spleef) main.api.getInstance(Spleef.class)).module.data.put(name, this);
		start();
	}
	
	private SpleefPlayer spleefPlayer;
	private SpleefGame spleefGame;
	
	public void start(){
		
		spleefPlayer = type.equals(SpleefDataType.PLAYER) ? new SpleefPlayer(){

			private DivinityPlayer dp = main.api.getDivPlayer(name);
			private SpleefPlayer opponent;
			private SpleefPlayer invite;
			private SpleefGame currentGame;
			private boolean inGame = false;
			private int points = 0;
			
			public void putt(Enum<?> enumm, Object o){
				put(enumm, o);
			}
			
			public Object gett(Enum<?> enumm){
				return get(enumm);
			}
	
			public SpleefPlayer opponent(){
				return opponent;
			}
			
			public SpleefPlayer getInvite(){
				return invite;
			}
	
			public SpleefGame currentGame(){
				return currentGame;
			}
			
			public DivinityPlayer toDp(){
				return dp;
			}

			public int getPoints(){
				return points;
			}
	
			public boolean inGame(){
				return inGame;
			}
			
			public void setOpponent(SpleefPlayer player){
				opponent = player;
			}
			
			public void setCurrentGame(SpleefGame game){
				currentGame = game;
			}
			
			public void setInGame(boolean inGame){
				this.inGame = inGame;
			}
			
			public void setInvite(SpleefPlayer player){
				invite = player;
			}
			
			public void addPoint(){
				points++;
			}
			
			public void setPoints(int point){
				points = point;
			}
		
		} : null;
	
		spleefGame = type.equals(SpleefDataType.GAME) ? new SpleefGame(){
	
			private List<SpleefPlayer> players = new ArrayList<SpleefPlayer>();
			private boolean enabled = true;
			
			public boolean isEnabled(){
				return enabled;
			}
			
			public List<SpleefPlayer> involvedPlayers(){
				return players;
			}
	
			public void bc(String message){
				for (SpleefPlayer p : involvedPlayers()){
					p.toDp().s(message);
				}
			}
			
			public void putt(Enum<?> enumm, Object o){
				put(enumm, o);
			}
			
			public void setEnabled(boolean enable){
				enabled = enable;
			}
			
			public void teleportPlayers(){
				
				for (int i = 0; i < 2; i++){
					String[] loc = i == 0 ? ((String) get(SpleefGameData.PLAYER_START_1)).split(" ") : ((String) get(SpleefGameData.PLAYER_START_2)).split(" "); 
					main.api.event(new DivinityTeleportEvent(Bukkit.getPlayer(players.get(i).toDp().uuid()), new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]))));
					Bukkit.getPlayer(players.get(i).toDp().uuid()).setWalkSpeed(0);
				}
				
				new Thread(new Runnable(){ public void run(){
					
					int count = 5;
					
					for (int i = 0; i < 5; i++){
						
						bc(count + "...");
						count = count - 1;
						
						if (count == 0){
							bc("GO!");
							Bukkit.getPlayer(players.get(0).toDp().uuid()).setWalkSpeed(0.2F);
							Bukkit.getPlayer(players.get(1).toDp().uuid()).setWalkSpeed(0.2F);
						}
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}}).start();
			}
			
			public Object gett(Enum<?> enumm){
				return get(enumm);
			}
			
		} : null;
	}
	
	public String name(){
		return name;
	}
	
	public SpleefDataType type(){
		return type;
	}
	
	public SpleefPlayer toPlayer(){
		return spleefPlayer;
	}
	
	public SpleefGame toGame(){
		return spleefGame;
	}
	
	public SpleefStorage toStorage(){
		return this;
	}
	
	public DivinityStorageModule toDivStorage(){
		
		DivinityStorageModule game = new DivinityStorageModule("spleef", name, main.divinity.api);
		
		for (SpleefGameData data : SpleefGameData.values()){
			game.set(data.s(), get(data));
		}
		
		return game;
	}
}*/