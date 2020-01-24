package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.ArrowsShotSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class ArrowsShotListener implements Listener {

	private final Statz plugin;

	public ArrowsShotListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShootArrow(final EntityShootBowEvent event) {

		final PlayerStat stat = PlayerStat.ARROWS_SHOT;

		if (!(event.getEntity() instanceof Player)) {
			return; // It was not a player that shoot a bow
		}

		// Get player
		final Player player = (Player) event.getEntity();

		// Do general check
		if (!plugin.doGeneralCheck(player, stat))
			return;

		PlayerStatSpecification specification = new ArrowsShotSpecification(player.getUniqueId(), 1,
				player.getWorld().getName());


		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

	}
}
