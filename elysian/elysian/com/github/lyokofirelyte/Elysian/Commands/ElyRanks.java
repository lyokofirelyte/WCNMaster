package com.github.lyokofirelyte.Elysian.Commands;

import lombok.Getter;

import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Empyreal.Command.DivCommand;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Empyreal.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

public class ElyRanks implements AutoRegister<ElyRanks> {

	private Elysian main;
	
	@Getter
	private ElyRanks type = this;
	
	public ElyRanks(Elysian i){
		main = i;
	}
	
	@DivCommand(aliases = {"rankup"}, desc = "Elysian Rankup Command", help = "/rankup", player = true)
	public void onRankup(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		ElyPerms perms = (ElyPerms) main.api.getInstance(ElyPerms.class);
		
		if (dp.getList(DPI.PERMS).contains("wa.rank.immortal")){
			main.s(p, "&c&oYou are currently the highest rank!");
			return;
		}
		
		if (dp.getStr(DPI.RANK_NAME).equalsIgnoreCase("Guest")){
			
			DivinityUtilsModule.bc(p.getDisplayName() + " &bis now a member of WA!");
			dp.set(DPI.RANK_NAME, "M");
			dp.set(DPI.RANK_COLOR, "&7");
			dp.set(DPI.RANK_DESC, "&7&oA registered member of the server!\n&6/home, build access.");
			dp.set(DPI.STAFF_DESC, "&7&oA registered member!");
			
		} else {
		
			for (String s : perms.memberGroups){
				if (!dp.getList(DPI.PERMS).contains("wa.rank." + s) && !s.equals("member")){
					
					String[] rank = perms.rankNames.get(s).split(" % ");
					
					if (dp.getInt(DPI.BALANCE) >= Integer.parseInt(rank[1].replace("k", "000").replace("m", "000000"))){
						dp.getList(DPI.PERMS).add("wa.rank." + s);
						dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) - Integer.parseInt(rank[1].replace("k", "000").replace("m", "000000")));
						dp.set(DPI.RANK_COLOR, rank[0]);
						dp.set(DPI.RANK_DESC, rank[0] + s.substring(0, 1).toUpperCase() + s.substring(1) + "\n" + "&6" + rank[3].replace(", ", "&7, &6"));
						dp.set(DPI.RANK_NAME, !main.api.perms(p, "wa.staff.intern", false) ? s.substring(0, 1).toUpperCase() : dp.getStr(DPI.RANK_NAME));
						DivinityUtilsModule.bc(p.getDisplayName() + " &bhas been promoted to &6" + s.substring(0, 1).toUpperCase() + s.substring(1) + "&b!");
						main.fw(p.getWorld(), p.getLocation(), Type.BURST, DivinityUtilsModule.getRandomColor());
						main.s(p, "&3&oNew Unlocks:");
						main.s(p, "Sunday percentage gain increased to: &6" + rank[2] + "%");
						main.s(p, "Access to: &6" + rank[3].replace(", ", "&7, &6"));
					} else {
						main.s(p, "&c&oInsufficient funds! You need &6&o" + rank[1]);
					}
					
					break;
				}
			}
		}
	}

	@DivCommand(aliases = {"ranks"}, desc = "Ranks Command", help = "/ranks", player = true)
	public void onRanks(Player p, String[] args){
		
		ElyPerms perms = (ElyPerms) main.api.getInstance(ElyPerms.class);
		
		for (String s : perms.memberGroups){
			String[] rank = perms.rankNames.get(s).split(" % ");
			JSONChatMessage message = new JSONChatMessage("", null, null);
			JSONChatExtra extra = new JSONChatExtra(main.AS(rank[0] + s.substring(0, 1).toUpperCase() + s.substring(1) + " &3- &c" + rank[1] + " &3- &2" + rank[2] + "%"), null, null);
			extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&o" + rank[3]));
			message.addExtra(extra);
			message.sendToPlayer(p);
		}
		main.s(p, "&7&oHover for reward information.");
	}
}