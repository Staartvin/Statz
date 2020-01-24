package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import me.staartvin.statz.datamanager.player.specification.ToolsBrokenSpecification;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ToolsBrokenListener implements Listener {

    private final Statz plugin;

    public ToolsBrokenListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onToolBreak(final PlayerItemBreakEvent event) {

        final PlayerStat stat = PlayerStat.TOOLS_BROKEN;

        // Get player
        final Player player = event.getPlayer();

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        ItemStack item = event.getBrokenItem();

        PlayerStatSpecification specification = new ToolsBrokenSpecification(player.getUniqueId(), 1,
                player.getWorld().getName(), item.getType());

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

    }
}
