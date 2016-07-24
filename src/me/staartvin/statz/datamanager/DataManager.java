package me.staartvin.statz.datamanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
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

		// Get results from database
		List<Query> results = plugin.getSqlConnector().getObjects(statType.getTableName(),
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
					if (!map.hasValue(entry.getKey())) {
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

	public void setPlayerInfo(final UUID uuid, final PlayerStat statType, Query results) {

		//System.out.println("Add to query: " + results);

		// Add query to the pool.
		plugin.getDataPoolManager().addQuery(statType, results);
	}
}
