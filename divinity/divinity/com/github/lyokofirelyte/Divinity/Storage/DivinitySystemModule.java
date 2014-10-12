package com.github.lyokofirelyte.Divinity.Storage;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lyokofirelyte.Divinity.API;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Spectral.DataTypes.DPI;
import com.github.lyokofirelyte.Spectral.Public.Direction;
import com.github.lyokofirelyte.Spectral.Public.Letter;
import com.github.lyokofirelyte.Spectral.Public.ParticleEffect;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;

public class DivinitySystemModule extends DivinityStorageModule implements DivinitySystem {

	public DivinitySystemModule(String n, API i) {
		super(n, i);
		set(DPI.DISPLAY_NAME, "&6Console");
		set(DPI.PM_COLOR, "&f");
	}
	
	private YamlConfiguration markkitYaml;
	
	public YamlConfiguration getMarkkit(){
		return markkitYaml;
	}
	
	public void setMarkkit(YamlConfiguration yaml){
		markkitYaml = yaml;
	}
	
	public void reloadMarkkit(){
		markkitYaml = YamlConfiguration.loadConfiguration(new File(DivinityManager.sysDir + "markkit.yml"));
	}
	
	public void saveMarkkit(){
		try {
			markkitYaml.save(new File(DivinityManager.sysDir + "markkit.yml"));
		} catch (Exception e){
			System.out.println("FAILED TO SAVE MARKKIT!");
		}
	}
	
	public void loadEffects(){
		if (contains("Effects")){
			for (String effectName : getConfigurationSection("Effects").getKeys(false)){
				String path = "Effects." + effectName + ".";
				String[] loc = getString(path + "Location").split(" ");
				cancelEffect(effectName);
				addEffect(effectName, ParticleEffect.fromName(getString(path + "Effect")), getInt(path + "OSX"), getInt(path + "OSY"), 
					getInt(path + "OSZ"), getInt(path + "Speed"), getInt(path + "Amount"), 
					new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3])), 
					getInt(path + "Range"), getLong(path + "Cycle"));
			}
		}
		if (contains("LetterEffects")){
			for (String effectName : getConfigurationSection("LetterEffects").getKeys(false)){
				String path = "LetterEffects." + effectName + ".";
				String[] loc = getString(path + "Location").split(" ");
				cancelEffect(effectName);
				addLetterEffect(effectName, ParticleEffect.fromName(getString(path + "Effect")),
				new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3])),
				Direction.getDirection(getString(path + "Direction")), getLong(path + "Cycle"));
			}
		}
	}
	
	public void addLetterEffect(String name, ParticleEffect eff, Location center, Direction dir, long cycleDelay){
		if (!contains("LetterEffects." + name)){
			String path = "LetterEffects." + name + ".";
			set(path + "Effect", eff.toString());
			set(path + "Location", center.getWorld().getName() + " " + center.getBlockX() + " " + center.getBlockY() + " " + center.getBlockZ());
			set(path + "Direction", dir.name());
			set(path + "Cycle", cycleDelay);
			Location l = new Location(center.getWorld(), center.getX(), center.getY(), center.getZ());
			api.repeat(this, "playLetterEffect", 0L, cycleDelay, "effects" + name, name, eff, l, dir);
		}
	}
	
	public void addEffect(String name, ParticleEffect eff, int offsetX, int offsetY, int offsetZ, int speed, int amount, Location center, int range, long cycleDelay){
		if (!contains("Effects." + name)){
			String path = "Effects." + name + ".";
			set(path + "OSX", offsetX);
			set(path + "OSY", offsetY);
			set(path + "OSZ", offsetZ);
			set(path + "Speed", speed);
			set(path + "Amount", amount);
			set(path + "Location", center.getWorld().getName() + " " + center.getBlockX() + " " + center.getBlockY() + " " + center.getBlockZ());
			set(path + "Range", range);
			set(path + "Cycle", cycleDelay);
			set(path + "Effect", eff.toString());
			api.repeat(this, "playEffect", 0L, cycleDelay, "effects" + name, eff, offsetX, offsetY, offsetZ, speed, amount, center, range);
		}
	}
	
	public void remEffect(String name){
		cancelEffect(name);
		set("Effects." + name, null);
		set("LetterEffects." + name, null);
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
}