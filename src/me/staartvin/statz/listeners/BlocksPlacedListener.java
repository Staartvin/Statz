package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.BlocksPlacedSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlocksPlacedListener implements Listener {

    private final Statz plugin;

    public BlocksPlacedListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {

        final PlayerStat stat = PlayerStat.BLOCKS_PLACED;

        // Get player
        final Player player = event.getPlayer();

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        Block blockPlaced = event.getBlockPlaced();
        final String worldName = blockPlaced.getWorld().getName();

        PlayerStatSpecification specification = new BlocksPlacedSpecification(player.getUniqueId(), 1,
                worldName, blockPlaced.getType());

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

    }
}
