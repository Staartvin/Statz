package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.KillsMobsSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class KillsMobsListener implements Listener {

    private final Statz plugin;

    public KillsMobsListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDie(final EntityDeathEvent event) {

        final PlayerStat stat = PlayerStat.KILLS_MOBS;

        Entity e = event.getEntity();

        if (!(e.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        PlayerStatSpecification specification;

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
                // Handled by other listener
            } else {
                // Player killed mob

                String mobType = StatzUtil.getMobType(e);

                String weapon = "";

                ItemStack item = player.getInventory().getItemInMainHand();

                if (item.getType() == Material.AIR) {
                    weapon = "HAND";
                } else {
                    weapon = item.getType().toString();
                }

                specification = new KillsMobsSpecification(player.getUniqueId(), 1, player.getWorld().getName(),
                        mobType, weapon);

                // Update value to new stat.
                plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

            }
        } else if (nEvent.getDamager() instanceof Arrow) {
            // Entity was killed by an arrow, now check if it was shot by a player
            Arrow killerArrow = (Arrow) nEvent.getDamager();

            if (killerArrow.getShooter() instanceof Player) {
                Player shooter = (Player) killerArrow.getShooter();

                specification = new KillsMobsSpecification(shooter.getUniqueId(), 1, shooter.getWorld().getName(),
                        StatzUtil.getMobType(e), "BOW");

                // Now update database.
                plugin.getDataManager().setPlayerInfo(shooter.getUniqueId(), stat, specification.constructQuery());
            }
        }

        //
    }
}
