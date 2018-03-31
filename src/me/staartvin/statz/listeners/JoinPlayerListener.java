package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinPlayerListener implements Listener {

    private final Statz plugin;

    public JoinPlayerListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event) {

        // Start task that periodically updates cache of a player.
        plugin.getTaskManager().startUpdatePlayerCacheTask(event.getPlayer().getUniqueId());

    }

}
