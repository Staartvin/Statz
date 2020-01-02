package me.staartvin.statz.gui;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.gui.inventory.ItemBuilder;
import me.staartvin.statz.gui.inventory.buttons.GUIButton;
import me.staartvin.statz.gui.inventory.types.PaginatedGUI;
import me.staartvin.statz.language.DescriptionMatcher;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
    private String inventoryTitle = "Statistics of ";

    public GUIManager(Statz instance) {
        this.plugin = instance;

        PaginatedGUI.prepare(instance);

        // Register inv clicking listener.
//        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    /**
     * Show an inventory to the given player.
     *
     * @param viewer Player to show the inventory to
     * @param inv    Inventory to show
     * @throws IllegalArgumentException When player or inventory is null
     */
    public void showInventory(Player viewer, Inventory inv) {

        if (viewer == null) {
            throw new IllegalArgumentException("Player is null!");
        }

        if (inv == null) {
            throw new IllegalArgumentException("Inventory is null!");
        }

        viewer.openInventory(inv);
    }

    /**
     * Get an inventory that represents the graphical form of /statz list.
     * It includes all data that has been recorded by Statz grouped per stat.
     *
     * @param uuid       UUID of the player to check
     * @param playerName Name of the player to check
     * @return An inventory that shows the data of the given player.
     */
    public Inventory getStatisticsListInventory(UUID uuid, String playerName) {

        PlayerInfo data = plugin.getDataManager().getPlayerInfo(uuid);

        if (data == null) {
            data = plugin.getDataManager().loadPlayerData(uuid);
        }

        int count = 0;

        Map<Integer, ItemStack> slots = new HashMap<>();

        for (PlayerStat statType : data.getStatistics()) {

            // Skip Players statistic
            if (statType == PlayerStat.PLAYERS) {
                continue;
            }

            List<Query> storedRows = data.getRows(statType);

            // Get icon of this stat type
            Material iconMaterial = plugin.getStatisticDescriptionConfig().getIconMaterial(statType);

            // Create an itemstack to show in the inventory
            ItemStack itemStack = new ItemStack(iconMaterial);
            itemStack.setAmount(1);

            String displayName = plugin.getStatisticDescriptionConfig().getHumanFriendlyTitle(statType);

            // Get item meta to add messages
            ItemMeta itemMeta = itemStack.getItemMeta();

            // Set display name to the human friendly name of this statistic
            itemMeta.setDisplayName(displayName);

            // Create a list of messages shown when hovering over the item
            List<String> messages = new ArrayList<>();

            if (storedRows == null || storedRows.isEmpty()) {
                messages.add(ChatColor.RED + "No information about " + playerName + "!");
                // Don't do anything when result is empty.
                continue;
            } else {

                // Create temp playerinfo object to obtain description
                PlayerInfo tempInfo = new PlayerInfo(uuid);
                tempInfo.setData(statType, storedRows);

                String totalDescription = ChatColor.stripColor(DescriptionMatcher.getTotalDescription(tempInfo,
                        statType));

                if (totalDescription != null) {
                    messages.add(ChatColor.YELLOW + totalDescription);
                }

                if (statType != PlayerStat.JOINS && statType != PlayerStat.VOTES) {
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

        Inventory inv = Bukkit.createInventory(null, (slots.size() + 8) / 9 * 9,
                inventoryTitle + ChatColor.RED + playerName);

        for (Map.Entry<Integer, ItemStack> entry : slots.entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue());
        }

        return inv;
    }

    /**
     * Much like {@link #getSpecificStatisticInventory(UUID, PlayerStat, String)}}, this method provides an inventory
     * that shows the data of a specific stat type.
     *
     * @param uuid       UUID of the player to view
     * @param statType   Type of stat to view
     * @param playerName Name of the player to view
     * @return an inventory that represents the data Statz has collected of the given player.
     */
    public Inventory getSpecificStatisticInventory(UUID uuid, PlayerStat statType, String playerName) {

        PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

        List<Query> results = info.getDataOfPlayerStat(statType);

        int invSize = Math.min((results.size() + 8) / 9 * 9, 63);

        Inventory inv = Bukkit.createInventory(null, invSize, inventoryTitle + ChatColor.RED + playerName);

        int count = 0;

        for (Query query : results) {

            if (count >= invSize) {
                break;
            }

            // Get icon of this stat type
            Material iconMaterial = plugin.getStatisticDescriptionConfig().getIconMaterial(statType);

            // Create an itemstack to show in the inventory
            ItemStack itemStack = new ItemStack(iconMaterial);
            itemStack.setAmount(1);

            String displayName = plugin.getStatisticDescriptionConfig().getHumanFriendlyTitle(statType);

            // Get item meta to add messages
            ItemMeta itemMeta = itemStack.getItemMeta();

            // Set display name to the human friendly name of this statistic
            itemMeta.setDisplayName(displayName);

            // Create a list of messages shown when hovering over the item
            List<String> messages = new ArrayList<>();

            String highDetailDescription = ChatColor.stripColor(DescriptionMatcher.getHighDetailDescription(query,
                    statType));

            if (highDetailDescription != null) {
                messages.addAll(fitTextToScreen(highDetailDescription));
            }

            itemMeta.setLore(messages);

            itemStack.setItemMeta(itemMeta);

            inv.setItem(count, itemStack);

            count++;
        }

        return inv;
    }

    /**
     * Get all statistics of a player, except the players table.
     *
     * @param uuid UUID of the player
     * @return all PlayerInfo per stat type of the given player.
     */
    private Map<PlayerStat, PlayerInfo> getPlayerStatistics(UUID uuid) {

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

    /**
     * Redirect Player to correct inventory if he/she clicks on an ItemStack.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClickInvSlot(final InventoryClickEvent event) {

        Inventory inv = event.getClickedInventory();

        // Clicked outside inv window.
        if (inv == null) {
            return;
        }

        // Check if clicking a Statz GUI window
        if (!event.getView().getTitle().contains(inventoryTitle)) {
            return;
        }

        String targetPlayer = ChatColor.stripColor(event.getView().getTitle().replace(inventoryTitle, "").trim());

        // Check if we can find the target player of this inventory.
        if (targetPlayer == null || targetPlayer.equalsIgnoreCase("")) {
            return;
        }

        event.setCancelled(true);

        // When player clicks with right mouse button, it indicates they want to back.
        if (event.isRightClick()) {
            event.getWhoClicked().closeInventory();

            // Send the player back to the main overview inventory.

            // Open the general inventory that shows all stats.
            Inventory generalInv = this.getStatisticsListInventory(event.getWhoClicked().getUniqueId(), targetPlayer);

            showInventory((Player) event.getWhoClicked(), generalInv);

            return;
        }

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

            String humanName = plugin.getStatisticDescriptionConfig().getHumanFriendlyTitle(stat);

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

        // Get UUID of target player
        UUID targetUUID;

        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(targetPlayer);

        if (offlinePlayer == null || offlinePlayer.getUniqueId() == null) {
            return;
        }

        targetUUID = offlinePlayer.getUniqueId();

        // Close currently opened inventory
        event.getWhoClicked().closeInventory();

        // Open stat specific inventory.
        Inventory specificInv = getSpecificStatisticInventory(targetUUID, statType, targetPlayer);

        showInventory((Player) event.getWhoClicked(), specificInv);
    }

    private List<String> getStatisticMessages(PlayerStat stat, PlayerInfo info) {
        List<String> messages = new ArrayList<>();

        List<Query> results = info.getDataOfPlayerStat(stat);

        for (Query q : results) {
            messages.add(StatzUtil.getInfoString(q, stat, "You"));
        }

        return messages;
    }

    /**
     * Convert a string into substrings where each substring can only have up to 40 characters.
     * This method will split the original string into different strings based on the words. If a word does not fit
     * on a line, a new line is created.
     *
     * @param message Original message to fit.
     * @return A list of strings with the original message split over different lines.
     */
    private List<String> fitTextToScreen(String message) {
        int maxLength = 40; // x chars
        List<String> list = new ArrayList<>();

        String[] words = message.split(" ");

        StringBuilder line = new StringBuilder();

        for (String word : words) {
            // Check if line with extra word is longer than max length
            if (line.length() + (word).length() <= maxLength) {
                line.append(word).append(" ");
            } else { // It is longer, so create a new line instead.
                // Add previous line to list
                list.add(line.toString());

                // Create a new empty line
                line = new StringBuilder(word + " ");
            }
        }

        // Add the last line as well
        list.add(line.toString());

        return list;
    }

    /**
     * Show a GUI that shows all statistics. Each statistic is shown by an item and is able to be clicked by the player.
     *
     * @param inventoryViewer Player that the GUI should be shown to.
     * @param targetUUID      UUID of the player we should show the statistics for. If null, the uuid of the
     *                        player parameter will be used.
     */
    public void showStatisticsOverviewInventory(Player inventoryViewer, UUID targetUUID, String targetPlayerName) {

        PaginatedGUI menu = new PaginatedGUI(this.inventoryTitle + targetPlayerName);

        PlayerInfo data = plugin.getDataManager().getPlayerInfo(targetUUID);

        if (data == null) {
            data = plugin.getDataManager().loadPlayerData(targetUUID);
        }

        int count = 0;

        List<PlayerStat> statistics = data.getStatistics();

        // Sort so we always have the same order.
        statistics.sort(Comparator.comparing(Enum::toString));

        for (PlayerStat statType : statistics) {

            // Skip Players statistic
            if (statType == PlayerStat.PLAYERS) {
                continue;
            }

            List<Query> storedRows = data.getRows(statType);

            boolean hasStoredInfo = storedRows != null && !storedRows.isEmpty();

            // Only show statistics that have data.
            if (!hasStoredInfo) continue;

            // Get icon of this stat type
            Material iconMaterial = plugin.getStatisticDescriptionConfig().getIconMaterial(statType);

            // Find display name of this item.
            String displayName = plugin.getStatisticDescriptionConfig().getHumanFriendlyTitle(statType);

            // Create a list of messages shown when hovering over the item
            List<String> messages = new ArrayList<>();


            // Create temp playerinfo object to obtain description
            PlayerInfo tempInfo = new PlayerInfo(targetUUID);
            tempInfo.setData(statType, storedRows);

            String totalDescription = ChatColor.stripColor(DescriptionMatcher.getTotalDescription(tempInfo,
                    statType));

            if (totalDescription != null) {
                messages.add(ChatColor.YELLOW + totalDescription);
            }

            if (statType != PlayerStat.JOINS && statType != PlayerStat.VOTES) {
                messages.add("");
                messages.add(ChatColor.RED + "Click me for more info!");
            }

            // Create an itemstack to show in the inventory
            GUIButton button = new GUIButton(
                    ItemBuilder.start(iconMaterial).name(displayName).lore(messages).build()
            );

            // Add a listener to each button.
            button.setListener(event -> {
                // When a button has been pressed, open the inventory that shows a specific statistic.

                event.setCancelled(true);
                event.getWhoClicked().closeInventory();

                this.showSpecificStatisticInventory(inventoryViewer, targetUUID, targetPlayerName, statType);
            });

            int position = 1 + 2 * (count % 4) + 9 * (count / 4);

            menu.setButton(position, button);

            count++;
        }

        this.showInventory(inventoryViewer, menu.getInventory());
    }

    /**
     * Show a GUI for a specific statistic.
     *
     * @param inventoryViewer Player that the GUI should be shown to.
     * @param targetUUID      UUID of the player we should show the statistics for. If null, the uuid of the
     *                        player parameter will be used.
     * @param statistic       Statistic that should be shown.
     */
    public void showSpecificStatisticInventory(Player inventoryViewer, UUID targetUUID,
                                               String targetPlayerName, PlayerStat statistic) {


        PaginatedGUI menu = new PaginatedGUI(this.inventoryTitle + targetPlayerName);

        PlayerInfo data = plugin.getDataManager().getPlayerInfo(targetUUID, statistic);

        if (data == null) {
            data = plugin.getDataManager().loadPlayerData(targetUUID);
        }

        List<Query> results = data.getRows();

        int count = 0;

        List<PlayerStat> statistics = data.getStatistics();

        // Sort so we always have the same order.
        statistics.sort(Comparator.comparing(Enum::toString));

        for (Query query : results) {

            // Skip Players statistic
            if (query == null) {
                continue;
            }

            // Get icon of this stat type
            Material iconMaterial = this.getIconMaterialForSpecificStatistic(query, statistic);

            if (statistic == PlayerStat.BLOCKS_BROKEN || statistic == PlayerStat.BLOCKS_PLACED) {
                System.out.println("Block: " + query.getValue("block") + " translates to " + Material.getMaterial(query.getValue("block").toString()));
            }

            // Find display name of this item.
            String displayName = plugin.getStatisticDescriptionConfig().getHumanFriendlyTitle(statistic);

            // Create a list of messages shown when hovering over the item
            List<String> messages = new ArrayList<>();

            String highDetailDescription = ChatColor.stripColor(DescriptionMatcher.getHighDetailDescription(query,
                    statistic));

            if (highDetailDescription != null) {
                messages.addAll(fitTextToScreen(highDetailDescription));
            }

            if (statistic != PlayerStat.JOINS && statistic != PlayerStat.VOTES) {
                messages.add("");
                messages.add(ChatColor.RED + "Click me to go back!");
            }

            // Create an itemstack to show in the inventory
            GUIButton button = new GUIButton(
                    ItemBuilder.start(iconMaterial).name(displayName).lore(messages).build()
            );

            // Add a listener to each button.
            button.setListener(event -> {
                // When a button has been pressed, don't give them the item.
                event.setCancelled(true);

                // Send them back to the general overview.
                this.showStatisticsOverviewInventory(inventoryViewer, targetUUID, targetPlayerName);
            });

            int position = 1 + 2 * (count % 4) + 9 * (count / 4);

            menu.setButton(position, button);

            count++;
        }

        this.showInventory(inventoryViewer, menu.getInventory());
    }

    private Material getIconMaterialForSpecificStatistic(Query query, PlayerStat stat) {

        if (stat == PlayerStat.BLOCKS_BROKEN || stat == PlayerStat.BLOCKS_PLACED) {
            return Material.getMaterial(query.getValue("block").toString());
        }

        if (stat == PlayerStat.VILLAGER_TRADES) {
            return Material.getMaterial(query.getValue("trade").toString());
        }

        if (stat == PlayerStat.KILLS_MOBS) {
            return Material.getMaterial(query.getValue("weapon").toString());
        }

        if (stat == PlayerStat.ITEMS_PICKED_UP || stat == PlayerStat.ITEMS_DROPPED || stat == PlayerStat.ITEMS_CRAFTED || stat == PlayerStat.TOOLS_BROKEN) {
            return Material.getMaterial(query.getValue("item").toString());
        }

        if (stat == PlayerStat.ITEMS_CAUGHT) {
            return Material.getMaterial(query.getValue("caught").toString());
        }

        if (stat == PlayerStat.FOOD_EATEN) {
            return Material.getMaterial(query.getValue("foodEaten").toString());
        }

        if (stat == PlayerStat.DISTANCE_TRAVELLED) {
            String movementType = query.getValue("moveType").toString();

            switch (movementType) {
                case "SWIM":
                    return Material.TROPICAL_FISH;
                case "FLY":
                    return Material.BLAZE_POWDER;
                case "BOAT":
                    return Material.OAK_BOAT;
                case "MINECART":
                case "HORSE IN MINECART":
                    return Material.MINECART;
                case "PIG IN MINECART":
                case "PIG":
                    return Material.COOKED_PORKCHOP;
                case "HORSE":
                    return Material.DIAMOND_HORSE_ARMOR;
                case "FLY WITH ELYTRA":
                    return Material.ELYTRA;
                case "WALK":
                    return Material.GOLDEN_BOOTS;
            }
        }


        return plugin.getStatisticDescriptionConfig().getIconMaterial(stat);
    }


}
