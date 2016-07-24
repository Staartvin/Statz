package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class PlayerCraftItemListener implements Listener {

	private final Statz plugin;

	public PlayerCraftItemListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCraft(final CraftItemEvent event) {

		final PlayerStat stat = PlayerStat.ITEMS_CRAFTED;

		// Get player
		final Player player = (Player) event.getWhoClicked();

		// Do general check
		if (!plugin.doGeneralCheck(player))
			return;

		final String itemCrafted = event.getCurrentItem().getType().toString();

		//		// Get player info.
		//		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat,
		//				StatzUtil.makeQuery("world", player.getWorld().getName(), "item", itemCrafted));
		//
		//		// Get current value of stat.
		//		int currentValue = 0;
		//
		//		// Check if it is valid!
		//		if (info.isValid()) {
		//			currentValue += info.getTotalValue();
		//		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", 1, "world",
						player.getWorld().getName(), "item", itemCrafted));

	}
}
