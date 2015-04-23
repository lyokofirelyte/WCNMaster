package com.github.lyokofirelyte.Elysian.MMO.Abilities;

import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Precision;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Commands.ElyProtect;
import com.github.lyokofirelyte.Elysian.MMO.MMO;
import com.github.lyokofirelyte.Empyreal.Database.DRF;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityPlayer;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinityUtilsModule;
import com.github.lyokofirelyte.Empyreal.Elysian.ElySkill;

public class TreeFeller {
	
	public Elysian main;

	public TreeFeller(Elysian i) {
		main = i;
	}

	public void r(Player p, DivinityPlayer dp){
		dp.set(MMO.IS_TREE_FELLING, !dp.getBool(MMO.IS_TREE_FELLING));
		dp.s("Tree feller " + (dp.getBool(MMO.IS_TREE_FELLING) + "").replace("true", "&aactive! Left click the bottom of a tree to ninja!").replace("false", "&cinactive."));
	}
	
	public void l(Player p, DivinityPlayer dp, Block b){
		
		ElyProtect pro = (ElyProtect) main.api.getInstance(ElyProtect.class);
		String result = pro.isInAnyRegion(b.getLocation());
		
		if (pro.hasFlag(result, DRF.BLOCK_BREAK)){
			if (!pro.hasRegionPerms(p, result)){
				dp.err("No permissions for this area!");
				return;
			}
		}
		
		if (dp.getLong(MMO.TREE_FELLER_CD) <= System.currentTimeMillis()){
			chop(p, dp, b.getLocation());
			dp.set(MMO.TREE_FELLER_CD, System.currentTimeMillis() + (180000 - (dp.getLevel(ElySkill.WOODCUTTING)*1000)));
		} else {
			dp.err("Tree feller on cooldown! &6" + ((System.currentTimeMillis() - dp.getLong(MMO.TREE_FELLER_CD))/1000)*-1 + " &c&oseconds remain.");
		}
	}
	
	private boolean isNatural(Location l){
		
		String loc = l.toVector().getBlockX() + "," + l.toVector().getBlockZ();
		int y = l.toVector().getBlockY();
		float x = Precision.round(l.toVector().getBlockX(), -3);
		float z = Precision.round(l.toVector().getBlockZ(), -3);
			
		File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
			
		if (!file.exists()){
			return true;
		}
		
		if (!YamlConfiguration.loadConfiguration(file).contains("History." + l.getWorld().getName() + "." + y)){
			return true;
		}
		
		return false;
	}
	
	private void chop(Player p, DivinityPlayer dp, Location l){
		
		boolean skyOpen = true;
		int bottom = l.getBlockY()-1;
		int top = 0;
		
		Map<Integer, List<Block>> blocks = new THashMap<Integer, List<Block>>();
		
		for (int i = l.getBlockY(); i < 256; i++){
			Location testLoc = new Location(l.getWorld(), l.getX(), i, l.getZ());
			if (!isType(testLoc, "log") && !isType(testLoc, "leaves")){
				top = top == 0 ? i : top;
				if (!isType(testLoc, "air")){
					skyOpen = false;
					break;
				}
			} else if (isType(testLoc, "log") || isType(testLoc, "log")){
				if (!isNatural(testLoc)){
					dp.err("There are player-built logs in the way! Can't continue.");
					dp.set(MMO.IS_TREE_FELLING, false);
					dp.set(MMO.IS_CHOPPING, false);
					dp.set(MMO.TREE_FELLER_CD, 0);
					return;
				}
				List<Block> b = new ArrayList<Block>();
				b.add(testLoc.getBlock());
				blocks.put(i, b);
				for (Location radiusLoc : DivinityUtilsModule.circle(testLoc, 7, 1, false, false, 0)){
					if (isType(radiusLoc, "log") || isType(radiusLoc, "leaves")){
						blocks.get(i).add(radiusLoc.getBlock());
					}
				}
			}
		}
		
		p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 5F, 5F);
		dp.set(MMO.IS_CHOPPING, true);
		
		if (skyOpen){
			dp.s("The sky is clear! Wooosssh!");
			p.teleport(new Location(l.getWorld(), l.getX(), top+3, l.getZ(), p.getLocation().getYaw(), 90));
			p.setVelocity(new Vector(0, 3, 0));
			main.api.schedule(this, "tpPlayer", 30L, "tpPlayer", p, new Location(l.getWorld(), l.getX(), top + 10, l.getZ(), p.getLocation().getYaw(), 90));
			main.api.repeat(this, "checkPlayer", 35L, 1L, "treeCheck" + p.getName(), p, top, bottom, blocks);
			main.api.schedule(this, "checkPlayerSaftey", 400L, "treeCheck2", p);
		} else {
			dp.s("No room for flight attack! Arming explosives!");
			for (int i : blocks.keySet()){
				for (Block b : blocks.get(i)){
					//main.logger.addToQue(b.getLocation(), "&b" + p.getName(), "&etree-felled &b" + b.getType().name().toLowerCase(), "break", b.getType().name().toLowerCase() + "split" + b.getData(), "AIRsplit0");
					b.breakNaturally();
				}
			}
			dp.set(MMO.IS_TREE_FELLING, false);
			dp.set(MMO.IS_CHOPPING, false);
		}
	}
	
	public void tpPlayer(Player p, Location l){
		p.teleport(l);
		p.setVelocity(new Vector(0, -1, 0));
	}
	
	public void checkPlayerSaftey(Player p){
		try {
			main.api.getDivPlayer(p).set(MMO.IS_TREE_FELLING, false);
			main.api.getDivPlayer(p).set(MMO.IS_CHOPPING, false);
			main.api.cancelTask("treeCheck" + p.getName());
		} catch (Exception e){}
	}
	@SuppressWarnings("deprecation")
	public void checkPlayer(Player p, int top, int bottom, Map<Integer, List<Block>> blocks){
		
		int y = p.getLocation().getBlockY();

		if (blocks.containsKey(y-1)){
			for (Block b : blocks.get(y-1)){
				if (isType(b, "log")){
					p.playEffect(b.getLocation(), Effect.STEP_SOUND, b.getTypeId());
				}
				//main.logger.addToQue(b.getLocation(), "&b" + p.getName(), "&etree-felled &b" + b.getType().name().toLowerCase(), "break", b.getType().name().toLowerCase() + "split" + b.getData(), "AIRsplit0");
				b.breakNaturally();
			}
		}
		
		if (y <= bottom+2){
			main.api.cancelTask("treeCheck" + p.getName());
			main.s(p, "You showed that tree!");
			main.api.getDivPlayer(p).set(MMO.IS_TREE_FELLING, false);
			main.api.getDivPlayer(p).set(MMO.IS_CHOPPING, false);
			for (Location l : DivinityUtilsModule.circle(p.getLocation(), 3, 1, true, false, 0)){
				l.getWorld().playEffect(l, Effect.ENDER_SIGNAL, 2);
			}
		}
	}
	
	public boolean isType(Block b, String item){
		return b != null && b.getType().toString().toLowerCase().contains(item.toLowerCase());
	}
	
	public boolean isType(Location l, String item){
		return l.getBlock() != null && l.getBlock().getType().toString().toLowerCase().contains(item.toLowerCase());
	}
}