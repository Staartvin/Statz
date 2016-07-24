package me.staartvin.statz.listeners;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class PlayerKillsPlayerListener implements Listener {

	private final Statz plugin;

	public PlayerKillsPlayerListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDie(final EntityDeathEvent event) {

		final PlayerStat stat = PlayerStat.KILLS_MOBS;

		Entity e = event.getEntity();

		if (!(e.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			return;
		}

		EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e.getLastDamageCause();
		if (nEvent.getDamager() instanceof Player) {
			// Entity died because of Player
			// Killer
			final Player player = (Player) nEvent.getDamager();

			if (e instanceof Player) {
				// Player killed player

				final Player murderedPlayer = (Player) e;

				//				//Get player info.
				//				final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(),
				//						PlayerStat.KILLS_PLAYERS, StatzUtil.makeQuery("world", player.getWorld().getName(),
				//								"playerKilled", murderedPlayer.getName()));
				//
				//				// Get current value of stat.
				//				int currentValue = 0;
				//
				//				// Check if it is valid!
				//				if (info.isValid()) {
				//					currentValue += info.getTotalValue();
				//					//currentValue = Integer.parseInt(info.getResults().getValue(0).getValue("value").toString());
				//				}

				// Update value to new stat.
				plugin.getDataManager().setPlayerInfo(player.getUniqueId(), PlayerStat.KILLS_PLAYERS,
						StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", 1, "world",
								player.getWorld().getName(), "playerKilled", murderedPlayer.getName()));

			} else {
				// Player killed mob		
// Handled by other listener
			}	
		} else {
			// Entity died of something else
		}

		//		
	}
}
