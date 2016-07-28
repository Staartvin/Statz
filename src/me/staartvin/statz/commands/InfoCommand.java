package me.staartvin.statz.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;
import net.md_5.bungee.api.ChatColor;

public class InfoCommand extends StatzCommand {

	private final Statz plugin;

	public InfoCommand(final Statz instance) {
		this.setUsage("/statz info <stat> <player>");
		this.setDesc("Check a specific stat of a player.");
		this.setPermission("statz.info");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		String playerName = null;
		UUID uuid = null;
		String statType = null;

		boolean hasGivenPlayerName = false;

		// Command with statType AND player name
		if (args.length > 2) {
			playerName = args[2];
			statType = args[1];

			hasGivenPlayerName = true;
		} else if (args.length > 1) {
			// Provided a stat but no name
			hasGivenPlayerName = false;
			statType = args[1];

		} else {
			sender.sendMessage(ChatColor.RED + "You did not provide a stat type!");
			return true;
		}

		if (!hasGivenPlayerName) {
			// No name was given, so use player's own name

			// Only players are allowed to check themselves.
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be performed by players!");
				return true;
			}

			Player player = (Player) sender;

			playerName = player.getName();

			uuid = player.getUniqueId();
		} else {
			@SuppressWarnings("deprecation")
			OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(playerName);

			if (!targetPlayer.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.RED + playerName + " has never played on this server before!");
				return true;
			}

			if (targetPlayer.isOnline()) {
				Player player = (Player) targetPlayer.getPlayer();

				playerName = player.getName();
				uuid = player.getUniqueId();
			} else {
				playerName = targetPlayer.getName();
				uuid = targetPlayer.getUniqueId();
			}

			if (playerName == null || uuid == null) {
				sender.sendMessage(ChatColor.RED + "Could not find player!");
				return true;
			}
		}

		PlayerStat stat = null;

		for (PlayerStat s : PlayerStat.values()) {
			if (s.toString().equalsIgnoreCase(statType)) {
				stat = s;
				break;
			}
		}

		if (stat == null) {
			sender.sendMessage(ChatColor.RED + statType + " is not a correct stat!");
			return true;
		}

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			private String playerName;
			private UUID uuid;
			private PlayerStat statType;

			private Runnable init(String playerName, UUID uuid, PlayerStat statType) {
				this.playerName = playerName;
				this.uuid = uuid;
				this.statType = statType;
				return this;
			}

			public void run() {

				List<String> messages = new ArrayList<>();

				PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

				sender.sendMessage(ChatColor.YELLOW + "---- [Stat " + statType + " of " + playerName + "] ----");

				// Only use valid info.
				if (!info.isValid() || statType == PlayerStat.PLAYERS) {
					sender.sendMessage(ChatColor.RED + "There is nothing to show for this stat.");
					return;
				}

				for (Query query : info.getResults()) {
					// Invalid query
					if (query == null)
						continue;

					messages.add(StatzUtil.getInfoString(query, statType, playerName));
				}

				for (String message : messages) {
					sender.sendMessage(message);
				}
			}
		}.init(playerName, uuid, stat));

		return true;
	}

	/* (non-Javadoc)
	 * @see me.staartvin.statz.commands.manager.StatzCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
			final String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
}
