package com.github.lyokofirelyte.Empyreal.Elysian;

import java.sql.ResultSet;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Material;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.JSONMap;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;

public class ElyMarkkitItem {
	
	@Getter
	Material material;
	
	@Getter @Setter
	int damage;
	
	@Getter @Setter
	int id;
	
	@Getter @Setter
	String name;
	
	@Getter @Setter
	public Empyreal api;
	
	@Getter @Setter
	private DivinitySystem system;
	
	private EmpyrealSQL sql;

	public ElyMarkkitItem(Empyreal i, Material mat, int data){
		setApi(i);
		sql = api.getInstance(EmpyrealSQL.class).getType();
		setSystem(api.getDivSystem());
		setMaterial(mat, data);
	}
	
	@SneakyThrows
	public ElyMarkkitItem(Empyreal i, String signname){
		setApi(i);
		sql = api.getInstance(EmpyrealSQL.class).getType();
		setSystem(api.getDivSystem());
		ResultSet id = sql.getResult("markkit", "id", "name = '" + signname + "'");
		id.next();
		ResultSet data = sql.getResult("markkit", "damage", "name = '" + signname + "'");
		data.next();
		setMaterial(Material.getMaterial(id.getInt(1)), data.getInt(1));
		setName(signname);
	}
	
	@SneakyThrows
	public int getStackSellPrice(){
		ResultSet rs = sql.getResult("markkit", "sellprice_64", "name='" + getName() + "'");
		rs.next();
		return rs.getInt(1);
	}

	@SneakyThrows
	public int getStackBuyPrice(){
		ResultSet rs = sql.getResult("markkit", "buyprice_64", "name='" + getName() + "'");
		rs.next();
		return rs.getInt(1);
	}
	
	public boolean setMaterial(Material mat, int data){
		material = mat;
		setId(mat.getId());
		setDamage(data);
		String name = getSignName();
		if(name == "none"){
			return false;
		}
		setName(name);
		return true;
	}
	
	@SneakyThrows
	public int getSellPrice(int i){
		ResultSet rs = sql.getResult("markkit", "sellprice_64", "name='" + getName() + "'");
		rs.next();
		rs.getInt(1);
		if(rs.wasNull() || rs.getString(1).equals("none")){
			rs = sql.getResult("markkit", "sellprice_1", "name='" + getName() + "'");
			rs.next();
			return rs.getInt(1) * i;
		}else{
			return rs.getInt(1) * i / 64;
		}
	}
	
	@SneakyThrows
	public int getBuyPrice(int i){
		ResultSet rs = sql.getResult("markkit", "buyprice_64", "name='" + getName() + "'");
		rs.next();
		rs.getInt(1);
		if(rs.wasNull() || rs.getString(1).equals("none")){
			rs = sql.getResult("markkit", "buyprice_1", "name='" + getName() + "'");
			rs.next();
			return rs.getInt(1) * i;
		}else{
			return rs.getInt(1) * i / 64;
		}
	}
	
	@SneakyThrows
	public int getInStock(){
		ResultSet rs = sql.getResult("markkit", "instock", "name='" + getName() + "'");
		rs.next();
		return rs.getInt(1);	
	}
	
	public int getAmountForPrice(int money){
		return money / getBuyPrice(1);
	}
	
	public void setInStock(int i){
		//sql.injectData("markkit", "instock", i + "", "name='" + getName() + "'");
	}
	
	@SneakyThrows
	public boolean isSellDoubled(){
		ResultSet rs = sql.getResult("markkit", "isselldoubled", "name='" + getName() + "'");
		rs.next();
		String bool = rs.getString(1);
		if(rs.wasNull()){
			return false;
		}
		return bool.contains("true");		
	}
	
	public void setSellDoubled(boolean doubled){
		//sql.injectData("markkit", "isselldoubled", ("'" + (doubled + "") + "_BOOLEAN_'"), "name='" + getName() + "'");
	}
	
	@SneakyThrows
	public String getSignName(){
//		System.out.println(getId() + " " + getDamage());
		if(doesItemExist(getId())){
			ResultSet rs = sql.getResult("markkit", "name", "id='" + getId() + "' and damage='" + getDamage() + "'");
			rs.next();
			//System.out.println(rs.getString(1));
			return rs.getString(1);
		}
		return "none";
	}
	
	@SneakyThrows
	public boolean doesItemExist(int id){
		ResultSet itemCheck = sql.getResult("markkit", "count(*)", "id='" + id + "'");
		itemCheck.next();
		if(itemCheck.getInt(1) == 0){
			return false;
		}else{
			return true;
		}
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