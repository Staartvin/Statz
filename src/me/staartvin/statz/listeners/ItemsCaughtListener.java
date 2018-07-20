package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class ItemsCaughtListener implements Listener {

    private final Statz plugin;

    public ItemsCaughtListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFishCaught(final PlayerFishEvent event) {

        final PlayerStat stat = PlayerStat.ITEMS_CAUGHT;

        // Get player
        final Player player = event.getPlayer();

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        Entity entity;
        ItemStack item;

        String materialName = null;

        if (event.getCaught() != null) {
            entity = event.getCaught();

            if (!(entity instanceof Item)) {
                return; // Did not catch an item
            }

            item = ((Item) entity).getItemStack();

            materialName = item.getType().name();
        } else {
            // Did not catch anything.
            return;
        }

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, StatzUtil.makeQuery("uuid",
                player.getUniqueId().toString(), "value", item.getAmount(), "caught", materialName, "world", player
                        .getWorld().getName()));

    }
}
