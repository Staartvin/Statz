package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

	private final Statz plugin;

	public QuitListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(final PlayerQuitEvent event) {

		// Get player
		final Player player = event.getPlayer();

		// Check if player already has a checker running.
		if (JoinsListener.updateID.containsKey(player.getUniqueId())) {
			// Cancel task of player
			plugin.getServer().getScheduler().cancelTask(JoinsListener.updateID.get(player.getUniqueId()));

			JoinsListener.updateID.remove(player.getUniqueId());
		}

		// Stop task of periodically refreshing cache of a player, as they logged out.
		plugin.getTaskManager().stopUpdatePlayerCacheTask(player.getUniqueId());
	}

}
