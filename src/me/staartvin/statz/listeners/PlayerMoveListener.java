package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class PlayerMoveListener implements Listener {

	private final Statz plugin;

	public PlayerMoveListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(final PlayerMoveEvent event) {

		final PlayerStat stat = PlayerStat.DISTANCE_TRAVELLED;
		
		// Get player
		final Player player = (Player) event.getPlayer();

		// Get player info.
		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

		String movementType = StatzUtil.getMovementType(player);
		
		double distTravelled; 
		
		try {
			distTravelled = event.getFrom().distance(event.getTo());
		} catch (IllegalArgumentException e) {
			// Did not move correctly, so ignore it.
			return;
		}
		
		if (distTravelled == 0) {
			return;
		}
		
		// Get current value of stat.
		double currentValue = 0;
		
		// Check if it is valid!
		if (info.isValid()) {
			for (Query map : info.getResults()) {
				if (map.getValue("world") != null
						&& map.getValue("world").toString().equalsIgnoreCase(player.getWorld().getName())
						&& map.getValue("moveType") != null && map.getValue("moveType").toString().equalsIgnoreCase(movementType)) {
					currentValue += Double.parseDouble(map.getValue("value").toString());
				}
			}
		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + distTravelled), "moveType",
						movementType, "world", player.getWorld().getName()));

	}
}
