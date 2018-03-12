package me.staartvin.statz.update;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UpdatePoolManager {

    private Map<PlayerStat, List<Query>> updateQueries = new ConcurrentHashMap<>();
    private Statz plugin;

    public static boolean isForcingPool = false;

    public UpdatePoolManager(Statz instance) {
        this.plugin = instance;
    }

    public Map<PlayerStat, List<Query>> getAllUpdateQueries() {
        return updateQueries;
    }

    public List<Query> getUpdateQueries(PlayerStat statType) {
        if (!hasUpdateQueries(statType)) {
            return new ArrayList<>();
        }

        return this.updateQueries.get(statType);
    }

    public List<Query> getUpdateQueriesCopy(PlayerStat statType) {
        List<Query> queries = new ArrayList<>();

        for (Query q : this.getUpdateQueries(statType)) {
            queries.add(q);
        }

        return queries;
    }

    public boolean hasUpdateQueries(PlayerStat statType) {
        return updateQueries.containsKey(statType) && updateQueries.get(statType) != null;
    }

    private void setUpdateQueries(PlayerStat statType, List<Query> queries) {
        this.updateQueries.put(statType, queries);
    }

    public void addQuery(PlayerStat statType, Query query) {
        List<Query> queries = getUpdateQueries(statType);

        queries.add(query);

        this.setUpdateQueries(statType, queries);
    }

    public void clearAllUpdateQueries() {
        updateQueries.clear();
    }

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


}
