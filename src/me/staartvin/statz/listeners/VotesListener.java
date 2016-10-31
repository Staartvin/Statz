package me.staartvin.statz.listeners;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class VotesListener implements Listener {

	private final Statz plugin;

	public VotesListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVote(final VotifierEvent event) {

		final PlayerStat stat = PlayerStat.VOTES;

		String userName = event.getVote().getUsername();

		// Get player
		final Player player = (Player) plugin.getServer().getPlayer(userName);

		UUID uuid = null;

		// Player is not online, so 
		if (player == null) {
		} else {
			uuid = player.getUniqueId();
			userName = player.getName();
		}

		if (player != null) {
			// Do general check
			if (!plugin.doGeneralCheck(player))
				return;
		}

		if (uuid == null) {
			@SuppressWarnings("deprecation")
			OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(userName);

			uuid = offlinePlayer.getUniqueId();
		}

		//		// Get player info.
		//		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);
		//
		//		// Get current value of stat.
		//		int currentValue = 0;
		//
		//		// Check if it is valid!
		//		if (info.isValid()) {
		//			currentValue += info.getTotalValue();
		//		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(uuid, stat,
				StatzUtil.makeQuery("uuid", uuid.toString(), "value", 1));

	}
}
