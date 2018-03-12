package me.staartvin.statz.tasks;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
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

            cachedData = cachedData.resolveConflicts(plugin.getDataManager().getPlayerInfo(uuid, statType));
        }

        System.out.println("Updated cache of " + uuid);
        // Store into cache.
        plugin.getCachingManager().registerCachedData(uuid, cachedData);

    }
}
