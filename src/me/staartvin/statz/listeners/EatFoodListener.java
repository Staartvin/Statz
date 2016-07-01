package me.staartvin.statz.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class EatFoodListener implements Listener {

	private final Statz plugin;

	public EatFoodListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEat(final PlayerItemConsumeEvent event) {

		final PlayerStat stat = PlayerStat.FOOD_EATEN;

		// Get player
		final Player player = event.getPlayer();

		final String foodName = StatzUtil.getFoodName(event.getItem());

		if (foodName == null)
			return;

		// Get player info.
		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

		// Get current value of stat.
		int currentValue = 0;

		// Check if it is valid!
		if (info.isValid()) {
			for (HashMap<String, String> map : info.getResults()) {
				if (map.get("world") != null
						&& map.get("world").toString().equalsIgnoreCase(player.getWorld().getName())
						&& map.get("foodEaten") != null && map.get("foodEaten").toString().equalsIgnoreCase(foodName)) {
					currentValue += Integer.parseInt(map.get("value").toString());
				}
			}
		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + 1), "foodEaten",
						foodName, "world", player.getWorld().getName()));

	}
}
