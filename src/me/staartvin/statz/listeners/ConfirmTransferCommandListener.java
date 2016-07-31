package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.TransferCommand;
import net.md_5.bungee.api.ChatColor;

public class ConfirmTransferCommandListener implements Listener {

	@SuppressWarnings("unused")
	private final Statz plugin;

	public ConfirmTransferCommandListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAsyncChat(final AsyncPlayerChatEvent event) {

		Player player = event.getPlayer();

		if (TransferCommand.confirmTransferSQLite.contains(player.getName())) {
			// Player did not type yes.
			if (!event.getMessage().trim().equalsIgnoreCase("yes")) {
				player.sendMessage(ChatColor.RED + "To confirm, type " + ChatColor.GOLD + "yes" + ChatColor.RED
						+ ". To deny, just wait 10 seconds.");
				event.setCancelled(true);
				return;
			}

			player.sendMessage(
					ChatColor.RED + "Confirmed " + ChatColor.GRAY + "/statz transfer" + ChatColor.RED + " command!");

			TransferCommand.confirmTransfer(player);

			// Cancel type of 'yes'
			event.setCancelled(true);
		} else if (TransferCommand.confirmTransferMySQL.contains(player.getName())) {
			// Player did not type yes.
			if (!event.getMessage().trim().equalsIgnoreCase("yes")) {
				player.sendMessage(ChatColor.RED + "To confirm, type " + ChatColor.GOLD + "yes" + ChatColor.RED
						+ ". To deny, just wait 10 seconds.");
				event.setCancelled(true);
				return;
			}

			player.sendMessage(
					ChatColor.RED + "Confirmed " + ChatColor.GRAY + "/statz transfer reverse" + ChatColor.RED + " command!");

			TransferCommand.confirmReverseTransfer(player);

			// Cancel type of 'yes'
			event.setCancelled(true);
		}

	}
}
