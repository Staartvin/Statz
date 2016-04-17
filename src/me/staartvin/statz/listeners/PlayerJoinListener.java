package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.SQLiteTable;
import me.staartvin.statz.util.StatzUtil;

public class PlayerJoinListener implements Listener {

	private final Statz plugin;

	public PlayerJoinListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {

		// Get player
		final Player player = event.getPlayer();

		// Get table to read and write to.
		final SQLiteTable table = plugin.getSqlConnector().getSQLiteTable("joins");

		final Object currentStat = plugin.getSqlConnector().getObject(table, "value",
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString()));

		// Get current value of stat.
		int currentValue = 0;

		// Only cast if currentStat is not null (hence it has an entry)
		if (currentStat != null) {
			currentValue = (int) currentStat;
		}

		// Update value to new stat.
		plugin.getSqlConnector().setObjects(table,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + 1) + ""));

		// Also update name in database.
		plugin.getSqlConnector().setObjects(plugin.getSqlConnector().getSQLiteTable("players"),
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "playerName", player.getName()));
	}
}
