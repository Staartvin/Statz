package me.staartvin.statz.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class CraftItemListener implements Listener {

	private final Statz plugin;

	public CraftItemListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCraft(final CraftItemEvent event) {

		final PlayerStat stat = PlayerStat.ITEMS_CRAFTED;

		// Get player
		final Player player = (Player) event.getWhoClicked();

		// Get player info.
		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

		String itemCrafted = event.getCurrentItem().getType().toString();

		// Get current value of stat.
		int currentValue = 0;

		// Check if it is valid!
		if (info.isValid()) {
			for (HashMap<String, String> map : info.getResults()) {
				if (map.get("world") != null
						&& map.get("world").toString().equalsIgnoreCase(player.getWorld().getName())
						&& map.get("item") != null && map.get("item").toString().equalsIgnoreCase(itemCrafted)) {
					currentValue += Integer.parseInt(map.get("value").toString());
				}
			}
		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + 1), "world",
						player.getWorld().getName(), "item", itemCrafted));

	}
}
