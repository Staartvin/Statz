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

	public PlayerInfo getPlayerInfo(final UUID uuid, final PlayerStat statType) {
		final PlayerInfo info = new PlayerInfo(uuid);

		List<HashMap<String, String>> results = plugin.getSqlConnector().getObjects(statType.getTableName(),
				StatzUtil.makeQuery("uuid", uuid.toString()));

		List<HashMap<String, String>> storedQueries = plugin.getDataPoolManager().getStoredQueries(statType,
				StatzUtil.makeQuery("uuid", uuid.toString()));
		
		//System.out.println("StoredQueries: " + storedQueries);

		if (storedQueries != null && !storedQueries.isEmpty()) {
			// There ARE conflicting queries and since the pool is more up to date, we have to override the old ones.

			for (HashMap<String, String> storedQuery : storedQueries) {
				
				// There is no data of this stat in the database, so storedQueries are always more up to date.
				if (results == null || results.isEmpty()) {
					//System.out.println("Stored query " + StatzUtil.printQuery(storedQuery) + " was more up to date since there is no record in database");
					results.add(storedQuery);
					continue;
				}
				
				//System.out.println("Stored query: " + StatzUtil.printQuery(storedQuery));
				
				List<HashMap<String, String>> conflictingQueries = plugin.getDataPoolManager().findConflicts(statType,
						storedQuery, results);
				
				if (conflictingQueries == null || conflictingQueries.isEmpty()) {
					//System.out.println("No conflicts found between " + StatzUtil.printQuery(storedQuery) + " and " + results);
					results.add(storedQuery);
					continue;
				}

				for (HashMap<String, String> conflictingQuery : conflictingQueries) {
					//System.out.println("Stored query " + StatzUtil.printQuery(storedQuery) + " conflicts with " + StatzUtil.printQuery(conflictingQuery));
					results.remove(conflictingQuery);
					results.add(storedQuery);
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
