package me.staartvin.statz.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class VehicleMoveListener implements Listener {

	private final Statz plugin;

	public VehicleMoveListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(final VehicleMoveEvent event) {

		final PlayerStat stat = PlayerStat.DISTANCE_TRAVELLED;

		Vehicle vehicle = event.getVehicle();

		System.out.println("VEHICLE: " + vehicle);

		if (vehicle.getPassenger() == null)
			return;

		// Get player
		final Player player;

		String movementType;

		if (vehicle.getPassenger() instanceof Player) {
			player = (Player) vehicle.getPassenger();

			movementType = StatzUtil.getMovementType(player);
		} else {
			// No passenger on the passenger
			if (vehicle.getPassenger().getPassenger() == null)
				return;

			// There is no player riding.
			if (!(vehicle.getPassenger().getPassenger() instanceof Player))
				return;

			player = (Player) vehicle.getPassenger().getPassenger();

			movementType = StatzUtil.getMovementType(player);
		}

		// No player found, no movementType found.
		if (player == null || movementType == null)
			return;

		// Get player info.
		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

		double distTravelled = event.getFrom().distance(event.getTo());

		if (distTravelled == 0) {
			return;
		}

		// Get current value of stat.
		double currentValue = 0;

		// Check if it is valid!
		if (info.isValid()) {
			for (HashMap<String, Object> map : info.getResults()) {
				if (map.get("world") != null
						&& map.get("world").toString().equalsIgnoreCase(player.getWorld().getName())
						&& map.get("moveType") != null
						&& map.get("moveType").toString().equalsIgnoreCase(movementType)) {
					currentValue += Double.parseDouble(map.get("value").toString());
				}
			}
		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + distTravelled),
						"moveType", movementType, "world", player.getWorld().getName()));

	}
}
