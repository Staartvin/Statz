package me.staartvin.statz.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
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

		int typeId = blockBroken.getTypeId();
		int dataValue = blockBroken.getData();
		String worldName = blockBroken.getWorld().getName();

		// Get player info.
		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

		// Get current value of stat.
		int currentValue = 0;

		// Check if it is valid!
		if (info.isValid()) {
			for (Query map : info.getResults()) {
				if (map.getValue("typeid") != null && map.getValue("typeid").toString().equalsIgnoreCase(typeId + "")
						&& map.getValue("datavalue") != null
						&& map.getValue("datavalue").toString().equalsIgnoreCase(dataValue + "") && map.getValue("world") != null
						&& map.getValue("world").toString().equalsIgnoreCase(worldName)) {
					currentValue += Double.parseDouble(map.getValue("value").toString());
				}
			}
		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + 1), "typeid",
						typeId, "datavalue", dataValue, "world", worldName));

	}
}
