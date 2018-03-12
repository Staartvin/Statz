package me.staartvin.statz.update;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The UpdatePoolManager is responsible for keeping track of the different update queries that need to be sent to the
 * database. Whenever a listener records a new event, the update is registered in the pool. The pool consists of a
 * list of {@link Query} objects. The pool is categorized by {@link PlayerStat}. Each statistic has its own pool. A
 * pool is just a big list of update queries.
 * <br>
 * <br>
 * Note that a pool consists of queries that most likely conflict. For example, the pool for the statistic
 * DISTANCE_TRAVELLED consists of many small queries that all have a value associated to them. There is task that
 * regularly reads the pools and sends these queries to the database (a so-called UpdateDatabaseTask). This task will
 * compress all queries into a pool into a smaller pool of queries that do not overlap. For the compression, see when
 * {@link Query} objects conflict: {@link Query#conflicts(Query)}.
 * <br>
 * <br>
 * Here's a small example. Let's say we look at the DISTANCE_TRAVELLED pool. We have two players online that are
 * walking about. We therefore have a few update queries in the pool, like so:
 * <ul>
 * <li>PlayerA walked 4 blocks on world 'overworld'.</li>
 * <li>PlayerA walked 5 blocks on world 'overworld'.</li>
 * <li>PlayerA walked 10 blocks on world 'overworld'.</li>
 * <li>PlayerA walked 56 blocks on world 'netherworld'.</li>
 * <li>PlayerB walked 3 blocks on world 'overworld'.</li>
 * <li>PlayerB walked 8 blocks on world 'overworld'.</li>
 * </ul>
 * When updating the database, the pool then gets compressed to:
 * <ul>
 * <li>PlayerA walked 19 blocks on world 'overworld'.</li>
 * <li>PlayerA walked 56 blocks on world 'netherworld'.</li>
 * <li>PlayerB walked 11 blocks on world 'overworld'.</li>
 * </ul>
 * After compression, the database is updated with these queries.
 */
public class UpdatePoolManager {

    private Map<PlayerStat, List<Query>> updateQueries = new ConcurrentHashMap<>();
    private Statz plugin;

    public static boolean isForcingPool = false;

    public UpdatePoolManager(Statz instance) {
        this.plugin = instance;
    }

    /**
     * Get a map containing every pool for each statistic. The pools are given in key-value pairs where the key is
     * the type of statistic for the pool, and the value is the pool (list of queries) itself.
     * @return a map of all pools.
     */
    public Map<PlayerStat, List<Query>> getAllUpdateQueries() {
        return updateQueries;
    }

    /**
     * Get a list of queries that are in the pool of the given statistic.
     * @param statType Type of statistic.
     * @return a list of queries that are in the pool.
     */
    private List<Query> getUpdateQueries(PlayerStat statType) {
        if (!hasUpdateQueries(statType)) {
            return new ArrayList<>();
        }

        return this.updateQueries.get(statType);
    }

    /**
     * Get a list of queries that are in the pool of the given statistic. Note that this creates a copy of the pool
     * and hence you cannot actually alter the 'real' pool.
     * @param statType Type of statistic.
     * @return a list of queries that are in the pool.
     */
    public List<Query> getUpdateQueriesCopy(PlayerStat statType) {
        List<Query> queries = new ArrayList<>();

        for (Query q : this.getUpdateQueries(statType)) {
            queries.add(q);
        }

        return queries;
    }

    /**
     * Get whether there is a pool for the given statistic. Note that a pool may exist but still be empty.
     * @param statType Type of statistic
     * @return true if there is a pool for the given statistic, false otherwise.
     */
    public boolean hasUpdateQueries(PlayerStat statType) {
        return updateQueries.containsKey(statType) && updateQueries.get(statType) != null;
    }

    /**
     * Set the pool of the given statistic.
     * @param statType Type of statistic
     * @param queries List of queries to set the pool to
     */
    private void setUpdateQueries(PlayerStat statType, List<Query> queries) {
        this.updateQueries.put(statType, queries);
    }

    /**
     * Add a query to a pool of the given statistic.
     * @param statType Type of statistic
     * @param query Query to add
     */
    public void addQuery(PlayerStat statType, Query query) {
        List<Query> queries = getUpdateQueries(statType);

        queries.add(query);

        this.setUpdateQueries(statType, queries);
    }

    /**
     * Clear all pools.
     */
    public void clearAllUpdateQueries() {
        updateQueries.clear();
    }

    /**
     * Clear the pool of the given statistic.
     * @param statType Type of statistic
     */
    public void clearUpdateQueries(PlayerStat statType) {
        this.updateQueries.remove(statType);
    }

    /**
     * Register a new update query for a player. This will also update the caching manager so the cache is up to date.
     *
     * @param query    Query that is new
     * @param statType Type of statistic this query belongs to
     * @param uuid     UUID of the player this query pertains to.
     * @throws IllegalArgumentException if given query or uuid is null.
     */
    public void registerNewUpdateQuery(final Query query, final PlayerStat statType, final UUID uuid) throws
            IllegalArgumentException {

        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null.");
        }

        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }

        // Add query to list of update queries
        this.addQuery(statType, query);

        // Update cache of a player with this new update query. Run this async as it can be an intensive task.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getCachingManager().addCachedQuery(statType, query, uuid);
            }
        });

    }

    /**
     * Print all pools to the console.
     */
    public void printPool() {

        if (this.updateQueries.isEmpty()) {
            System.out.println("POOL IS EMPTY");
            return;
        }

        System.out.println("PRINT POOL");
        System.out.println("------------------------");

        for (PlayerStat stat : PlayerStat.values()) {

            List<Query> queries = this.getUpdateQueriesCopy(stat);

            if (queries == null || queries.isEmpty()) {
                System.out.println("[PlayerStat: " + stat + "]: EMPTY");
                continue;
            }

            System.out.println("------------------------");
            System.out.println("[PlayerStat: " + stat + "] Size: " + queries.size());

            for (Query query : queries) {
                System.out.println("------------------------");
                StringBuilder builder = new StringBuilder("{");
                for (Map.Entry<String, String> entry : query.getEntrySet()) {
                    builder.append(entry.getKey() + ": " + entry.getValue() + ", ");
                }
                builder.append("}");
                System.out.println(builder.toString());
            }
        }
    }


}
