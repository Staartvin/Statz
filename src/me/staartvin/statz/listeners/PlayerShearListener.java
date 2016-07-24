package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class PlayerShearListener implements Listener {

	private final Statz plugin;

	public PlayerShearListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShear(final PlayerShearEntityEvent event) {

		final PlayerStat stat = PlayerStat.TIMES_SHORN;

		// Get player
		final Player player = (Player) event.getPlayer();
		
		// Do general check
				if (!plugin.doGeneralCheck(player)) return;

		//		// Get current value of stat.
		//		int currentValue = 0;
		//
		//		// Get player info.
		//		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat,
		//				StatzUtil.makeQuery("world", player.getWorld().getName()));
		//
		//		// Check if it is valid!
		//		if (info.isValid()) {
		//			currentValue += info.getTotalValue();
		//		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, StatzUtil.makeQuery("uuid",
				player.getUniqueId().toString(), "value", 1, "world", player.getWorld().getName()));

	}
}
