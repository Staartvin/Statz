package me.staartvin.statz.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.language.Lang;
import me.staartvin.statz.util.StatzUtil;
import net.md_5.bungee.api.ChatColor;

public class ListCommand extends StatzCommand {

	private final Statz plugin;

	public ListCommand(final Statz instance) {
		this.setUsage("/statz list <player> (stat name) (page number)");
		this.setDesc("Check the stats of a player.");
		this.setPermission("statz.list");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		String playerName = null;
		String statType = null;
		UUID uuid = null;
		int pageNumber = 1;

		boolean hasGivenPlayerName = false;

		// If this is true, a list of stats will be shown, otherwise we'll show a specific stat.
		boolean showList = true;

		if (args.length >= 3) {
			// [0] = 'list', [1] = 'player name', [2] = 'stat name', [3] = 'page number'

			playerName = args[1];
			hasGivenPlayerName = true;

			// Page number not a stat type
			if (StringUtils.isNumeric(args[2])) {
				try {
					pageNumber = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					// Third argument was not a number, so give an error
					sender.sendMessage(Lang.INCORRECT_PAGE_NUMBER.getConfigValue());
					return true;
				}
			} else {
				statType = args[2];
				showList = false;
			}

		} else if (args.length == 2) {
			// [0] = 'list', [1] = 'player name'

			// If [1] is a number, the input was a page number, not a name
			if (StringUtils.isNumeric(args[1])) {
				try {
					pageNumber = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					// [1] argument was not a number, so give an error
					sender.sendMessage(Lang.INCORRECT_PAGE_NUMBER.getConfigValue());
					return true;
				}
			} else {
				// [1] is a name
				playerName = args[1];
				hasGivenPlayerName = true;
			}
		} else /*if (args.length == 1)*/ {
			// No player name given and no page number
			// Default to page number = 1
			hasGivenPlayerName = false;
		}

		if (!hasGivenPlayerName) {
			// No name was given, so use player's own name

			// Only players are allowed to check themselves.
			if (!(sender instanceof Player)) {
				sender.sendMessage(Lang.COMMAND_PERFORMED_ONLY_PLAYERS.getConfigValue());
				return true;
			}

			Player player = (Player) sender;

			playerName = player.getName();

			uuid = player.getUniqueId();
		} else {
			@SuppressWarnings("deprecation")
			OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(playerName);

			if (!targetPlayer.hasPlayedBefore()) {
				sender.sendMessage(Lang.PLAYER_NEVER_PLAYED_BEFORE.getConfigValue(playerName));
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

		if (showList) {
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

				private String playerName;
				private UUID uuid;
				private int pageNumber;

				private Runnable init(String playerName, UUID uuid, int pageNumber) {
					this.playerName = playerName;
					this.uuid = uuid;
					this.pageNumber = pageNumber - 1;
					return this;
				}

				public void run() {

					plugin.getDataManager().sendStatisticsList(sender, playerName, uuid, pageNumber, Arrays.asList(PlayerStat.values()));

				}
			}.init(playerName, uuid, pageNumber));
		} else {
			// Show specific stat
			PlayerStat stat = null;

			for (PlayerStat s : PlayerStat.values()) {
				if (s.toString().equalsIgnoreCase(statType)) {
					stat = s;
					break;
				}
			}

			if (stat == null) {
				sender.sendMessage(Lang.INCORRECT_STAT_TYPE.getConfigValue(statType));
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

					sender.sendMessage(Lang.SPECIFIC_STAT_HEADER.getConfigValue(statType, playerName));

					// Only use valid info.
					if (!info.isValid() || statType == PlayerStat.PLAYERS) {
						sender.sendMessage(Lang.NO_STATISTICS_TO_SHOW.getConfigValue());
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

		}

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
