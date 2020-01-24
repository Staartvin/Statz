package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.KillsPlayersSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillsPlayersListener implements Listener {

    private final Statz plugin;

    public KillsPlayersListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDie(final EntityDeathEvent event) {

        final PlayerStat stat = PlayerStat.KILLS_PLAYERS;

        Entity e = event.getEntity();

        if (!(e.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e.getLastDamageCause();
        if (nEvent.getDamager() instanceof Player) {
            // Entity died because of Player
            // Killer
            final Player player = (Player) nEvent.getDamager();

            // Do general check
            if (!plugin.doGeneralCheck(player, stat))
                return;

            if (e instanceof Player) {
                // Player killed player

                final Player murderedPlayer = (Player) e;

                PlayerStatSpecification specification = new KillsPlayersSpecification(player.getUniqueId(), 1,
                        player.getWorld().getName(), murderedPlayer.getName());

                // Update value to new stat.
                plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

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
