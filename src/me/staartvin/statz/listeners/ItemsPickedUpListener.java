package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.ItemsPickedUpSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemsPickedUpListener implements Listener {

    private final Statz plugin;

    public ItemsPickedUpListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPickUp(final PlayerPickupItemEvent event) {

        final PlayerStat stat = PlayerStat.ITEMS_PICKED_UP;

        // Get player
        final Player player = event.getPlayer();

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        PlayerStatSpecification specification = new ItemsPickedUpSpecification(player.getUniqueId(),
                event.getItem().getItemStack().getAmount(), player.getWorld().getName(),
                event.getItem().getItemStack().getType());

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

    }
}
