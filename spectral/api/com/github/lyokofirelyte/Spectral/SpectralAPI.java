package com.github.lyokofirelyte.Spectral;

import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityAlliance;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityGame;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityPlayer;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRegion;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinityRing;
import com.github.lyokofirelyte.Spectral.StorageSystems.DivinitySystem;

public interface SpectralAPI {
	
	/**
	 * @return Retreive a registered class instance that was registered using the registerAll()
	 */
	public Object getInstance(Class<?> clazz);

	/**
	 * @return The DivinityPlayer from a String, Player, or UUID object.<br />
	 * This also accepts partial names.
	 */
	public DivinityPlayer getDivPlayer(Object o);
	
	/**
	 * @return The alliance with the name specified. Returns null if it does not exist.
	 */
	public DivinityAlliance getDivAlliance(String name);
	
	/**
	 * @return The divinity game in the folder with the given name
	 */
	public DivinityGame getDivGame(String folder, String name);
	
	/**
	 * @return The divinity region with the specified name
	 */
	public DivinityRegion getDivRegion(String region);
	
	/**
	 * @return The instance of the DivinitySystem, which includes other settings such as the markkit.
	 */
	public DivinitySystem getDivSystem();
	
	/**
	 * @return The ring with the name specified.
	 */
	public DivinityRing getDivRing(String name);
	
	/**
	 * Returns the Bukkit player object for the partial name supplied
	 */
	public Player getPlayer(String name);
	
	/**
	 * @return A collection of all of the current registered DivinityPlayerModules, in storage form
	 */
	public Collection<?> getAllPlayers();
	
	/**
	 * Schedules a task for later using the Bukkit Scheduler. <br />
	 * Example: schedule(this, "someMethod", 200L, "someTaskName", p); <br />
	 * The above example would schedule the method someMethod(Player p) to go off in 200 ticks in the current class
	 */
	public void schedule(Object clazz, String method, long delay, String taskName, Object... args);
	
	/**
	 * Schedules a repeating task for later using the Bukkit Scheduler. <br />
	 * Example: repeat(this, "someMethod", 200L, 5L, "someTaskName", p); <br />
	 * The above example would schedule the method someMethod(Player p) to go off in 200 ticks in the current class and repeat every 5 seconds <br />
	 * To cancel the task, use cancelTask("someTaskName");
	 */
	public void repeat(Object clazz, String method, long delay, long period, String taskName, Object... args);
	
	/**
	 * Cancels the given task
	 */
	public void cancelTask(String name);
	
	/**
	 * Registers all of the objects provided - all of which should have one or more @DivCommand annotations
	 */
	public void registerCommands(Object... o);
	
	/**
	 * Registers all of the provided listeners for the provided plugin
	 */
	public void registerListeners(Plugin plugin, List<Listener> o);
	
	/**
	 * @return The properly formatted color codes from the '&' symbol input <br />
	 * Example: AS("&7Grey Text"); will actually make it grey
	 */
	public String AS(String s);
	
	/**
	 * Check for permissions. If you supply "false" for silent, it will alert the player on failure.
	 * @return A boolean determining if the command sender has the selected permission
	 */
	public boolean perms(CommandSender cs, String perm, boolean silent);
	
	/**
	 * @return The online status of the specified partial player
	 */
	public boolean isOnline(String p);
	
	/**
	 * @return A boolean determining if the supplied region exists or not
	 */
	public boolean doesRegionExist(String region);
	
	/**
	 * @return A boolean determining if a partial match can be made for the string provided
	 */
	public boolean doesPartialPlayerExist(String player);
	
	/**
	 * @return A boolean determining if the supplied ring exists or not
	 */
	public boolean doesRingExist(String ring);
	
	/**
	 * Fires the specified event and passes to all plugins
	 */
	public void event(Event e);
	
	/**
	 * Calls the startup load method
	 */
	public void loadAllFiles(boolean full);
	
	/**
	 * Calls the shutdown save method
	 */
	public void saveAllFiles();
	
	/**
	 * Performs a system backup on all files
	 */
	public void backup();
	
	/**
	 * <b>This only works for classes that have the constructor someClass(Elysian i){}</b> <br />
	 * Registers all of the applicable classes within Elysian <br />
	 * Registration includes DivCommands, Listeners, AutoSaves, DivGames, and storing in a map for later use of the object <br />
	 * If you need to register a class that does not use the constructor someClass(Elysian i){}, you can make a "parent" class <br >
	 * that does, implement DivGame, and use the registerSubClasses() method, or simply use api.registerCommands(..) and api.registerListeners(..) <br />
	 * Please note that if you do not use registerSubClasses() or this method, your classes won't be saved in the global hashmap of instances
	 * @param mainClassInstanceOfElysian ex: main
	 */
	public void registerAll(Object mainClassInstanceOfElysian);
}