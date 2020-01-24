package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.XPGainedSpecification;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class XPGainedListener implements Listener {

    private final Statz plugin;

    public XPGainedListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onXPGain(final PlayerExpChangeEvent event) {

        final PlayerStat stat = PlayerStat.XP_GAINED;

        // Get player
        final Player player = event.getPlayer();

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
                new XPGainedSpecification(player.getUniqueId(), event.getAmount(), player.getWorld().getName())
                        .constructQuery());

    }
}
