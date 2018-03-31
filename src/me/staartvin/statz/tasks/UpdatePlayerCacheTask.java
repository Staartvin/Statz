package me.staartvin.statz.tasks;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * This task grabs the data of a player from the database and overwrites the cache so that the cache is up-to-date.
 */
public class UpdatePlayerCacheTask extends BukkitRunnable {

    private UUID uuid;
    private Statz plugin;

    public UpdatePlayerCacheTask(Statz instance, UUID uuid) {
        this.uuid = uuid;
        this.plugin = instance;
    }

    @Override
    public void run() {

        PlayerInfo cachedData = new PlayerInfo(uuid);

        for (PlayerStat statType : PlayerStat.values()) {
            // Find conflicts and resolve them.

            if (statType.equals(PlayerStat.PLAYERS)) {
                continue;
            }

            PlayerInfo databaseInfo = plugin.getDataManager().getFreshPlayerInfo(uuid, statType);

            // User is not loaded, or there is no cache so we don't bother overwriting the cache.
            if (databaseInfo == null) {
                continue;
            }

            cachedData = cachedData.resolveConflicts(databaseInfo);
        }

        plugin.debugMessage("Updated cache of " + uuid);
        // Store into cache.
        plugin.getCachingManager().registerCachedData(uuid, cachedData);

    }
}
