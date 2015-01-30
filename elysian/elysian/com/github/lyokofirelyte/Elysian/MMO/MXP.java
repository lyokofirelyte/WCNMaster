package com.github.lyokofirelyte.Elysian.MMO;

import java.util.HashMap;
import java.util.Map;

import gnu.trove.map.hash.THashMap;

import org.bukkit.Material;

import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;

public class MXP extends THashMap<ElySkill, Integer[]>{

	private static final long serialVersionUID = 1L;
	private Material material;
	public Map<Material, Map<ElySkill, Integer>> toolReqs = new THashMap<>();
	
	public MXP(Material m, ElySkill skill, int level, int xp){
		material = m;
		put(skill, new Integer[]{level, xp});
	}
	
	public Material getMat(){
		return material;
	}
	
	public boolean canUseTool(ElySkill skill, Material m, int level){
		return toolReqs.containsKey(m) && toolReqs.get(m).containsKey(skill) ? toolReqs.get(m).get(skill) <= level : true;
	}
	
	public boolean hasSkill(ElySkill skill){
		return containsKey(skill);
	}
	
	public boolean hasLevel(ElySkill skill, int level){
		return containsKey(skill) ? get(skill)[0] <= level : true;
	}
	
	public int getXP(ElySkill skill){
		return containsKey(skill) ? get(skill)[1] : 0;
	}
	
	public int getNeededLevel(ElySkill skill){
		return containsKey(skill) ? get(skill)[0] : 0;
	}
	
	public int getNeededToolLevel(ElySkill skill, Material m){
		return toolReqs.containsKey(m) && toolReqs.get(m).containsKey(skill) ? toolReqs.get(m).get(skill) : 0;
	}
	
	public void addSkill(ElySkill skill, int level, int xp){
		put(skill, new Integer[]{level, xp});
	}
	
	public void addTool(ElySkill skill, int level, Material tool){
		if (!toolReqs.containsKey(tool)) { toolReqs.put(tool, new THashMap<ElySkill, Integer>()); }
		toolReqs.get(tool).put(skill, level);
	}
}