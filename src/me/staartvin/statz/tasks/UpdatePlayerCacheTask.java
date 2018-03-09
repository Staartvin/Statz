package me.staartvin.statz.tasks;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;


public class UpdatePlayerCacheTask extends BukkitRunnable {

    private UUID uuid;
    private Statz plugin;

    public UpdatePlayerCacheTask(Statz instance, UUID uuid) {
        this.uuid = uuid;
        this.plugin = instance;
    }

    @Override
    public void run() {

        // Stop task if player is not found anymore.
        if (Bukkit.getServer().getPlayer(uuid) == null) {
            System.out.println("Killing task as " + uuid + " is not online anymore.");
            this.cancel();
            return;
        }

        PlayerInfo cachedData = new PlayerInfo(uuid);

        for (PlayerStat statType : PlayerStat.values()) {
            // Find conflicts and resolve them.
            cachedData = cachedData.resolveConflicts(plugin.getDataManager().getPlayerInfo(uuid, statType));
        }

        System.out.println("Storing new data in cache for " + uuid + " with " + cachedData.getNumberOfStatistics() +
                " lists.");
        // Store into cache.
        plugin.getCachingManager().registerCachedData(uuid, cachedData);

    }
}
