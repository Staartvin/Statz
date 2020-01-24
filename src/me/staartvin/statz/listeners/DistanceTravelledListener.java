package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.DistanceTravelledSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class DistanceTravelledListener implements Listener {

    private final Statz plugin;

    public DistanceTravelledListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {

        final PlayerStat stat = PlayerStat.DISTANCE_TRAVELLED;

        // Get player
        final Player player = event.getPlayer();

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        String movementType = StatzUtil.getMovementType(player);

        final double distTravelled;

        try {
            distTravelled = event.getFrom().distance(event.getTo());
        } catch (IllegalArgumentException e) {
            // Did not move correctly, so ignore it.
            return;
        }

        if (distTravelled == 0) {
            return;
        }

        PlayerStatSpecification specification = new DistanceTravelledSpecification(player.getUniqueId(),
                distTravelled, player.getWorld().getName(), movementType);

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

    }
}
