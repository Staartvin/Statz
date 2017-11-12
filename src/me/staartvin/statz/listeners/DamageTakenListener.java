package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageTakenListener implements Listener {

	private final Statz plugin;

	public DamageTakenListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(final EntityDamageEvent event) {

		final PlayerStat stat = PlayerStat.DAMAGE_TAKEN;

		if (!(event.getEntity() instanceof Player)) {
			// It was not a player that got damage
			return;
		}

		// Get player
		final Player player = (Player) event.getEntity();

		// Do general check
		if (!plugin.doGeneralCheck(player, stat))
			return;

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", event.getDamage(), "cause",
						event.getCause().toString(), "world", player.getWorld().getName()));

	}
}
