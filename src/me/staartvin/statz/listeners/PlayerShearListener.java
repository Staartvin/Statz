package me.staartvin.statz.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class PlayerShearListener implements Listener {

	private final Statz plugin;

	public PlayerShearListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDamage(final PlayerShearEntityEvent event) {

		final PlayerStat stat = PlayerStat.TIMES_SHORN;

		// Get player
		final Player player = (Player) event.getPlayer();

		// Get player info.
		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

		// Get current value of stat.
		int currentValue = 0;

		// Check if it is valid!
		if (info.isValid()) {
			for (HashMap<String, Object> map : info.getResults()) {
				if (map.get("world") != null
						&& map.get("world").toString().equalsIgnoreCase(player.getWorld().getName())) {
					currentValue += Integer.parseInt(map.get("value").toString());
				}
			}
		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, StatzUtil.makeQuery("uuid",
				player.getUniqueId().toString(), "value", (currentValue + 1), "world", player.getWorld().getName()));

	}
}
