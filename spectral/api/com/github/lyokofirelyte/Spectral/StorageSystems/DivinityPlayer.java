package com.github.lyokofirelyte.Spectral.StorageSystems;

import org.bukkit.entity.Player;
import com.github.lyokofirelyte.Spectral.DataTypes.ElySkill;
import com.github.lyokofirelyte.Spectral.Public.ParticleEffect;

public interface DivinityPlayer extends DivinityStorage {
	
	/**
	 * @return The online status of the player
	 */
	public boolean isOnline();
	
	/**
	 * @return The player's skill level
	 */
	public int getLevel(ElySkill skill);
		
	/**
	 * @return The total amount of XP that the player has in the given skill
	 */
	public int getXP(ElySkill skill);
		
	/**
	 * @return The total amount of XP required to level up for the player in the given skill
	 */
	public int getNeededXP(ElySkill skill);
	
	/**
	 * @return A boolean for if the player has the supplied level in the supplied skill
	 */
	public boolean hasLevel(ElySkill skill, int level);

	/**
	 * Plays the supplied effect at the player's location with the given cycle delay.<br />
	 * Example: lockEffect("test", ParticleEffect.CRIT, 1, 1, 1, 0, 100, 16, 1); <br />
	 * The above example would display the crit effect with a circle of 1 radius and 100 particles.<br />
	 * The above example would have a visible reach of 16 blocks and repeat every tick.<br />
	 * The speed, which is 0 in the example, is how fast the dissapear. We use 0 for constant effects.<br />
	 * The name "test" is used in the remEffect() method to clear the effect.
	 */
	public void lockEffect(String name, ParticleEffect eff, int offsetX, int offsetY, int offsetZ, int speed, int amount, int range, long cycleDelay);
	
	/**
	 * Plays the supplied effect at the player's location one time.<br />
	 * Example: playEffect(ParticleEffect.CRIT, 1, 1, 1, 0, 100, 16, 1, p); <br />
	 * The above example would display the crit effect with a circle of 1 radius and 100 particles.<br />
	 * The above example would have a visible reach of 16 blocks.<br />
	 * The speed, which is 0 in the example, is how fast the dissapear. We use 0 for constant effects.<br />
	 */
	public void playEffect(ParticleEffect eff, int offsetX, int offsetY, int offsetZ, int speed, int amount, int range, Player p);
	
	/**
	 * Removes a locked effect from the player that we defined in lockEffect().<br />
	 * Example: remEffect("test");
	 */
	public void remEffect(String name);
	
	/**
	 * Removes ALL locked effects from the player - or does nothing in the event that none are active.
	 */
	public void clearEffects();
		
	/**
	 * Sends a message to the player with the pre-defined Elysian color-scheme. <br />
	 * Scheme is: &bElysian &7[snowflake] &b[message] <br />
	 * You can add colors by using the '&' symbol - they are converted automatically.
	 */
	public void s(String message);
		
	/**
	 * Sends a message to the player with the pre-defined Elysian-error color-scheme. <br />
	 * Scheme is: &bElysian &7[snowflake] &c&o[message] <br />
	 * You can add colors by using the '&' symbol - they are converted automatically.
	 */
	public void err(String message);
}