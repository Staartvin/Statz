package me.staartvin.statz.tasks;

import me.staartvin.statz.Statz;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskManager {

    private Statz plugin;
    private Map<UUID, Integer> cacheUpdateTask = new HashMap<>();

    public TaskManager(Statz instance) {
        this.plugin = instance;
    }

    // Start update task that periodically updates the cache of a player

    /**
     * Start an update task that periodically updates the cache of a player
     *
     * @param uuid UUID of the player to start the task for.
     */
    public void startUpdatePlayerCacheTask(UUID uuid) {
        BukkitTask task = new UpdatePlayerCacheTask(plugin, uuid).runTaskTimerAsynchronously(plugin, 0, 20 * 30);

        cacheUpdateTask.put(uuid, task.getTaskId());

    }

    /**
     * Check if a player has a running task that is updating their cache periodically
     *
     * @param uuid UUID of the player
     * @return true if there is a updating task running, false otherwise.
     */
    public boolean hasRunningCacheUpdateTask(UUID uuid) {
        return cacheUpdateTask.containsKey(uuid) && cacheUpdateTask.get(uuid) != null;
    }

    /**
     * Stop the task that periodically updates the cache of a player.
     *
     * @param uuid UUID of the player.
     */
    public void stopUpdatePlayerCacheTask(UUID uuid) {
        if (!hasRunningCacheUpdateTask(uuid)) {
            return;
        }

        int taskID = cacheUpdateTask.get(uuid);

        // Stop task
        plugin.getServer().getScheduler().cancelTask(taskID);

        // Remove task id from hashmap.
        cacheUpdateTask.remove(uuid);
    }

    public void startSyncDatabaseTask() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new SyncUpdatesWithDatabaseTask(plugin),
                0, 20 * 10);
    }
}
