package me.staartvin.statz.datamanager;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.Table;

/**
 * Since Statz is event-driven, a lot of queries are made to the database in sequential order. 
 * For 10 movements of a player, 10 updates queries have to be sent to the server. That way of inserting data is inefficient.
 * Instead of updating for every single query, Statz pools the queries together and obtains the highest result and updates it to
 * the database in a single query. This class handles the pools of queries that will have to be sent to the database.
 * <br><br>Whenever a query is added to the pool, the pool manager checks whether there is already an existing query with the same conditions
 * in the pool. When there is, the old query is removed from the pool and the new one is inserted.
 * <br><br>Since we are not updating the database constantly, it will be not up to date (until the latest events have been processed and queried).
 * To solve this issue, the pool manager will first look in the pool and check whether there is an update query that meet the given conditions.
 * If there is, the most recent update query (satisfying the given conditions) will be returned, as it is more up to date compared to the database.
 * If there is no update query satisfying the given conditions, the pool manager will hand over the 'GET' request to the actual database manager
 * to get the data from the database.
 * 
 * <br><br>Lastly, every x seconds, the pool manager updates the database with the current update queries that are in the pool to ensure
 * that the database is not running <i>too far</i> behind. In case of an unexpected shutdown of the server, the database will still have 
 * 'sort of' the most recent data.
 * 
 * <br><br>This system was invented to decrease the amount of calls to the database and improve server performance.
 * 
 * @author Staartvin
 *
 */
/**
 * @author Staartvin
 *
 */
public class DataPoolManager {

	private Statz plugin;

	// The PlayerStat key is to distinguish which table the query belongs to.
	// The List contains all queries for one specific table. A LinkedHashMap is one query to the database.
	private HashMap<PlayerStat, List<Query>> pool = new HashMap<>();

	// What queries were most recently written to the database?
	private HashMap<PlayerStat, List<Query>> lastWrittenQueries = new HashMap<>();

	public DataPoolManager(Statz plugin) {
		this.plugin = plugin;
	}

	/**
	 * Add a query to pool.
	 * @param stat Stat of this query
	 * @param query The actual query
	 * @return true if the query was successfully added to the pool, false if otherwise.
	 */
	public synchronized boolean addQuery(PlayerStat stat, Query query) {

		List<Query> queries = this.getStoredQueries(stat);

		if (queries == null) {
			queries = new ArrayList<>();
		}

		if (queries.isEmpty()) {
			// Since there are no other queries in the pool, we do not have to check for conflicting ones.
			queries.add(query);
			pool.put(stat, queries);

			//System.out.println("Add to pool (because empty): " + query);
			return true;
		}

		List<Query> conflictsQuery = this.findConflicts(stat, query);

		// No conflicting queries found, so we can just add the given query to the pool without having to worry about conflicts.
		if (conflictsQuery == null || conflictsQuery.isEmpty()) {
			queries.add(query);
			pool.put(stat, queries);
			//System.out.println("Add to pool (because no conflict): " + query);
			return true;
		}

		// Shit, we found a conflicting query. Remove conflicting ones and add a new query.
		queries.removeAll(conflictsQuery);

		// Add new query
		queries.add(query);

		//System.out.println("Add to pool (after conflict): " + query);

		// Update pool with new queries
		pool.put(stat, queries);

		return true;
	}

	public synchronized void removeQuery(PlayerStat stat, Query query) {
		List<Query> queries = this.getStoredQueries(stat);

		if (queries == null) {
			queries = new ArrayList<>();
		}

		if (queries.isEmpty()) {
			// Since there are no other queries in the pool, we cannot delete any queries.
			return;
		}

		Query toBeDeleted = null;

		for (Query iterateQuery : queries) {
			if (query.equals(iterateQuery)) {
				toBeDeleted = iterateQuery;
			}
		}

		// No query found to be deleted
		if (toBeDeleted == null)
			return;

		// Remove query from copy pool
		queries.remove(toBeDeleted);

		// Update pool with removed query
		pool.put(stat, queries);

		return;
	}

