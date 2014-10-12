package com.github.lyokofirelyte.Elysian.MMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtilsModule;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Divinity.Manager.JSONManager.JSONClickType;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.google.common.collect.ImmutableMap;

public class ElyPatrol {
	
	private Elysian main;

	public ElyPatrol(Elysian i) {
		main = i;
	}

	Map<String, Patrol> patrols = new THashMap<>();
	
	public void createPatrol(final String name){
		
		if (!patrols.containsKey(name)){
			patrols.put(name, new Patrol(){
				
				private List<String> members = new ArrayList<String>();
				private Map<String, List<String>> kickVotes = new THashMap<>();
				private String memberString = "";
				private String n = name;
				
				public List<String> getMembers(){
					return members;
				}
				
				public List<String> getKickVotes(String name){
					return kickVotes.get(name);
				}
				
				public boolean hasKickVotes(String name){
					return kickVotes.containsKey(name);
				}
				
				public void addKickVote(String name, String kicker){
					if (!kickVotes.containsKey(name)){
						kickVotes.put(name, new ArrayList<String>());
					}
					kickVotes.get(name).add(kicker);
				}
				
				public String getMembersAsString(){
					memberString = "";
					for (String member : members){
						memberString = memberString.equals("") ? "&6" + member : memberString + "&7, &6" + member;
					}
					return memberString;
				}
				
				public String name(){
					return n;
				}
			});
		}
	}
	
	public Patrol getPatrol(String name){
		return patrols.containsKey(name) ? patrols.get(name) : null;
	}
	
	public Patrol getPatrolWithPlayer(Player p){
		
		for (Patrol patrol : patrols.values()){
			for (String player : patrol.getMembers()){
				if (player.equals(p.getName())){
					return patrol;
				}
			}
		}
		
		return null;
	}
	
	public boolean doesPatrolExist(String name){
		return patrols.containsKey(name);
	}
	
