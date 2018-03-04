package me.staartvin.statz.datamanager;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;
import me.staartvin.statz.util.StatzUtil.Time;
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
		//System.out.println("----------------------");
		final PlayerInfo info = new PlayerInfo(uuid);

		// Get results from database
		List<Query> results = plugin.getDatabaseConnector().getObjects(statType.getTableName(),
				StatzUtil.makeQuery("uuid", uuid.toString()));

//		System.out.println("--------------");
//		System.out.println("Table: " + statType);
//		
//		for (Query map : results) {
//			System.out.println("RESULT: " + map);
//		}

		// Get a list of queries currently in the pool
		List<Query> pooledQueries = plugin.getDataPoolManager().getStoredQueries(statType);

		// If we have queries in the pool, check for conflicting ones.
		if (pooledQueries != null && !pooledQueries.isEmpty()) {

			//			for (HashMap<String, String> store : storedQueries) {
			//				System.out.println("STORED: "  + store);
			//			}

			// There ARE stored queries and since the pool is more up to date, we have to override the old ones.
			for (Query pooledQuery : pooledQueries) {
				// If UUID of query in the pool is not matching with uuid of player, don't add it.
				if (!pooledQuery.getValue("uuid").toString().equalsIgnoreCase(uuid.toString())) {
					continue;
				}

				// There is no data of this stat in the database, so storedQueries are always more up to date. (IF the UUIDs match)
				if (results == null || results.isEmpty()) {
					//					System.out.println("Stored query " + StatzUtil.printQuery(storedQuery)
					//							+ " was more up to date since there is no record in database");
					results.add(pooledQuery);
					continue;
				}

				// Get the queries of the pool that conflict with the 'old' database results.
				List<Query> conflictingQueries = pooledQuery.findConflicts(results);

				// No conflicts found, yeah!!
				if (conflictingQueries == null || conflictingQueries.isEmpty()) {
					//					System.out.println(
					//							"No conflicts found between " + StatzUtil.printQuery(storedQuery) + " and " + results);
					results.add(pooledQuery);
					continue;
				}

				// We found conflicting queries.
				for (Query conflictingQuery : conflictingQueries) {
					//System.out.println("Stored query " + pooledQuery + " conflicts with " + conflictingQuery);
					// Remove old data from results and add new (more updated data) to the results pool.
					//results.remove(conflictingQuery);
					//results.add(pooledQuery);
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
		//		
		//				for (Query map : results) {
		//					System.out.println("END RESULT: " + map);
		//				}

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

			for (Query map : info.getResults()) {
				for (Entry<String, String> entry : conditions.getEntrySet()) {
					if (!map.hasKey(entry.getKey())) {
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
		if (!results.hasKey("uuid")) {
			results.setValue("uuid", uuid);
		}
		
		// Add query to the pool.
		plugin.getDataPoolManager().addQuery(statType, results);
	}
	
	public void sendStatisticsList(CommandSender sender, String playerName, UUID uuid, int pageNumber, List<PlayerStat> list) {
		List<String> messages = new ArrayList<>();
		List<TextComponent> messagesSpigot = new ArrayList<>();

		for (PlayerStat statType : list) {
			PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

			// Only use valid info.
			if (!info.isValid() || statType == PlayerStat.PLAYERS)
				continue;

			switch (statType) {
				case JOINS:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has joined ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" times");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has joined " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
					}

					break;
				case DEATHS:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has died ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" times");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has died " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
					}

					break;
				case ITEMS_CAUGHT:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has caught ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" items while fishing");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has caught " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items while fishing");
					}

					break;
				case BLOCKS_PLACED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has placed ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" blocks");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has placed " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " blocks");
					}

					break;
				case BLOCKS_BROKEN:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has broken ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" blocks");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create
                                        ()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has broken " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " blocks");
					}

					break;
				case KILLS_MOBS:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has killed ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" mobs");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create
                                        ()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has killed " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " mobs");
					}

					break;
				case KILLS_PLAYERS:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has killed ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" players");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has killed " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " players");
					}

					break;
				case TIME_PLAYED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has played for ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent(
								StatzUtil.timeToString((int) info.getTotalValue(), Time.MINUTES));
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent("");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has played for " + ChatColor.GREEN
								+ StatzUtil.timeToString((int) info.getTotalValue(), Time.MINUTES));
					}

					break;
				case FOOD_EATEN:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has eaten ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" times");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has eaten " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
					}

					break;
				case DAMAGE_TAKEN:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has taken ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" points of damage");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create
                                        ()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has taken " + ChatColor.GREEN
								+ info.getTotalValue(2) + ChatColor.DARK_AQUA + " points of damage");
					}

					break;
				case TIMES_SHORN:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has shorn ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" sheep");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has shorn " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " sheep");
					}

					break;
				case DISTANCE_TRAVELLED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has travelled ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent(info.getTotalValue(2) + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" blocks");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create
                                        ()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has travelled " + ChatColor.GREEN
								+ info.getTotalValue(2) + ChatColor.DARK_AQUA + " blocks");
					}

					break;
				case ITEMS_CRAFTED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has crafted ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" items");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has crafted " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items");
					}

					break;
				case XP_GAINED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has gained ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" exp");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has gained " + ChatColor.GREEN
								+ info.getTotalValue() + ChatColor.DARK_AQUA + " exp");
					}

					break;
				case VOTES:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has voted ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" times");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has voted " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
					}

					break;
				case ARROWS_SHOT:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has fired ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" arrows");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has fired " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " arrows");
					}

					break;
				case ENTERED_BEDS:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " went sleepy sleepy in ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" beds");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create
                                        ()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " went sleepy sleepy in "
								+ ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " beds");
					}

					break;
				case COMMANDS_PERFORMED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has performed ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" commands");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create
                                        ()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has performed " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " commands");
					}

					break;
				case TIMES_KICKED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has been kicked ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" times");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create
                                        ()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has been kicked " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
					}

					break;
				case TOOLS_BROKEN:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has broken ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" tools");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has broken " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " tools");
					}

					break;
				case EGGS_THROWN:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has thrown ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" unborn chickens (eggs)");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has thrown " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " unborn chickens (eggs)");
					}

					break;
				case WORLDS_CHANGED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has changed worlds ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" times");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has changed worlds " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
					}

					break;
				case BUCKETS_FILLED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has filled ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" buckets");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has filled " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " buckets");
					}

					break;
				case BUCKETS_EMPTIED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has emptied ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" buckets");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create
                                        ()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has emptied " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " buckets");
					}

					break;
				case ITEMS_PICKED_UP:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has picked up ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" items");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has picked up " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items");
					}

					break;
				case ITEMS_DROPPED:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has dropped ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" items");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has dropped " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items");
					}

					break;
				case TELEPORTS:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has teleported ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" times");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has teleported " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
					}

					break;
				case VILLAGER_TRADES:
					if (sender instanceof Player) {
						TextComponent message = new TextComponent(playerName + " has traded ");
                        message.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
                        partTwo.setColor(net.md_5.bungee.api.ChatColor.GREEN);

						TextComponent partThree = new TextComponent(" times with villagers");
                        partThree.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

						message.addExtra(partTwo);
						message.addExtra(partThree);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz list " + playerName + " " + statType.toString()));
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click on me for more info about ")
                                        .append(statType.toString()).color(net.md_5.bungee.api.ChatColor.GOLD).create()));

						messagesSpigot.add(message);
					} else {
						messages.add(ChatColor.DARK_AQUA + playerName + " has traded " + ChatColor.GREEN
								+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times with villagers");
					}

					break;
				default:
					if (sender instanceof Player) {
						messagesSpigot.add(new TextComponent("Unknown stat '" + statType + "'."));
					} else {
						messages.add("Unknown stat '" + statType + "'.");
					}

					break;
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

		//for (int i = 0; i < pages; i++) {

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

			//System.out.println("index: " + index);

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
