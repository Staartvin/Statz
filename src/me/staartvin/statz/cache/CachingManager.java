package me.staartvin.statz.cache;

import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.datamanager.player.PlayerStat;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for keeping a record of cached data for a loaded player. Each time an update query is
 * found from one of the listeners that act on player events, the cache is updated to include this data. Hence, the
 * cache of a player should be up-to-date.
 * <br>
 * <br>
 * The cache is used to provide statistics about a player. Whenever a player (or plugin) requires data about a
 * player, the cache is asked to provide it first. If there is no data in the cache about the player, it should be
 * retrieved from the database. Note that this has to occur asynchronously.
 * <br>
 * <br>
 * Finally, there is task that runs periodically that grabs the data from the database and overwrites the cache. This
 * happens to prevent a possible deviation between the real data (in the database) and the cache.
 */
public class CachingManager {

    // TODO: implement a way to check whether we already loaded a certain statistic. Now, when a player does not have
    // anything stored in the database for a given statistic, the cache for that statistic is empty (as it should be)
    // , but this means that it keeps loading fresh data from the database when cached data is requested. We should
    // make a way to know when data has been cached but it is empty for a player.

    // To keep track of the cached data
    private ConcurrentHashMap<UUID, PlayerInfo> cachedPlayerData = new ConcurrentHashMap<>();

    /**
     * Register cached data for a player. It will overwrite any current cached data.
     *
     * @param uuid        UUID of the player.
     * @param dataToCache Data to store.
     *
     * @throws NullPointerException if data to cache is null
     */
    public void registerCachedData(UUID uuid, PlayerInfo dataToCache) throws NullPointerException {

        if (dataToCache == null) {
            throw new NullPointerException("Data to cache is null.");
        }

        // Store data in cache, overwriting previous data
        cachedPlayerData.put(uuid, dataToCache);
    }

    /**
     * Add new data to the cache. The new data will be merged with the already existing cached data.
     * The merging of cached and new data can be a quite intensive process and so should preferably be run
     * asynchronously.
     * <br>
     * If no cached data exists, this will just store the given data as new data.
     *
     * @param uuid        UUID of player that the data is for
     * @param dataToCache new data to store
     * @throws IllegalArgumentException if given data is null
     */
    public void addCachedData(UUID uuid, PlayerInfo dataToCache) throws IllegalArgumentException {
        if (dataToCache == null) {
            throw new IllegalArgumentException("Data to cache is null.");
        }

        PlayerInfo cachedData = getCachedPlayerData(uuid);

        // There is no cached data, so we just register new data.
        if (cachedData == null) {
            registerCachedData(uuid, dataToCache);
            return;
        }

        // Resolve conflicts and update cache.
        PlayerInfo resolvedCache = cachedData.resolveConflicts(dataToCache);
        // Update cache with new cached data.
        this.registerCachedData(uuid, resolvedCache);
    }

    /**
     * Get cached data of a player.
     *
     * @param uuid UUID of the player.
     *
     * @return PlayerInfo object containing cached data or null if no cached data exists.
     */
    public PlayerInfo getCachedPlayerData(UUID uuid) {

        // Do sanity check to prevent NullPointerException while calling hashmap.
        if (uuid == null) {
            return null;
        }

        return cachedPlayerData.get(uuid);
    }


    /**
     * Add a single query to the cache of a player.
     *
     * @param statType   Type of statistic the query belongs to
     * @param queryToAdd Query to add
     * @param uuid       UUID of the player
     * @throws IllegalArgumentException if the given query is null or the uuid is null.
     */
    public void addCachedQuery(PlayerStat statType, Query queryToAdd, UUID uuid) throws IllegalArgumentException {

        if (queryToAdd == null) {
            throw new IllegalArgumentException("Query cannot be null.");
        }

        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }

        PlayerInfo info = new PlayerInfo(uuid);

        info.addRow(statType, queryToAdd);

        this.addCachedData(uuid, info);
    }

    /**
     * Check whether a player's data is loaded in the cache.
     *
     * @param uuid UUID of player
     * @return true if there is cached data about the given player. False otherwise.
     */
    public boolean isPlayerCacheLoaded(UUID uuid) {
        return cachedPlayerData.containsKey(uuid) && cachedPlayerData.get(uuid) != null;
    }

    /**
     * Check whether a player's data is loaded for a given statistic in the cache.
     *
     * @param uuid     UUID of the player
     * @param statType Type of statistic to check
     *
     * @return true if data about the given statistic is loaded for the given player. False otherwise.
     */
    public boolean isPlayerCacheLoaded(UUID uuid, PlayerStat statType) {

        if (!isPlayerCacheLoaded(uuid)) {
            return false;
        }

        return cachedPlayerData.get(uuid).hasDataOfPlayerStat(statType);
    }


}