	public boolean doesPatrolExistWithPlayer(Player p){
		
		if (patrols.size() <= 0){
			return false;
		}
		
		for (Patrol patrol : patrols.values()){
			for (String player : patrol.getMembers()){
				if (player.equals(p.getName())){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Collection<Patrol> getAllPatrols(){
		return patrols.values();
	}

	public interface Patrol {
		List<String> getKickVotes(String name);
		List<String> getMembers();
		boolean hasKickVotes(String name);
		void addKickVote(String name, String kicker);
		String name();
		String getMembersAsString();
	}
	
	public void sendMessage(Patrol patrol, JSONChatMessage message){
		for (String player : patrol.getMembers()){
			message.sendToPlayer(main.api.getPlayer(player));
		}
	}
	
	public void sendMessage(Patrol patrol, String message){
		for (String player : patrol.getMembers()){
			main.s(main.api.getPlayer(player), message);
		}
	}
	
	@DivCommand(aliases = {"p"}, desc = "Elysian Patrol Chat Command", help = "/p <message>", player = true)
	public void onPatrolChat(Player p, String[] args){
		
		if (doesPatrolExistWithPlayer(p)){
			
			DivinityPlayer dp = main.api.getDivPlayer(p);
			String xpColor = !dp.getBool(DPI.SHARE_XP) ? "&a" : "&c";
			String xpName = !dp.getBool(DPI.SHARE_XP) ? "&a&osharing" : "&c&onot sharing";
			Location l = p.getLocation();
			
			sendMessage(getPatrolWithPlayer(p), ((JSONChatMessage) main.divinity.api.createJSON("&3P &7\u2744 ", ImmutableMap.of(
					
				"&7[&8TP&7] ", ImmutableMap.of(
					JSONClickType.CLICK_RUN, new String[]{
						"/patrol #tp " + p.getName(),
						"&b&oTeleport to this player!\n&c&oRequires patrol level 20 or higher.\n&c&o10 minute cooldown."
					}
				),
				
				"&7[" + xpColor + "XP&7] ", ImmutableMap.of(
					JSONClickType.CLICK_RUN, new String[]{
						"/patrol #xp " + p.getName(),
						"&7&oThis player is " + xpName + " &7&oXP with the group.\n&b&oIf this is on, all patrol members gain 15% of all xp gained.\n&b&oThe person obtaining the xp still receives 100%.\nClick to toggle."
					}
				),
				
				"&7[&cx&7] ", ImmutableMap.of(
					JSONClickType.CLICK_RUN, new String[]{
						"/patrol #kick " + p.getName() + " " + getPatrolWithPlayer(p).name(),
						"&7&oVote to kick this player. Requires 51% approval or higher."
					}
				),
				
				p.getDisplayName() + "&f: &3" + DivinityUtilsModule.createString(args, 0), ImmutableMap.of(
					JSONClickType.NONE, new String[]{
						"&7This player is located at &6" + l.getBlockX() + "&7, &6" + l.getBlockY() + "&7, &6" + l.getBlockZ() + "&7."
					}
				)
				
			))));
			
		} else {
			main.s(p, "&c&oYou're not in a patrol. See /patrol.");
		}
	}
	
	@DivCommand(aliases = {"patrol"}, desc = "Elysian Patrol Command", help = "/patrol help", player = true)
	public void onPatrol(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (args.length > 0){
			
			switch (args[0]){
			
				case "#join":
					
					if (!doesPatrolExistWithPlayer(p)){
						getPatrol(args[1]).getMembers().add(p.getName());
						sendMessage(getPatrol(args[1]), "&6System&f: " + p.getDisplayName() + " &bhas joined the patrol!");
						dp.s("Type /p to type in patrol chat! Hover over options near your name.");
					} else {
						dp.err("You're already in a patrol! Leave it first.");
					}
					
				break;
				
				case "#leave":
					
					if (doesPatrolExistWithPlayer(p)){
						getPatrol(args[1]).getMembers().remove(p.getName());
						sendMessage(getPatrol(args[1]), "&6System&f: " + p.getDisplayName() + " &bhas left the patrol!");
						dp.s("You've left your patrol.");
					} else {
						dp.err("You're not in a patrol.");
					}
					
				break;
				
				case "#createInput":
					
					if (getAllPatrols().size() >= 10 || doesPatrolExistWithPlayer(p)){
						dp.err("There are already the max amount of patrols (or you are in one).");
					} else {
						dp.s("Please type the name of your patrol in chat.");
						dp.set(DPI.PATROL_INPUT, true);
					}
					
				break;
				
				case "#xp":
					
					if (args[1].equals(p.getName())){
						dp.set(DPI.SHARE_XP, !dp.getBool(DPI.SHARE_XP));
						dp.s("Updated!");
					} else {
						dp.err("You're not " + args[1] + "!");
					}
					
				break;
				
				case "#kick":
					
					Patrol patrol = getPatrol(args[2]);
					
					if (main.api.isOnline(args[1])){
					
						if (patrol.hasKickVotes(args[1])){
							if (patrol.getKickVotes(args[1]).contains(p.getName())){
								dp.err("You've already voted to kick this user.");
								return;
							}
						}
						
						patrol.addKickVote(args[1], p.getName());
						
						if (patrol.getKickVotes(args[1]).size() > patrol.getMembers().size()/2){
							sendMessage(patrol, "&6System&f: &6&o" + args[1] + " &c&owas kicked from the patrol.");
						} else {
							sendMessage(patrol, "&6System&f: " + p.getDisplayName() + " &b&ohas voted to kick &6&o" + args[1] + " &b&ofrom the patrol.");
						}
							
					} else {
						sendMessage(patrol, "&6System&f: &c&oFailed to kick &6&o" + args[1] + "&c&o, player offline!");
					}
					
				break;
				
				case "#add":
					
					dp.set(DPI.PATROL_INPUT, false);
					createPatrol(args[1]);
					onPatrol(p, new String[]{});
					
					DivinityUtilsModule.bc("Patrol formed by " + p.getDisplayName() + " &6(&3" + args[1] + "&6)&b!");
					
				break;
				
				case "#tp":
					
					if (dp.getLevel(ElySkill.PATROL) >= 20){
						if (System.currentTimeMillis() >= dp.getLong(DPI.PATROL_TP_COOLDOWN)){
							if (main.api.isOnline(args[1])){
								dp.set(DPI.PATROL_TP_COOLDOWN, System.currentTimeMillis() + 600000L);
								main.api.event(new DivinityTeleportEvent(p, main.api.getPlayer(args[1]).getLocation()));
								sendMessage(getPatrolWithPlayer(p), "&6System&f: &bPatrol TP " + p.getDisplayName() + " &b-->> " + main.api.getPlayer(args[1]).getDisplayName());
							} else {
								dp.err("That player is no longer online!");
							}
						} else {
							dp.err("Cooldown! &6" + (dp.getLong(DPI.PATROL_TP_COOLDOWN) - System.currentTimeMillis()/1000 + " &bseconds remain!"));
						}
					} else {
						dp.err("You must be patrol level 20 to use this.");
					}
					
				break;
			}
			
		} else {
		
			main.s(p, main.divinity.api.createJSON("&3Elysian Patrol System ", ImmutableMap.of(
				"&a{+}", ImmutableMap.of(
					JSONClickType.CLICK_RUN, new String[]{
						"/patrol #createInput",
						"&7&oCreate a new patrol!"
					}
				)
			)));
			
			p.sendMessage("");
			
			for (Patrol patrol : getAllPatrols()){
							
				main.s(p, main.divinity.api.createJSON("&6" + patrol.name() + " ", ImmutableMap.of(
								
					"&f[&e" + patrol.getMembers().size() + "/10&f] ", ImmutableMap.of(
						JSONClickType.NONE, new String[]{
							patrol.getMembersAsString()
						}
					),
								
					"&f[&aJoin&f] ", ImmutableMap.of(
						JSONClickType.CLICK_RUN, new String[]{
							"/patrol #join " + patrol.name(),
							"&7&oJoin this patrol!"
						}
					),
								
					"&f[&cLeave&f]", ImmutableMap.of(
						JSONClickType.CLICK_RUN, new String[]{
							"/patrol #leave " + patrol.name(),
							"&7&oLeave this patrol!"
						}
					)
								
				)));
			}
			
			p.sendMessage("");
		}
	}
}