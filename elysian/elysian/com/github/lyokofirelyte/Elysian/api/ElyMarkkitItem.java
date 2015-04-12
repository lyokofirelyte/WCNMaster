package com.github.lyokofirelyte.Elysian.api;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Elysian.DivinitySystem;

public class ElyMarkkitItem {
	Material material;
	int damage;
	public Empyreal api;
	private DivinitySystem system;

	public ElyMarkkitItem(Empyreal i, Material mat, int data){
		material = mat;
		damage = data;
		api = i;
		system = api.getDivSystem();
	}
	
	public ElyMarkkitItem(Empyreal i, String signname){
		api = i;
		system = api.getDivSystem();
		material = Material.getMaterial(system.getMarkkit().getInt("Items." + signname + ".ID"));
		damage = system.getMarkkit().getInt("Items." + signname + ".Damage");
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public int getDurability(){
		return damage;
	}
	
	public void setMaterial(Material material){
		this.material = material;
	}
	
	public void setDurability(int durability){
		this.damage = durability;
	}
	
	public int getMaterialID(){
		return material.getId();
	}
	
	public int getStackSellPrice(){
		return api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".sellprice");
	}

	public int getStackBuyPrice(){
		return api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".buyprice");
	}
	
	public int getSellPrice(int i){
		if(api.getDivSystem().getMarkkit().get("Items." + getSignName() + ".64") == null){
			return api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + 1 + ".sellprice") * i;
		}else{
			return api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".sellprice") * i / 64;
		}
	}
	
	public int getBuyPrice(int i){
		try {
			return api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + i + ".buyprice");
		} catch (Exception e){
			return api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".buyprice") * i / 64;
		}
	}
	
	public int getInStock(){
		return api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + ".inStock");
	}
	
	public int getAmountForPrice(int money){
		return money / getBuyPrice(1);
	}
	
	public void setInStock(int i){
		api.getDivSystem().getMarkkit().set("Items." + getSignName() + ".inStock", i);
	}
	public boolean isSellDoubled(){
		return api.getDivSystem().getMarkkit().getBoolean("Items." + getSignName() + ".isSellDoubled");
	}
	
	public void setSellDoubled(boolean doubled){
		api.getDivSystem().getMarkkit().set("Items." + getSignName() + ".isSellDoubled", doubled);
	}
	public String getSignName(){
		 ConfigurationSection configSection = api.getDivSystem().getMarkkit().getConfigurationSection("Items");
		 String text = "§fNot Found";
		for (String path : configSection.getKeys(false)){
			 if((Integer.parseInt(api.getDivSystem().getMarkkit().getString("Items." + path + ".ID")) == material.getId()) && (Integer.parseInt(api.getDivSystem().getMarkkit().getString("Items." + path + ".Damage")) == damage)){
				 text = path;
				 return text;
			 }
		 }
		return text;
	}
}

/*class ElyPlayerItem{
	public Elysian main;
	Player p;
	Material m;
	int damage;
	
	ElyPlayerItem(Elysian main, Player p, Material m, int damage){
		this.main = main;
		this.p = p;
		this.m = m;
		this.damage = damage;
	}
}*/