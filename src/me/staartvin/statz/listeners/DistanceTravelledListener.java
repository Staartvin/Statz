package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class DistanceTravelledListener implements Listener {

	private final Statz plugin;

	public DistanceTravelledListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(final PlayerMoveEvent event) {

		final PlayerStat stat = PlayerStat.DISTANCE_TRAVELLED;

		// Get player
		final Player player = (Player) event.getPlayer();

		// Do general check
		if (!plugin.doGeneralCheck(player, stat))
			return;

		String movementType = StatzUtil.getMovementType(player);

		final double distTravelled;

		try {
			distTravelled = event.getFrom().distance(event.getTo());
		} catch (IllegalArgumentException e) {
			// Did not move correctly, so ignore it.
			return;
		}

		if (distTravelled == 0) {
			return;
		}

		final String movement = movementType;

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (distTravelled), "moveType",
						movement, "world", player.getWorld().getName()));

	}
}
