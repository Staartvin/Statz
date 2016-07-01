package me.staartvin.statz.datamanager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

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
	 * <br>An extra safety mechanism was added in that prevents data from accidentally overriding old data. Every x seconds, the queries
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

		List<HashMap<String, String>> results = plugin.getSqlConnector().getObjects(statType.getTableName(),
				StatzUtil.makeQuery("uuid", uuid.toString()));

		List<HashMap<String, String>> storedQueries = plugin.getDataPoolManager().getStoredQueries(statType,
				StatzUtil.makeQuery("uuid", uuid.toString()));

		//System.out.println("StoredQueries: " + storedQueries);

		if (storedQueries != null && !storedQueries.isEmpty()) {
			// There ARE conflicting queries and since the pool is more up to date, we have to override the old ones.

			//System.out.println("QUERY NOT NULL");

			for (HashMap<String, String> storedQuery : storedQueries) {

				// There is no data of this stat in the database, so storedQueries are always more up to date.
				if (results == null || results.isEmpty()) {
//					System.out.println("Stored query " + StatzUtil.printQuery(storedQuery)
//							+ " was more up to date since there is no record in database");
					results.add(storedQuery);
					continue;
				}

				//System.out.println("Stored query: " + StatzUtil.printQuery(storedQuery));

				List<HashMap<String, String>> conflictingQueries = plugin.getDataPoolManager().findConflicts(statType,
						storedQuery, results);

				if (conflictingQueries == null || conflictingQueries.isEmpty()) {
//					System.out.println(
//							"No conflicts found between " + StatzUtil.printQuery(storedQuery) + " and " + results);
					results.add(storedQuery);
					continue;
				}

				for (HashMap<String, String> conflictingQuery : conflictingQueries) {
//					System.out.println("Stored query " + StatzUtil.printQuery(storedQuery) + " conflicts with "
//							+ StatzUtil.printQuery(conflictingQuery));
					results.remove(conflictingQuery);
					results.add(storedQuery);
				}

			}

		} else {
			// No queries in the pool

			// IF query is null, it could be due to a 'just-update', so we look at the last written query to find the most recent data.
			List<HashMap<String, String>> lastQueries = plugin.getDataPoolManager().getLatestQueries(statType);

			if (lastQueries != null) {
				for (HashMap<String, String> lastQuery : lastQueries) {
					// Find the last written values that we can use by checking for conflicts
					List<HashMap<String, String>> conflicts = plugin.getDataPoolManager().findConflicts(statType, lastQuery, results);

					if (conflicts != null && !conflicts.isEmpty()) {
						// Use last written query if one conflicts
						for (HashMap<String, String> conflict : conflicts) {
							// Use last written value as old value
							conflict.put("value", lastQuery.get("value"));
						}
					}
				}
			}
		}

		// Result is not null, so this is a valid player info.
		if (results != null && !results.isEmpty()) {
			info.setValid(true);

			info.setResults(results);
		}

		return info;
	}

	public void setPlayerInfo(final UUID uuid, final PlayerStat statType, LinkedHashMap<String, String> results) {

		//final SQLiteTable table = plugin.getSqlConnector().getSQLiteTable(statType.getTableName());

		// Add query to the pool.
		plugin.getDataPoolManager().addQuery(statType, results);

		//plugin.getSqlConnector().setObjects(table, results);
	}
}
