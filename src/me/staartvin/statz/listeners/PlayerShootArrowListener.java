package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class PlayerShootArrowListener implements Listener {

	private final Statz plugin;

	public PlayerShootArrowListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShootArrow(final EntityShootBowEvent event) {

		final PlayerStat stat = PlayerStat.ARROWS_SHOT;

		if (!(event.getEntity() instanceof Player)) {
			return; // It was not a player that shoot a bow
		}

		// Get player
		final Player player = (Player) event.getEntity();

		// Do general check
		if (!plugin.doGeneralCheck(player))
			return;

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, StatzUtil.makeQuery("uuid",
				player.getUniqueId(), "value", 1, "world", player.getWorld().getName(), "forceShot", event.getForce()));

	}
}
