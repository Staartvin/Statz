package me.staartvin.statz.gui;

import com.sun.org.apache.xpath.internal.SourceTree;
import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.language.DescriptionMatcher;
import me.staartvin.statz.util.StatzUtil;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * This class manages the creation and deletion of GUIs. Statz can use GUI's to show data about players.
 * <p>
 * Created by s150149 on 27-5-2017.
 */
public class GUIManager implements Listener {

    private Statz plugin;

    private DescriptionMatcher descriptionMatcher = new DescriptionMatcher();

    public GUIManager(Statz instance) {
        this.plugin = instance;

        // Register inv clicking listener.
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    public void showInventory(Player player, Inventory inv) {

        if (player == null) {
            throw new IllegalArgumentException("Player is null!");
        }

        if (inv == null) {
            throw new IllegalArgumentException("Inventory is null!");
        }

        player.openInventory(inv);
    }

    public Inventory getStatisticsListInventory(Player player) {

        Map<PlayerStat, PlayerInfo> data = getPlayerStatistics(player);

        int count = 0;

        Map<Integer, ItemStack> slots = new HashMap<>();

        for (Map.Entry<PlayerStat, PlayerInfo> entry : data.entrySet()) {

            PlayerStat statType = entry.getKey();
            PlayerInfo statInfo = entry.getValue();

            //System.out.println(count + ": " + statType);

            // Get icon of this stat type
            Material iconMaterial = statType.getIconMaterial();

            // Create an itemstack to show in the inventory
            ItemStack itemStack = new ItemStack(iconMaterial);
            itemStack.setAmount(1);

            String displayName = statType.getHumanFriendlyName();

            // Get item meta to add messages
            ItemMeta itemMeta = itemStack.getItemMeta();

            // Set display name to the human friendly name of this statistic
            itemMeta.setDisplayName(displayName);

            // Create a list of messages shown when hovering over the item
            List<String> messages = new ArrayList<>();

            List<Query> results = statInfo.getResults();

            if (results.isEmpty()) {
                messages.add(ChatColor.RED + "No information about you yet!");
                // Don't do anything when result is empty.
                continue;
            } else {

                String totalDescription = descriptionMatcher.getTotalDescription(statInfo, statType);

                if (totalDescription != null) {
                    messages.add(ChatColor.YELLOW + totalDescription);
                }

                if (statType != statType.JOINS && statType != statType.VOTES) {
                    messages.add("");
                    messages.add(ChatColor.RED + "Click me for more info!");
                }
            }

            itemMeta.setLore(messages);

            itemStack.setItemMeta(itemMeta);

            // Store in slots.
            slots.put(count, itemStack);

            //inv.setItem(count, itemStack);

            count++;
        }

        Inventory inv = Bukkit.createInventory(null, (slots.size() + 8) / 9 * 9, "Statistics of " + ChatColor.RED + player.getName());

        for (Map.Entry<Integer, ItemStack> entry : slots.entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue());
        }

        return inv;
    }

