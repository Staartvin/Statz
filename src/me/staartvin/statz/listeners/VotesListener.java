package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.VotesSpecification;
import me.staartvin.utils.pluginlibrary.events.PlayerVotedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class VotesListener implements Listener {

    private final Statz plugin;

    public VotesListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVote(final PlayerVotedEvent event) {

        final PlayerStat stat = PlayerStat.VOTES;

        Player player = event.getPlayer();

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
                new VotesSpecification(player.getUniqueId(), 1).constructQuery());

    }
}
