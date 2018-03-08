package me.staartvin.statz.cache;

import me.staartvin.statz.datamanager.player.PlayerInfo;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CachingManager {

    private ConcurrentHashMap<UUID, PlayerInfo> cachedPlayerData = new ConcurrentHashMap<>();

    /**
     * Register cached data for a player. It will overwrite any current data.
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

        PlayerInfo newCachedData = new PlayerInfo(uuid);


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


}