    public Inventory getSpecificStatisticInventory(Player player, PlayerStat statType) {

        PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), statType);

        List<Query> results = info.getResults();

        int invSize = (results.size() + 8) / 9 * 9 > 63 ? 63 : (results.size() + 8) / 9 * 9;

        Inventory inv = Bukkit.createInventory(null, invSize, "Statistics of " + ChatColor.RED + player.getName());

        int count = 0;

        for (Query query : results) {

            if (count >= invSize) {
                break;
            }

            //System.out.println(count + ": " + query);

            // Get icon of this stat type
            Material iconMaterial = statType.getIconMaterial();//randomEnum(Material.class);

            // Create an itemstack to show in the inventory
            ItemStack itemStack = new ItemStack(iconMaterial);
            itemStack.setAmount(1);

            String displayName = statType.getHumanFriendlyName();

            // Get item meta to add messages
            ItemMeta itemMeta = itemStack.getItemMeta();

            // Set display name to the human friendly name of this statistic
            itemMeta.setDisplayName(displayName);

            // Create a list of messages shown when hovering over the item
            List<String> messages = new ArrayList<>();

            String highDetailDescription = descriptionMatcher.getHighDetailDescription(query, statType);

            if (highDetailDescription != null) {
                messages.addAll(fitTextToScreen(highDetailDescription));
                //messages.add(ChatColor.YELLOW + highDetailDescription);
            }

            itemMeta.setLore(messages);

            itemStack.setItemMeta(itemMeta);

            inv.setItem(count, itemStack);

            count++;
        }

        return inv;
    }

    private Map<PlayerStat, PlayerInfo> getPlayerStatistics(Player player) {

        UUID uuid = player.getUniqueId();

        Map<PlayerStat, PlayerInfo> statistics = new HashMap<>();

        for (PlayerStat stat : PlayerStat.values()) {

            if (stat == PlayerStat.PLAYERS) {
                continue;
            }

            PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, stat);

            statistics.put(stat, info);
        }

        return statistics;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClickInvSlot(final InventoryClickEvent event) {

        Inventory inv = event.getClickedInventory();

        // Clicked outside inv window.
        if (inv == null) {
            return;
        }

        // Check if clicking a Statz GUI window
        if (!inv.getTitle().contains("Statistics of ")) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getSlot();

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) {
            return;
        }

        if (!clickedItem.hasItemMeta()) {
            return;
        }

        String displayName = clickedItem.getItemMeta().getDisplayName();

        if (displayName == null) {
            return;
        }

        PlayerStat statType = null;

        for (PlayerStat stat : PlayerStat.values()) {

            String humanName = stat.getHumanFriendlyName();

            if (humanName == null) {
                continue;
            }

            if (humanName.equalsIgnoreCase(displayName)) {
                statType = stat;
                break;
            }
        }

        // We couldn't find what stat the player clicked on.
        if (statType == null) {
            return;
        }

        boolean itemIsClickable = false;

        // Go through messages of item and see if it is clickable.
        for (String message : clickedItem.getItemMeta().getLore()) {
            if (message.contains("Click me for more info!")) {
                itemIsClickable = true;
            }
        }

        // If it is not clickable, ignore the click.
        if (!itemIsClickable) {
            return;
        }

        // Close currently opened inventory
        event.getWhoClicked().closeInventory();

        // Open stat specific inventory.
        Inventory specificInv = getSpecificStatisticInventory((Player) event.getWhoClicked(), statType);

        showInventory((Player) event.getWhoClicked(), specificInv);
    }

    private List<String> getStatisticMessages(PlayerStat stat, PlayerInfo info) {
        List<String> messages = new ArrayList<>();

        List<Query> results = info.getResults();

        for (Query q : results) {
            messages.add(StatzUtil.getInfoString(q, stat, "You"));
        }

        return messages;
    }

    private List<String> fitTextToScreen(String message) {
        int maxLength = 40; // x chars
        List<String> list = new ArrayList<>();

        int numberOfStrings = (int) Math.ceil(message.length() / (maxLength * 1.0));

        if (message.length() <= maxLength) {
            list.add(message);
            return list;
        }
//
//        System.out.println("Length of message: " + message.length());
//        System.out.println("Divided lenght: " + (message.length() / (maxLength * 1.0)));
//        System.out.println("CEIL: " + Math.ceil(message.length() / (maxLength * 1.0)));
//        System.out.println("Number of strings: " + numberOfStrings);
//        System.out.println("Original message:" + message);

        for (int i = 0; i < numberOfStrings; i++) {

            int beginIndex = i * maxLength;
            // Add max length to begin index, or length
            int endIndex = i * maxLength + maxLength > message.length() ? message.length() - 1: i * maxLength + maxLength;

            String subMessage = message.substring(beginIndex, endIndex);
            //System.out.println("MESSAGE " + i + ": " + subMessage);

            list.add(subMessage.trim());

        }

        return list;
    }


}
