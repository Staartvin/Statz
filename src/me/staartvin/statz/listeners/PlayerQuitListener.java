package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.staartvin.statz.Statz;

public class PlayerQuitListener implements Listener {

	private final Statz plugin;

	public PlayerQuitListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(final PlayerQuitEvent event) {

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			public void run() {

				// Get player
				final Player player = event.getPlayer();

				// Check if player already has a checker running.
				if (PlayerJoinListener.updateID.containsKey(player.getUniqueId())) {
					// Cancel task of player
					plugin.getServer().getScheduler().cancelTask(PlayerJoinListener.updateID.get(player.getUniqueId()));

					PlayerJoinListener.updateID.remove(player.getUniqueId());
				}
			}

		});
	}
	
	
}
