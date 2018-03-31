package me.staartvin.statz.importer;

import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.LibraryHook;
import me.staartvin.plugins.pluginlibrary.hooks.StatsHook;
import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.util.StatzUtil;
import nl.lolmewn.stats.api.stat.Stat;
import nl.lolmewn.stats.api.stat.StatEntry;
import nl.lolmewn.stats.api.user.StatsHolder;
import nl.lolmewn.stats.stats.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class can import data into Statz' database from other plugins. Currently supports Stats3
 * (https://dev.bukkit.org/bukkit-plugins/lolmewnstats/)
 *
 * @author Staartvin
 */
public class ImportManager {

    private Statz plugin;

    public ImportManager(Statz plugin) {
        this.plugin = plugin;
    }

    /**
     * Import data from Stats 3.
     *
     * @return number of entries imported from Stats 3.
     */
    @SuppressWarnings("deprecation")
    public int importFromStats3() {
        int importedEntries = 0;

        LibraryHook hook = plugin.getDependencyManager().getLibraryHook(Library.STATS);

        if (hook == null || !hook.isAvailable()) {
            plugin.getLogger().warning("Cannot import data from Stats 3 as it is not running!");

            return -1;
        }

        int waitingTime = 10;

        StatsHook stats3 = (StatsHook) hook;

        List<UUID> loggedPlayers = stats3.getLoggedPlayers();

        plugin.getLogsManager().writeToLogFile("Requested " + loggedPlayers.size() + " users from Stats. Now waiting " +
                "" + ((loggedPlayers.size() * waitingTime) / 1000.0) + " seconds for the response...");
        plugin.getLogger().info("Requested all users for importing, now wait " + ((loggedPlayers.size() * waitingTime) /
                1000.0) + " seconds for Stats to load all users.");

        try {
            Thread.sleep(loggedPlayers.size() * waitingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        plugin.getLogsManager().writeToLogFile("Started processing UUIDs of Stats.");

        for (UUID uuid : loggedPlayers) {

            StatsHolder user = stats3.getStatsHolder(uuid);

            importedEntries++;

            if (importedEntries % 1000 == 0) {
                plugin.getLogsManager().writeToLogFile("Processed " + importedEntries + " / " + loggedPlayers.size()
                        + " uuids.");
            }

            Collection<Stat> storedStats = user.getStats();

            for (Stat stat : storedStats) {
                for (StatEntry entry : user.getStats(stat)) {

                    Map<String, Object> metadata = entry.getMetadata();

                    double value = entry.getValue();
                    String worldName = (String) metadata.get("world");

                    if (stat instanceof Move) {
                        // Movement stat

                        double moveType = Double.parseDouble(metadata.get("type").toString());

                        String movementType = "WALK";

                        switch ((int) moveType) {
                            case 0:
                                movementType = "WALK";
                                break;
                            case 1:
                                movementType = "BOAT";
                                break;
                            case 2:
                                movementType = "MINECART";
                                break;
                            case 3:
                                movementType = "PIG";
                                break;
                            case 4:
                                movementType = "PIG IN MINECART";
                                break;
                            case 5:
                                movementType = "HORSE";
                                break;
                            case 6:
                                movementType = "FLY";
                                break;
                        }

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.DISTANCE_TRAVELLED,
                                StatzUtil.makeQuery("value", (value), "moveType", movementType, "world", worldName));

                    } else if (stat instanceof Kill) {
                        // Kill stat

                        EntityType entity = EntityType.fromName((String) metadata.get("entityType"));

                        String entityName = entity != null ? entity.toString() : "UNKNOWN";

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.KILLS_MOBS,
                                StatzUtil.makeQuery("value", (value), "mob", entityName, "world", worldName,
                                        "weapon", "UNKNOWN"));

                    } else if (stat instanceof Teleports) {
                        // Teleport stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TELEPORTS, StatzUtil.makeQuery("value",
                                (value), "world", "UNKNOWN", "destWorld", worldName, "cause", "UNKNOWN"));

                    } else if (stat instanceof Arrows) {
                        // Arrows shot stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ARROWS_SHOT,
                                StatzUtil.makeQuery("value", (value), "world", worldName, "forceShot", 1));

                    } else if (stat instanceof BedEnter) {
                        // Beds entered stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ENTERED_BEDS,
                                StatzUtil.makeQuery("value", (value), "world", worldName));

                    } else if (stat instanceof BlockBreak) {
                        // Blocks broken stat

                        int dataValue = Integer.parseInt(metadata.get("data").toString());
                        String blockName = (String) metadata.get("name");

                        Material material = Material.getMaterial(blockName);

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.BLOCKS_BROKEN,
                                StatzUtil.makeQuery("value", (value), "world", worldName, "datavalue", dataValue,
                                        "typeid", material.getId()));

                    } else if (stat instanceof BlockPlace) {
                        // Blocks placed stat

                        int dataValue = Integer.parseInt(metadata.get("data").toString());
                        String blockName = (String) metadata.get("name");

                        Material material = Material.getMaterial(blockName);

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.BLOCKS_PLACED,
                                StatzUtil.makeQuery("value", (value), "world", worldName, "datavalue", dataValue,
                                        "typeid", material.getId()));

                    } else if (stat instanceof BucketEmpty) {
                        // Buckets emptied stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.BUCKETS_EMPTIED,
                                StatzUtil.makeQuery("value", (value), "world", worldName));

                    } else if (stat instanceof BucketFill) {
                        // Buckets filled stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.BUCKETS_FILLED,
                                StatzUtil.makeQuery("value", (value), "world", worldName));

                    } else if (stat instanceof CommandsDone) {
                        // Commands performed stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.COMMANDS_PERFORMED, StatzUtil.makeQuery(
                                "value", (value), "world", worldName, "command", "UNKNOWN", "arguments", "UNKNOWN"));

                    } else if (stat instanceof DamageTaken) {
                        // Damage taken stat

                        String cause = metadata == null ? "UNKNOWN" : (metadata.get("cause") != null ? metadata.get
                                ("cause").toString() : "UNKNOWN");

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.DAMAGE_TAKEN, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "cause", cause));

                    } else if (stat instanceof Death) {
                        // Times died stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.DEATHS, StatzUtil
                                .makeQuery("value", (value), "world", worldName));

                    } else if (stat instanceof EggsThrown) {
                        // Eggs thrown stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.EGGS_THROWN, StatzUtil
                                .makeQuery("value", (value), "world", worldName));

                    } else if (stat instanceof FishCaught) {
                        // Fish caught stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_CAUGHT, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "caught", "UNKNOWN"));

                    } else if (stat instanceof ItemsCrafted) {
                        // Items crafted stat

                        String name = metadata == null ? "UNKNOWN" : (metadata.get("name") != null ? metadata.get
                                ("name").toString() : "UNKNOWN");

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_CRAFTED, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "item", name));

                    } else if (stat instanceof ItemsDropped) {
                        // Items dropped stat

                        String name = metadata == null ? "UNKNOWN" : (metadata.get("name") != null ? metadata.get
                                ("name").toString() : "UNKNOWN");

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_DROPPED, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "item", name));

                    } else if (stat instanceof ItemsPickedUp) {
                        // Items picked up stat

                        String name = metadata == null ? "UNKNOWN" : (metadata.get("name") != null ? metadata.get
                                ("name").toString() : "UNKNOWN");

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_PICKED_UP, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "item", name));

                    } else if (stat instanceof ItemsDropped) {
                        // Items dropped stat

                        String name = metadata == null ? "UNKNOWN" : (metadata.get("name") != null ? metadata.get
                                ("name").toString() : "UNKNOWN");

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_DROPPED, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "item", name));

                    } else if (stat instanceof Joins) {
                        // Times joined stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.JOINS, StatzUtil
                                .makeQuery("value", (value)));

                    } else if (stat instanceof Omnomnom) {
                        // Food eaten stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.FOOD_EATEN, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "foodEaten", "UNKNOWN"));

                    } else if (stat instanceof Playtime) {
                        // Time played stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TIME_PLAYED, StatzUtil
                                .makeQuery("value", (value / 60d
                                        /* Stats records in seconds, Statz does it in minutes */), "world", worldName));

                    } else if (stat instanceof PVP) {
                        // Number of players killed stat
                        String victim = metadata == null ? "UNKNOWN" : (metadata.get("victim") != null ? metadata.get
                                ("victim").toString() : "UNKNOWN");

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.KILLS_PLAYERS, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "playerKilled", victim));

                    } else if (stat instanceof Shears) {
                        // Number of sheep shorn stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TIMES_SHORN, StatzUtil
                                .makeQuery("value", (value), "world", worldName));

                    } else if (stat instanceof WorldChanged) {
                        // Times changed of worlds stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.WORLDS_CHANGED, StatzUtil
                                .makeQuery("value", (value), "world", "UNKNOWN", "destWorld", "UNKNOWN"));

                    } else if (stat instanceof TimesKicked) {
                        // Times kicked stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TIMES_KICKED, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "reason", "UNKNOWN"));

                    } else if (stat instanceof ToolsBroken) {
                        // Tools broken stat

                        String name = metadata == null ? "UNKNOWN" : (metadata.get("name") != null ? metadata.get
                                ("name").toString() : "UNKNOWN");

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TOOLS_BROKEN, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "item", name));

                    } else if (stat instanceof Trades) {
                        // Number of trades made stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.VILLAGER_TRADES, StatzUtil
                                .makeQuery("value", (value), "world", worldName, "trade", "UNKNOWN"));

                    } else if (stat instanceof XpGained) {
                        // XP gained stat

                        plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.XP_GAINED, StatzUtil
                                .makeQuery("value", (value), "world", worldName));

                    }

                }

            }
        }

        return importedEntries;
    }

}
