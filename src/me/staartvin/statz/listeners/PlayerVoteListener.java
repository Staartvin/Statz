package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class PlayerVoteListener implements Listener {

	private final Statz plugin;

	public PlayerVoteListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVote(final VotifierEvent event) {

		final PlayerStat stat = PlayerStat.VOTES;

		// Get player
		final Player player = (Player) plugin.getServer().getPlayer(event.getVote().getUsername());

		// Unknown player
		if (player == null)
			return;

		// Get player info.
		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

		// Get current value of stat.
		int currentValue = 0;

		// Check if it is valid!
		if (info.isValid()) {
			currentValue += info.getTotalValue();
		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, StatzUtil.makeQuery("uuid",
				player.getUniqueId().toString(), "value", (currentValue + 1)));

	}
}
