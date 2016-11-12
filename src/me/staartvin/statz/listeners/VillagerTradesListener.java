package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class VillagerTradesListener implements Listener {

	private final Statz plugin;

	public VillagerTradesListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVillagerTrade(final InventoryClickEvent event) {

		final PlayerStat stat = PlayerStat.VILLAGER_TRADES;

		// Thanks to Lolmewn for this code (https://bitbucket.org/Lolmewn/stats/src/4eae2db1b21038a91b7d39181f27bdd3cd987324/src/main/java/nl/lolmewn/stats/stats/bukkit/BukkitTrades.java?at=3.0&fileviewer=file-view-default)

		if (event.getInventory().getType() != InventoryType.MERCHANT) {
			return;
		}
		if (!event.getSlotType().equals(SlotType.RESULT)) {
			return;
		}
		if (!event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)
				&& !event.getAction().equals(InventoryAction.PICKUP_ALL)) {
			return;
		}
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getWhoClicked();

		// Do general check
		if (!plugin.doGeneralCheck(player, stat))
			return;

		ItemStack item = event.getCurrentItem();

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", item.getAmount(), "world",
						player.getWorld().getName(), "trade", item.getType().toString()));

	}
}
