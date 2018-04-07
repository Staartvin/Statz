package me.staartvin.statz.tasks;

import me.staartvin.statz.Statz;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is responsible for managing all tasks that need to be run (periodically or only once). It is preferred
 * to run a task from instead of firing it yourself using the BukkitScheduler.
 */
public class TaskManager {

    // TODO: Add a task that can retrieve

    private Statz plugin;

    /**
     * How often should the database be updated with queries from the pool (in seconds)?
     */
    public static int UPDATE_DATABASE_TASK_INTERVAL = 10;
    /**
     * How often should the cache of a player be validated with the database (in seconds)?
     */
    public static int UPDATE_PLAYER_CACHE_INTERVAL = 60 * 5;
    // Store the task id of the 'update player cache' task for each player.
    private Map<UUID, Integer> cacheUpdateTask = new HashMap<>();

    public TaskManager(Statz instance) {
        this.plugin = instance;
        UPDATE_DATABASE_TASK_INTERVAL = instance.getConfigHandler().getPeriodicSaveTime();
        UPDATE_PLAYER_CACHE_INTERVAL = instance.getConfigHandler().getPeriodiceRefreshPlayerCacheTime();
    }

    /**
     * Start an update task that periodically updates the cache of a player
     *
     * @param uuid UUID of the player to start the task for.
     */
    public void startUpdatePlayerCacheTask(UUID uuid) {
        BukkitTask task = new UpdatePlayerCacheTask(plugin, uuid).runTaskTimerAsynchronously(plugin, 0, 20 *
                UPDATE_PLAYER_CACHE_INTERVAL);

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

        plugin.debugMessage("Stopped update task of " + uuid);

        // Stop task
        plugin.getServer().getScheduler().cancelTask(taskID);

        // Remove task id from hashmap.
        cacheUpdateTask.remove(uuid);
    }

    /**
     * Starts the task that periodically updates the database with queries from the pools.
     */
    public void startUpdateDatabaseTask() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new UpdateDatabaseTask(plugin),
                0, 20 * UPDATE_DATABASE_TASK_INTERVAL);
    }
}
