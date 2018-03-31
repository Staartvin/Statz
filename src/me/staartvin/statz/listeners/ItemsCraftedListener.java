package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class ItemsCraftedListener implements Listener {

	private final Statz plugin;

	public ItemsCraftedListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCraft(final CraftItemEvent event) {

		final PlayerStat stat = PlayerStat.ITEMS_CRAFTED;

		// Get player
		final Player player = (Player) event.getWhoClicked();

		// Do general check
		if (!plugin.doGeneralCheck(player, stat))
			return;

		final String itemCrafted = event.getCurrentItem().getType().toString();

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
                StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", event.getCurrentItem().getAmount(), "world",
						player.getWorld().getName(), "item", itemCrafted));

	}
}
