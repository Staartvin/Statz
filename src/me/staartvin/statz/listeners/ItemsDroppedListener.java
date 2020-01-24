package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.ItemsDroppedSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemsDroppedListener implements Listener {

    private final Statz plugin;

    public ItemsDroppedListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDrop(final PlayerDropItemEvent event) {

        final PlayerStat stat = PlayerStat.ITEMS_DROPPED;

        // Get player
        final Player player = event.getPlayer();

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        PlayerStatSpecification specification = new ItemsDroppedSpecification(player.getUniqueId(),
                event.getItemDrop().getItemStack().getAmount(), player.getWorld().getName(),
                event.getItemDrop().getItemStack().getType());

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

    }
}