	public synchronized void removeQueries(PlayerStat stat, List<Query> queries) {
		for (Query q : queries) {
			this.removeQuery(stat, q);
		}
	}

	/**
	 * Find conflicts for the current queries in the pool.
	 * For more info, see {@link Query#findConflicts(List)}.
	 * @param stat Stat to get the queries from
	 * @param queryCompare Query to compare other queries (currently in the pool) to.
	 * @return a list of conflicting queries or null if there are no conflicting queries.
	 */
	public List<Query> findConflicts(PlayerStat stat, Query queryCompare) {
		List<Query> conflicts = new ArrayList<Query>();

		if (queryCompare == null) {
			return conflicts; // Empty list.
		}

		List<Query> poolQueries = this.getStoredQueries(stat);

		if (poolQueries == null || poolQueries.isEmpty()) {
			return conflicts; // Empty list.
		}

		conflicts = queryCompare.findConflicts(poolQueries);

		return conflicts;
	}

	//	/**
	//	 * Get queries in the given list of queries that conflict with the given queryCompare.
	//	 * A query conflicts with another query when they have the same values for the same columns (except for the column 'value').
	//	 * <br>
	//	 * <br>Let's assume there are three queries: A, B and C.
	//	 * Query A consists of {uuid: Staartvin, mob: COW, world: testWorld}. Query B is made of {uuid: Staartvin, mob:COW, world: noTestWorld}.
	//	 * Lastly, Query C is made of {uuid: BuilderGuy, mob:COW, world: testWorld}. None of these queries conflict with each other.
	//	 * <br><br>Query A does not conflict with B, as the world columns do not have the same values. Query A does also not conflict with C,
	//	 * since the UUIDs don't match. Lastly, query B does not conflict with query C, since both the UUID and world columns do not match.
	//	 *
	//	 * @param queryCompare Query to compare to other queries
	//	 * @param queries A list of queries to check whether they conflict with the given queryCompare. 
	//	 * @return a list of queries (from the given queries list) that conflict with queryCompare or null if no conflicts were found.
	//	 */
	//	public List<HashMap<String, String>> findConflicts(HashMap<String, String> queryCompare,
	//			List<HashMap<String, String>> queries) {
	//		List<HashMap<String, String>> conflictingQueries = new ArrayList<>();
	//
	//		if (queries == null)
	//			return null;
	//
	//		// Do reverse traversal
	//		for (int i = queries.size() - 1; i >= 0; i--) {
	//
	//			HashMap<String, String> comparedQuery = queries.get(i);
	//
	//			boolean isSame = true;
	//
	//			for (Entry<String, String> entry : queryCompare.entrySet()) {
	//				String columnName = entry.getKey();
	//				String columnValue = entry.getValue();
	//
	//				if (columnName.equalsIgnoreCase("value"))
	//					continue;
	//
	//				// Stored query does not have value that the given query has -> this cannot conflict
	//				if (comparedQuery.get(columnName) == null) {
	//					isSame = false;
	//					break;
	//				}
	//
	//				// If value of condition in stored query is not the same as the given query, they cannot conflict. 
	//				if (!comparedQuery.get(columnName).equalsIgnoreCase(columnValue)) {
	//					isSame = false;
	//					break;
	//				}
	//			}
	//
	//			if (!comparedQuery.get("uuid").equalsIgnoreCase(queryCompare.get("uuid"))) {
	//				isSame = false;
	//			}
	//
	//			// We have found a conflicting query
	//			if (isSame) {
	//				conflictingQueries.add(comparedQuery);
	//			}
	//		}
	//
	//		// No conflicting query found
	//		if (conflictingQueries.isEmpty()) {
	//			return null;
	//		} else {
	//			return conflictingQueries;
	//		}
	//
	//	}

	/**
	 * Get the queries that are currently in the pool (and have not been set to the database yet).
	 * @param stat Queries of what stat type?
	 * @return a list of queries that are in the pool or null if there are no queries in the pool.
	 */
	public List<Query> getStoredQueries(PlayerStat stat) {
		List<Query> queries = pool.get(stat);

		if (queries == null || queries.isEmpty()) {
			return null;
		}

		// Return a copy of the list
		return new ArrayList<Query>(queries);
	}

