package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class BucketsEmptiedListener implements Listener {

	private final Statz plugin;

	public BucketsEmptiedListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBucketEmpty(final PlayerBucketEmptyEvent event) {

		final PlayerStat stat = PlayerStat.BUCKETS_EMPTIED;

		// Get player
		final Player player = event.getPlayer();

		// Do general check
		if (!plugin.doGeneralCheck(player, stat))
			return;

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, StatzUtil.makeQuery("uuid",
				player.getUniqueId().toString(), "value", 1, "world", player.getWorld().getName()));

	}
}
