package me.staartvin.statz.datamanager;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.language.DescriptionMatcher;
import me.staartvin.statz.util.StatzUtil;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * This class handles all incoming data queries from other plugins (and from
 * internal calls).
 * <br>
 * Getting info of a player should be done here.
 * <p>
 * Date created: 15:03:12
 * 17 apr. 2016
 * 
 * @author "Staartvin"
 *
 */
public class DataManager {

	private final Statz plugin;

	public DataManager(final Statz instance) {
		plugin = instance;
	}

	/**
	 * This method will obtain all rows that are in the database table of the specific stat type. 
	 * It will only give the rows of the given UUID. Since Statz uses a pool manager, it will obtain the data from the database
	 * (which could be outdated if no update has occured yet) and it will match this data with the current queries in the pool.
	 * When the queries in the pool are more up to date, it will override the outdated results of the database with the new data
	 * from the queries in the pool.
	 * 
	 * <br>
	 * <br>An extra safety mechanism was added that prevents data from accidentally overriding old data. Every x seconds, the queries
	 * from the pool are executed on the database. However, this takes some small time (somewhere in milliseconds). When the query is executed,
	 * it is removed from the pool to prevent it from executing again. However, when getPlayerInfo() is called at the same time when the query is
	 * deleted, Statz will give back data from the database, since the pool is empty. The database is not yet updated and so the wrong data is
	 * returned.
	 * <br>
	 * <br>This issue is solved by saving the last written actions (see {@link DataPoolManager#getLatestQueries(PlayerStat)}). This method returns the
	 * last performed queries on the database. {@link #getPlayerInfo(UUID, PlayerStat)} will try use this info (if it is available) whenever it notices 
	 * that we are performing a database save. In this way, this method will ensure you'll always get the most recent info. 
	 * @param uuid UUID of the player to search for
	 * @param statType Type of stat to get the data of.
	 * @return a {@link PlayerInfo} class that contains the results of the performed action on the database.
	 */
	public PlayerInfo getPlayerInfo(final UUID uuid, final PlayerStat statType) {
		final PlayerInfo info = new PlayerInfo(uuid);

		// Get results from database
		List<Query> results = plugin.getDatabaseConnector().getObjects(statType.getTableName(),
				StatzUtil.makeQuery("uuid", uuid.toString()));

		// Get a list of queries currently in the pool
		List<Query> pooledQueries = plugin.getDataPoolManager().getStoredQueries(statType);

		// If we have queries in the pool, check for conflicting ones.
		if (pooledQueries != null && !pooledQueries.isEmpty()) {

			// There ARE stored queries and since the pool is more up to date, we have to override the old ones.
			for (Query pooledQuery : pooledQueries) {
				// If UUID of query in the pool is not matching with uuid of player, don't add it.
				if (!pooledQuery.getValue("uuid").toString().equalsIgnoreCase(uuid.toString())) {
					continue;
				}

				// There is no data of this stat in the database, so storedQueries are always more up to date. (IF the UUIDs match)
				if (results == null || results.isEmpty()) {
					results.add(pooledQuery);
					continue;
				}

				// Get the queries of the pool that conflict with the 'old' database results.
				List<Query> conflictingQueries = pooledQuery.findConflicts(results);

				// No conflicts found, yeah!!
				if (conflictingQueries == null || conflictingQueries.isEmpty()) {
					results.add(pooledQuery);
					continue;
				}

				// We found conflicting queries.
				for (Query conflictingQuery : conflictingQueries) {
					// Remove old data from results and add new (more updated data) to the results pool.
					conflictingQuery.addValue("value", pooledQuery.getValue());
					
				}

			}

		} else {
			// No queries in the pool
		}

		// Result is not null, so this is a valid player info.
		if (results != null && !results.isEmpty()) {
			info.setValid(true);

			info.setResults(results);
		}

		return info;
	}

	/**
	 * Get Player info like {@link #getPlayerInfo(UUID, PlayerStat)}, but check for additional conditions.
	 * Let's say you want to get all the player info for a player on world 'world'. You would call this method with the player's UUID, 
	 * provide the statType and add a Query condition with StatzUtil.makeQuery().
	 * @param uuid UUID of the player
	 * @param statType Type of stat to get player info of.
	 * @param conditions Extra conditions that need to apply.
	 * @return a {@link PlayerInfo} object.
	 */
	public PlayerInfo getPlayerInfo(final UUID uuid, final PlayerStat statType, Query conditions) {
		PlayerInfo info = this.getPlayerInfo(uuid, statType);

		if (info.isValid()) {
			List<Query> deletedQueries = new ArrayList<>();

            for (Query map : info.getDataOfPlayerStat(statType)) {
				for (Entry<String, String> entry : conditions.getEntrySet()) {
                    if (!map.hasColumn(entry.getKey())) {
						deletedQueries.add(map);
						break;
					}

					if (!map.getValue(entry.getKey()).equals(entry.getValue())) {
						deletedQueries.add(map);
						break;
					}
				}
			}

			// Remove queries that are not relevant.
			for (Query q : deletedQueries) {
				info.removeResult(q);
			}
		}

		return info;
	}

	/**
	 * Set data of a player into the database. The query parameter can be build by using {@link me.staartvin.statz.util.StatzUtil#makeQuery(Object...)}
	 * @param uuid
	 * @param statType
	 * @param results
	 */
	public void setPlayerInfo(final UUID uuid, final PlayerStat statType, Query results) {

		// If the query does not have a UUID, add it in manually.
        if (!results.hasColumn("uuid")) {
			results.setValue("uuid", uuid);
		}
		
		// Add query to the pool.
		plugin.getDataPoolManager().addQuery(statType, results);

        PlayerInfo info = new PlayerInfo(uuid);

        //info.addRow(results);

        plugin.getCachingManager().registerCachedData(uuid, info);
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
