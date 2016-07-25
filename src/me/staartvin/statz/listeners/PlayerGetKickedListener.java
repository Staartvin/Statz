package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class PlayerGetKickedListener implements Listener {

	private final Statz plugin;

	public PlayerGetKickedListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKick(final PlayerKickEvent event) {

		final PlayerStat stat = PlayerStat.TIMES_KICKED;

		// Get player
		final Player player = event.getPlayer();

		// Do general check
		if (!plugin.doGeneralCheck(player))
			return;

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, StatzUtil.makeQuery("uuid",
				player.getUniqueId(), "value", 1, "world", player.getWorld().getName(), "reason", event.getReason()));

	}
}
