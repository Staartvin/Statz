package me.staartvin.statz.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class PlayerBlockPlaceListener implements Listener {

	private final Statz plugin;

	public PlayerBlockPlaceListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event) {

		final PlayerStat stat = PlayerStat.BLOCKS_PLACED;

		// Get player
		final Player player = event.getPlayer();

		Block blockPlaced = event.getBlockPlaced();

		final int typeId = blockPlaced.getTypeId();
		final int dataValue = blockPlaced.getData();
		final String worldName = blockPlaced.getWorld().getName();

		// Get player info.
		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("typeid", typeId, "datavalue", dataValue, "world", worldName));

		// Get current value of stat.
		int currentValue = 0;

		// Check if it is valid!
		if (info.isValid()) {
			currentValue += info.getTotalValue();
		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + 1), "typeid",
						typeId, "datavalue", dataValue, "world", worldName));

	}
}
