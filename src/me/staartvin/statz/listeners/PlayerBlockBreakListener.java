package me.staartvin.statz.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class PlayerBlockBreakListener implements Listener {

	private final Statz plugin;

	public PlayerBlockBreakListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event) {

		final PlayerStat stat = PlayerStat.BLOCKS_BROKEN;

		// Get player
		final Player player = event.getPlayer();

		Block blockBroken = event.getBlock();

		final int typeId = blockBroken.getTypeId();
		final int dataValue = blockBroken.getData();
		final String worldName = blockBroken.getWorld().getName();

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				// Get player info.
				final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat, StatzUtil.makeQuery("typeid", typeId, "datavalue", dataValue, "world", worldName));

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
		});
		
		

	}
}