	//	// Return exactly the same as getStoredQueries(), except the returned list is not a copy but the real object.
	//	private List<Query> getRawQueries(PlayerStat stat) {
	//		List<Query> queries = pool.get(stat);
	//
	//		if (queries == null || queries.isEmpty()) {
	//			return null;
	//		}
	//
	//		// Return a copy of the list
	//		return queries;
	//	}

	/**
	 * Send queries that are currently in the pool to the database. This will remove the queries from the pool that are sent to the database.
	 */
	public void sendPool() {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			public void run() {
				forceSendPool();
			}
		});
	}

	/**
	 * Send queries that are currently in the pool to the database. This will remove the queries from the pool that are sent to the database.
	 * <br>This will do it on the main thread and is only to be used when the server is shutdown.
	 */
	public void forceSendPool() {

		printPool();

		if (plugin.getConfigHandler().shouldShowDatabaseSave()) {
			plugin.debugMessage(ChatColor.BLUE + "Save Statz database.");
		}

		for (PlayerStat stat : PlayerStat.values()) {

			List<Query> queries = getStoredQueries(stat);
			List<Query> deletedQueries = new ArrayList<>();

			Table table = plugin.getSqlConnector().getTable(stat.getTableName());

			if (queries == null || table == null) {
				// Pool is empty
				continue;
			}

			//Update in batch.
			plugin.getSqlConnector().setBatchObjects(table, queries);

			//System.out.println("In pool: " + queries.size() + " for stat " + stat.getTableName());

			// Add to last written query
			List<Query> lastWritten = lastWrittenQueries.get(stat);

			// Send to database
			for (Query query : queries) {
				// Send query to database. Update: Do not send this directly to database, but use batch update instead.
				//plugin.getSqlConnector().setObjects(table, query);

				if (lastWritten == null) {
					lastWritten = new ArrayList<>();
				}

				List<Query> conflicts = query.findConflicts(lastWritten);

				if (conflicts != null && !conflicts.isEmpty()) {
					// Override last written query if one conflicts
					for (Query conflict : conflicts) {
						//System.out.println("Remove from last written: " + conflict);
						lastWritten.remove(conflict);
					}
				}

				//System.out.println("Add to last written: " + query);
				lastWritten.add(query);

				deletedQueries.add(query);

			}

			lastWrittenQueries.put(stat, lastWritten);

			try {
				// Remove sent queries from pool
				//queries.removeAll(deletedQueries);
				removeQueries(stat, deletedQueries);
			} catch (ConcurrentModificationException e) {
				plugin.debugMessage("Some data may not have been removed.");
			}

		}
	}

	/**
	 * Get the queries that were last performed (from the pool) on the database.
	 * @param stat What statType do we need to get the queries from.
	 * @return a query in the form of a hashmap.
	 */
	public synchronized List<Query> getLatestQueries(PlayerStat stat) {
		return lastWrittenQueries.get(stat);
	}

	/**
	 * Print the queries that are in the pool
	 */
	public void printPool() {

		if (pool.size() == 0) {
			System.out.println("POOL IS EMPTY");
			return;
		}

		System.out.println("PRINT POOL");
		System.out.println("------------------------");

		for (PlayerStat stat : PlayerStat.values()) {

			List<Query> queries = this.getStoredQueries(stat);

			if (queries == null || queries.isEmpty()) {
				System.out.println("[PlayerStat: " + stat + "]: EMPTY");
				continue;
			}

			System.out.println("------------------------");
			System.out.println("[PlayerStat: " + stat + "] Size: " + queries.size());

			for (Query query : queries) {
				System.out.println("------------------------");
				StringBuilder builder = new StringBuilder("{");
				for (Entry<String, String> entry : query.getEntrySet()) {
					builder.append(entry.getKey() + ": " + entry.getValue() + ", ");
				}
				builder.append("}");
				System.out.println(builder.toString());
			}
		}
	}
}
