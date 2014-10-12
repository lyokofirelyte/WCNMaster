package com.github.lyokofirelyte.Spectral.Events;

import org.bukkit.event.Cancellable;

public interface DivinityEventHandler extends Cancellable {

	/**
	 * Returns the state of the event. If cancelled, the actual in-game event won't happen, but the code will. <br />
	 * Example: Breaking blocks will be cancelled, but it will still pass to other events. <br />
	 * Place the annotation @EventHandler(ignoreCancelled = true) to not even read events that are already cancelled.
	 */
	public boolean isCancelled();
	
	/**
	 * Set the state of the event. If cancelled, the actual in-game event won't happen, but the code will.
	 */
	public void setCancelled(boolean cancel);
}