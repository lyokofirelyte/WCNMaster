package com.github.lyokofirelyte.Elysian;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;

public class ElyMarkkitItem {
	Material material;
	int damage;
	public Elysian main;
	private DivinitySystem system;

	public ElyMarkkitItem(Elysian i, Material mat, int data){
		material = mat;
		damage = data;
		main = i;
		system = main.api.getDivSystem();
	}
	
	public ElyMarkkitItem(Elysian i, String signname){
		main = i;
		system = main.api.getDivSystem();
		material = Material.getMaterial(system.getMarkkit().getInt("Items." + signname + ".ID"));
		damage = system.getMarkkit().getInt("Items." + signname + ".Damage");
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public int getDurability(){
		return damage;
	}
	
	public int getMaterialID(){
		return material.getId();
	}
	
	public int getStackSellPrice(){
		return main.api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".sellprice");
	}
	
	public int getStackBuyPrice(){
		return main.api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".buyprice");
	}
	
	public int getSellPrice(int i){
		return main.api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".sellprice") * i / 64;
	}
	
	public int getBuyPrice(int i){
		return main.api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".buyprice") * i / 64;
	}
	
	public int getInStock(){
		return main.api.getDivSystem().getMarkkit().getInt("Items." + getSignName() + ".inStock");
	}
	
	public void setInStock(int i){
		main.api.getDivSystem().getMarkkit().set("Items." + getSignName() + ".inStock", i);
	}
	public boolean isSellDoubled(){
		return main.api.getDivSystem().getMarkkit().getBoolean("Items." + getSignName() + ".isSellDoubled");
	}
	
	public void setSellDoubled(boolean doubled){
		main.api.getDivSystem().getMarkkit().set("Items." + getSignName() + ".isSellDoubled", doubled);
	}
	public String getSignName(){
		 ConfigurationSection configSection = main.api.getDivSystem().getMarkkit().getConfigurationSection("Items");
		 String text = "§fNot Found";
		for (String path : configSection.getKeys(false)){
			 if((Integer.parseInt(main.api.getDivSystem().getMarkkit().getString("Items." + path + ".ID")) == material.getId()) && (Integer.parseInt(main.api.getDivSystem().getMarkkit().getString("Items." + path + ".Damage")) == damage)){
				 text = path;
				 return text;
			 }
		 }
		return text;
	}
}

class ElyPlayerItem{
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
}