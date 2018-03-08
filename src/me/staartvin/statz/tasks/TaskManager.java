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

    public void startUpdatePlayerCacheTask(UUID uuid) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new
                UpdatePlayerCacheTask(plugin, uuid), 0, 20 * 30);

        System.out.println("Running new task: " + task.getTaskId());

        cacheUpdateTask.put(uuid, task.getTaskId());

    }
}
