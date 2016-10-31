package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class CommandsPerformedListener implements Listener {

	private final Statz plugin;

	public CommandsPerformedListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPerformCommand(final PlayerCommandPreprocessEvent event) {

		final PlayerStat stat = PlayerStat.COMMANDS_PERFORMED;

		// Get player
		final Player player = event.getPlayer();

		// Do general check
		if (!plugin.doGeneralCheck(player))
			return;
		
		String message = event.getMessage();
		
		int subString = message.indexOf(" ");
		
		String command = "";
		String arguments = "";
		
		if (subString > 0) {
			command = message.substring(0, subString).trim();
			arguments = message.substring(subString).trim();
		} else {
			command = message.trim();
		}
		
		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, StatzUtil.makeQuery("uuid",
				player.getUniqueId(), "value", 1, "world", player.getWorld().getName(), "command", command, "arguments", arguments));

	}
}
