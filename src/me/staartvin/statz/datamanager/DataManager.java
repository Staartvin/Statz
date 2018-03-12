package me.staartvin.statz.datamanager;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.language.DescriptionMatcher;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * This class handles all requests for data of a player. Whenever you want to obtain data about a player (or update
 * data about a player), you will need to use this manager.
 * <br>
 * <br>
 * <h2>Requesting data</h2>
 * When you request data about a player, this manager will ask the caching manager for data about this player. If
 * there is no cached data yet, you'll need to load the user in the cache first. Note that retrieving data from the
 * database should be done asynchronously, as it blocks the thread it's working on.
 * <br>
 * <br>
 * <h2>Updating data</h2>
 * To update a player's data, you can use the {@link #setPlayerInfo(UUID, PlayerStat, Query)} method. Note that the
 * update will be cached immediately and will be sent to the database after a while. Whenever you update the player's
 * data, the changes will immediately appear in the cache.
 *
 */
public class DataManager {

	private final Statz plugin;

	public DataManager(final Statz instance) {
		plugin = instance;
	}

	/**
     * This method will obtain all data that is known about a player for a given statistic. Note that this data is
     * cached for performance reasons. When a player is not loaded into the cache, the method will return null. The
     * player should first be loaded into the cache.
	 * @param uuid UUID of the player to search for
     * @param statType Type of stat to get the data of
	 * @throws IllegalArgumentException if the given uuid is null
	 * @return a {@link PlayerInfo} class that contains the data of a player or null if no the player was not loaded
	 * in the cache yet.
	 */
	public PlayerInfo getPlayerInfo(final UUID uuid, final PlayerStat statType) throws IllegalArgumentException {

		if (uuid == null) {
			throw new IllegalArgumentException("UUID cannot be null.");
		}

        if (!this.isPlayerLoaded(uuid)) {
			return null;
		}

		return plugin.getCachingManager().getCachedPlayerData(uuid);
	}

	/**
	 * Get Player info like {@link #getPlayerInfo(UUID, PlayerStat)}, but check for additional conditions.
	 * Let's say you want to get all the player info for a player on world 'world'. You would call this method with the player's UUID, 
	 * provide the statType and add a Query condition with StatzUtil.makeQuery().
	 * @param uuid UUID of the player
	 * @param statType Type of stat to get player info of.
	 * @param requirements Extra conditions that need to apply. See {@link RowRequirement}.
	 * @return a {@link PlayerInfo} object.
	 */
	public PlayerInfo getPlayerInfo(final UUID uuid, final PlayerStat statType, RowRequirement... requirements) {
		PlayerInfo info = this.getPlayerInfo(uuid, statType);

		// There are no requirement, so we don't need to check any data.
		if (requirements == null || requirements.length == 0) {
			return info;
		}

		for (Iterator<Query> it = info.getDataOfPlayerStat(statType).iterator(); it.hasNext(); ) {
			Query query = it.next();

			// Remove query if it does not meet the given requirements.
			if (!query.meetsAllRequirements(Arrays.asList(requirements))) {
				it.remove();
			}
		}

		return info;
	}

	/**
     * Check whether there is cached data of a player. If not, the player should first be loaded before trying to
     * obtain data. Note that loading player data is asynchronous!
     * @param uuid UUID of the player
     * @return true if there is cached data about a player, false otherwise.
     */
    public boolean isPlayerLoaded(UUID uuid) {
        return plugin.getCachingManager().isPlayerCacheLoaded(uuid);
    }

    /**
     * Update a player's data with a given query. Note that it may take a while before the data actually reaches the
     * database (due to the pooling system). Passing a query with new data means it will be added to the already
     * existing value of the data, e.g. you cannot overwrite the data, merely add to it. Hence, updates should be
     * relative, not absolute.
     * @param uuid UUID of the player
     * @param statType Type of statistic the given query belongs to
     * @param updateQuery Query that contains updated data.
	 */
    public void setPlayerInfo(final UUID uuid, final PlayerStat statType, Query updateQuery) {

		// If the query does not have a UUID, add it in manually.
        if (!updateQuery.hasColumn("uuid")) {
            updateQuery.setValue("uuid", uuid);
		}

		// Add query to pool of updates
        plugin.getUpdatePoolManager().registerNewUpdateQuery(updateQuery, statType, uuid);
	}
	
	public void sendStatisticsList(CommandSender sender, String playerName, UUID uuid, int pageNumber, List<PlayerStat> list) {
		List<String> messages = new ArrayList<>();
		List<TextComponent> messagesSpigot = new ArrayList<>();

		for (PlayerStat statType : list) {
			PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

			// Only use valid info.
			if (!info.isValid() || statType == PlayerStat.PLAYERS)
				continue;

            String messageString = DescriptionMatcher.getTotalDescription(info, statType);

            if (sender instanceof Player && plugin.getServer().getVersion().toLowerCase().contains("spigot")) {
                TextComponent spigotMessage = new TextComponent(messageString);

                spigotMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/statz list " + playerName + " " + statType.toString()));
                spigotMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click on me for more info about ")
                                .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

                messagesSpigot.add(spigotMessage);
            } else {
                messages.add(messageString);
			}
		}

		int messagesPerPage = 8;

		int pages = 0;

		if (sender instanceof Player) {
			pages = (int) Math.ceil(messagesSpigot.size() / (double) messagesPerPage);
		} else {
			pages = (int) Math.ceil(messages.size() / (double) messagesPerPage);
		}

		if (pageNumber > (pages - 1) || pageNumber < 0) {
			pageNumber = 0;
		}

		sender.sendMessage(
				ChatColor.YELLOW + "---------------- [Statz of " + playerName + "] ----------------");
		for (int j = 0; j < messagesPerPage; j++) {
			int index = (pageNumber == 0 ? j : (pageNumber * messagesPerPage) + j);

			// Don't try to get other messages, as there are no others.
			if (sender instanceof Player) {
				if (index >= messagesSpigot.size()) {
					break;
				}
			} else {
				if (index >= messages.size()) {
					break;
				}
			}

			if (sender instanceof Player) {
				Player p = (Player) sender;

				p.spigot().sendMessage(messagesSpigot.get(index));
			} else {
				sender.sendMessage(messages.get(index));
			}

		}

		// Create page clicker
        BaseComponent[] pageClicker = new ComponentBuilder("<<< ").color(net.md_5.bungee.api.ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
						"/statz list " + playerName + " " + (pageNumber < 0 ? 0 : pageNumber)))
                .append("Page ").color(net.md_5.bungee.api.ChatColor.DARK_AQUA).append(pageNumber + 1 + "").color(net.md_5.bungee.api.ChatColor.GREEN)
                .append(" of " + pages).color(net.md_5.bungee.api.ChatColor.DARK_AQUA).append(" >>>").color(net.md_5.bungee.api.ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
						"/statz list " + playerName + " " + (pageNumber + 2 > pages ? pages
								: pageNumber + 2)))
				.create();

		if (sender instanceof Player) {
			Player p = (Player) sender;

			p.spigot().sendMessage(pageClicker);
		} else {
			sender.sendMessage(ChatColor.GOLD + "<<< " + ChatColor.DARK_AQUA + "Page " + ChatColor.GREEN
					+ (pageNumber + 1) + ChatColor.DARK_AQUA + " of " + pages + ChatColor.GOLD + " >>>");
		}
	}
}
