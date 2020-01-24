package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.CommandsPerformedSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

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
		if (!plugin.doGeneralCheck(player, stat))
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

		PlayerStatSpecification specification = new CommandsPerformedSpecification(player.getUniqueId(), 1,
				player.getWorld().getName(), command, arguments);

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

	}
}
