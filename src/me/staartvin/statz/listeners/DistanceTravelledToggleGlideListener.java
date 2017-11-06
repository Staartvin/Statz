package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class DistanceTravelledToggleGlideListener implements Listener {

	@SuppressWarnings("unused")
	private final Statz plugin;

	public DistanceTravelledToggleGlideListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(final EntityToggleGlideEvent event) {

		if (!(event.getEntity() instanceof Player)) return;
		
		// Get player
		final Player player = (Player) event.getEntity();

		StatzUtil.isGliding.put(player.getUniqueId(), event.isGliding());
	}
}
