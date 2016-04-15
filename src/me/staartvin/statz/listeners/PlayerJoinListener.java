package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.SQLiteTable;
import me.staartvin.statz.util.StatzUtil;

public class PlayerJoinListener implements Listener {

	private Statz plugin;

	public PlayerJoinListener(Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		// Get player
		Player player = event.getPlayer();

		// Get table to read and write to.
		SQLiteTable table = plugin.getSqlConnector().getSQLiteTable("joins");

		Object currentStat = plugin.getSqlConnector().getObject(table, "value",
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
