package com.github.lyokofirelyte.Spectral.StorageSystems;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lyokofirelyte.Spectral.Public.Direction;
import com.github.lyokofirelyte.Spectral.Public.ParticleEffect;

public interface DivinitySystem extends DivinityStorage {

	/**
	 * @return The instance of the markkit configuration
	 */
	public YamlConfiguration getMarkkit();
	
	/**
	 * Change the yaml file associated with the markkit
	 */
	public void setMarkkit(YamlConfiguration yaml);
	
	/**
	 * Reloads the yaml file associated with the markkit
	 */
	public void reloadMarkkit();
	
	/**
	 * Saves the yaml file associated with the markkit.
	 */
	public void saveMarkkit();
	
	/**
	 * Loads all of the effects onto the system. <br />
	 * <b>This is called automatically on startup</b>
	 */
	public void loadEffects();
	
	/**
	 * Displays a letter formation from the string supplied with the supplied instructions. <br />
	 * Example: addLetterEffect("Hugs", ParticleEffect.CRIT, p.getLocation(), Direction.NORTH, 1); <br />
	 * The above example would spell "Hugs" with a CRIT effect at p's location and repeat every tick.
	 */
	public void addLetterEffect(String name, ParticleEffect eff, Location center, Direction dir, long cycleDelay);
	
	/**
	 * Adds a repeating effect to the server using the supplied arguments. <br />
	 * Example: addEffect("test", ParticleEffect.CRIT, 1, 1, 1, 0, 1000, p.getLocation(), 16, 1); <br />
	 * The above example would display the CRIT effect in a 1 radius around the player with 1000 particles per tick. <br />
	 * The above example would be seen from 16 blocks away. We will use "test" to remove the effect later.
	 */
	public void addEffect(String name, ParticleEffect eff, int offsetX, int offsetY, int offsetZ, int speed, int amount, Location center, int range, long cycleDelay);
	
	/**
	 * Removes an effect added with addEffect();
	 */
	public void remEffect(String name);
	
	/**
	 * Adds an effect to the server using the supplied arguments. <br />
	 * Example: addEffect("test", ParticleEffect.CRIT, 1, 1, 1, 0, 1000, p.getLocation(), 16); <br />
	 * The above example would display the CRIT effect in a 1 radius around the player with 1000 particles. <br />
	 * Unlike addEffect(), this method only displays the effect once and does not repeat.
	 */
	public void playEffect(ParticleEffect eff, int offsetX, int offsetY, int offsetZ, int speed, int amount, Location center, int range);
	
	/**
	 * Displays a letter formation from the string supplied with the supplied instructions. <br />
	 * Example: addLetterEffect("Hugs", ParticleEffect.CRIT, p.getLocation(), Direction.NORTH); <br />
	 * The above example would spell "Hugs" with a CRIT effect at p's location. <br />
	 * Unlike addLetterEffect(), this method only displays the letter once and does not repeat.
	 */
	public void playLetterEffect(String name, ParticleEffect eff, Location center, Direction dir);
	
	/**
	 * Cancels an effect that is playing but does not remove the effect from file. <br />
	 * The next startup will resume the effect.
	 */
	public void cancelEffect(String name);
}