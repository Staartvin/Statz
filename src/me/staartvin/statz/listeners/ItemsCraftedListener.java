package me.staartvin.statz.listeners;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.datamanager.player.specification.ItemsCraftedSpecification;
import me.staartvin.statz.datamanager.player.specification.PlayerStatSpecification;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemsCraftedListener implements Listener {

    private final Statz plugin;

    public ItemsCraftedListener(final Statz plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(final CraftItemEvent event) {

        final PlayerStat stat = PlayerStat.ITEMS_CRAFTED;

        // Get player
        final Player player = (Player) event.getWhoClicked();

        // Do general check
        if (!plugin.doGeneralCheck(player, stat))
            return;

        final ItemStack itemCrafted = event.getCurrentItem();

        PlayerStatSpecification specification = new ItemsCraftedSpecification(player.getUniqueId(),
                itemCrafted.getAmount(), player.getWorld().getName(),
                itemCrafted.getType());

        // Update value to new stat.
        plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat, specification.constructQuery());

    }

    /**
     * Get the amount of items the player had just crafted.
     * This method will take into consideration shift clicking &
     * the amount of inventory space the player has left.
     *
     * @param e CraftItemEvent
     * @return int: actual crafted item amount
     * @author lewysryan (https://www.spigotmc
     * .org/threads/util-get-the-crafted-item-amount-from-a-craftitemevent.162952/)
     */
    private int getCraftAmount(CraftItemEvent e) {

        if (e.isCancelled()) {
            return 0;
        }

        Player p = (Player) e.getWhoClicked();

        if (e.isShiftClick()) {
            int itemsChecked = 0;
            int possibleCreations = 1;

            int amountCanBeMade = 0;

            for (ItemStack item : e.getInventory().getMatrix()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (itemsChecked == 0) {
                        possibleCreations = item.getAmount();
                        itemsChecked++;
                    } else {
                        possibleCreations = Math.min(possibleCreations, item.getAmount());
                    }
                }
            }

            int amountOfItems = e.getRecipe().getResult().getAmount() * possibleCreations;

            ItemStack i = e.getRecipe().getResult();

            for (int s = 0; s <= e.getInventory().getSize(); s++) {
                ItemStack test = p.getInventory().getItem(s);
                if (test == null || test.getType() == Material.AIR) {
                    amountCanBeMade += i.getMaxStackSize();
                    continue;
                }
                if (test.isSimilar(i)) {
                    amountCanBeMade += i.getMaxStackSize() - test.getAmount();
                }
            }

            return amountOfItems > amountCanBeMade ? amountCanBeMade : amountOfItems;
        } else {
            return e.getRecipe().getResult().getAmount();
        }
    }
}
