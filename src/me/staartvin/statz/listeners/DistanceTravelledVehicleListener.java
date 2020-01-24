package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.DistanceTravelledSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class DistanceTravelledVehicleListener implements Listener {

	private final Statz plugin;

	public DistanceTravelledVehicleListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(final VehicleMoveEvent event) {

		final PlayerStat stat = PlayerStat.DISTANCE_TRAVELLED;

		Vehicle vehicle = event.getVehicle();

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

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        final double distTravelled = event.getFrom().distance(event.getTo());

        if (distTravelled == 0) {
            return;
        }

        PlayerStatSpecification specification = new DistanceTravelledSpecification(player.getUniqueId(),
                distTravelled, player.getWorld().getName(), movementType);

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

    }
}
