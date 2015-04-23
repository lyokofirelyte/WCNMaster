package com.github.lyokofirelyte.Empyreal.Elysian;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Database.DPI;
import com.github.lyokofirelyte.Empyreal.Database.EmpyrealSQL;
import com.github.lyokofirelyte.Empyreal.Utils.Direction;
import com.github.lyokofirelyte.Empyreal.Utils.Letter;
import com.github.lyokofirelyte.Empyreal.Utils.ParticleEffect;

public class DivinitySystem extends DivinityStorageModule {

	private static final long serialVersionUID = -3369945641382205397L;

	public DivinitySystem(Empyreal i, String n) {
		super(n, i, "system");
		set(DPI.DISPLAY_NAME, "&6Console");
		set(DPI.PM_COLOR, "&f");
		reloadMarkkit();
	}
	
	private YamlConfiguration markkitYaml;
	
	public YamlConfiguration getMarkkit(){
		return markkitYaml;
	}
	
	public void reloadMarkkit(){
		markkitYaml = YamlConfiguration.loadConfiguration(new File("./plugins/Divinity/system/markkit.yml"));
	}
	
	public void saveMarkkit(){
		try {
			markkitYaml.save(new File("../wa/Divinity/system/markkit.yml"));
		} catch (Exception e){
			System.out.println("FAILED TO SAVE MARKKIT!");
		}
	}
	
	public void addLetterEffect(String name, ParticleEffect eff, Location center, Direction dir, long cycleDelay){
		Location l = new Location(center.getWorld(), center.getX(), center.getY(), center.getZ());
		api.repeat(this, "playLetterEffect", 0L, cycleDelay, "effects" + name, name, eff, l, dir);
	}
	
	public void addEffect(String name, ParticleEffect eff, int offsetX, int offsetY, int offsetZ, int speed, int amount, Location center, int range, long cycleDelay){
		api.repeat(this, "playEffect", 0L, cycleDelay, "effects" + name, eff, offsetX, offsetY, offsetZ, speed, amount, center, range);
	}
	
	public void remEffect(String name){
		cancelEffect(name);
	}
	
	public void playEffect(ParticleEffect eff, int offsetX, int offsetY, int offsetZ, int speed, int amount, Location center, int range){
		eff.display(offsetX, offsetY, offsetZ, speed, amount, center, range);
	}
	
	public void playLetterEffect(String name, ParticleEffect eff, Location center, Direction dir){
		Letter.centreString(name, eff, center, dir);
	}
	
	public void cancelEffect(String name){
		api.cancelTask("effects" + name);
	}
	
	@Override
	public void save(){
		api.getInstance(EmpyrealSQL.class).getType().saveMapToDatabase("system", this);
	}
	
	public void transfer(){
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("../wa/Divinity/system/" + getName() + ".yml"));
		
		for (String key : yaml.getKeys(false)){
			set(key, yaml.get(key));
		}
		
		save();
	}
}