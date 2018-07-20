package me.staartvin.statz.language;

import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Every enumeration value has its path and default value.
 * To get the path, do {@link #getPath()}.
 * To get the default value, do {@link #getDefault()}.
 * <p>
 * For the defined value in the lang.yml config, use
 * {@link #getConfigValue(Object...)}.
 * String objects are expected as input.
 *
 * @author Staartvin and gomeow
 */
public enum StatisticDescription {

    /**
     * Joined the server {0} times.
     */
    JOINS(PlayerStat.JOINS, "Joined the server {0} times.", "&3Joined the server &2{0}&3 times."),

    /**
     * Died {0} times on world '{1}'
     */
    DEATHS(PlayerStat.DEATHS, "Died {0} times on world '{1}'.", "&3Died &2{0}&3 times."),

    /**
     * Caught {0} {1} times on world '{2}'
     */
    ITEMS_CAUGHT(PlayerStat.ITEMS_CAUGHT, "Caught {0} {1} times on world '{2}'.", "&3Caught" +
            " &2{0}&3 " + "items."),

    /**
     * Placed {0} blocks of {1} on world '{1}'
     */
    BLOCKS_PLACED(PlayerStat.BLOCKS_PLACED, "Placed {0} blocks of {1} on " +
            "world '{2}'.", "&3Placed &2{0}&3 blocks."),

    /**
     * Broke {0} blocks of {1} on world '{2}'.
     */
    BLOCKS_BROKEN(PlayerStat.BLOCKS_BROKEN, "Broke {0} blocks of {1} on " +
            "world " + "'{2}'.", "&3Broke &2{0}&3 blocks."),

    /**
     * Killed {0} {1}s on world '{2}'.
     */
    KILLS_MOBS(PlayerStat.KILLS_MOBS, "Killed {0} {1}s on world '{2}'.", "&3Killed &2{0}&3 " +
            "mobs."),

    /**
     * Killed {0} {1} times on world '{2}'.
     */
    KILLS_PLAYERS(PlayerStat.KILLS_PLAYERS, "Killed {0} {1} times on world '{2}'.",
            "&3Killed &2{0}&3" +
                    " players."),

    /**
     * Played for {0} on world '{1}'.
     */
    TIME_PLAYED(PlayerStat.TIME_PLAYED, "Played for {0} on world '{1}'.", "&3Played &2{0}&3."),

    /**
     * Eaten {0} {1} on world '{2}'.
     */
    FOOD_EATEN(PlayerStat.FOOD_EATEN, "Eaten {0} {1} on world '{2}'.", "&3Ate &2{0}&3 " +
            "consumables."),

    /**
     * Took {0} points of damage by {1} on world '{2}'.
     */
    DAMAGE_TAKEN(PlayerStat.DAMAGE_TAKEN, "Took {0} points of damage by {1} on world '{2}'" +
            ".", "&3Took " +
            "&2{0}&3 " +
            "points of damage."),

    /**
     * Shorn {0} sheep on world '{1}'.
     */
    TIMES_SHORN(PlayerStat.TIMES_SHORN, "Shorn {0} sheep on world '{1}'.", "&3Shorn &2{0}&3 " +
            "sheep."),

    /**
     * Travelled {0} blocks on world '{1}' by {2}.
     */
    DISTANCE_TRAVELLED(PlayerStat.DISTANCE_TRAVELLED, "Travelled {0} blocks on world " +
            "'{1}' by {2}.",
            "&3Travelled " +
                    "&2{0}&3 blocks."),

    /**
     * Crafted {0} {1} times on world '{2}'.
     */
    ITEMS_CRAFTED(PlayerStat.ITEMS_CRAFTED, "Crafted {0} {1} times on world '{2}'.",
            "&3Crafted " +
                    "&2{0}&3 items."),

    /**
     * Gained {0} points of xp on world '{1}'
     */
    XP_GAINED(PlayerStat.XP_GAINED, "Gained {0} points of xp on world '{1}'.", "&3Gained " +
            "&2{0}&3 " +
            "points of xp" +
            "."),

    /**
     * Voted {0} times
     */
    VOTES(PlayerStat.VOTES, "Voted {0} times.", "&3Voted &2{0}&3 times."),

    /**
     * Shot {0} arrows with a force of {1} on world '{2}'.
     */
    ARROWS_SHOT(PlayerStat.ARROWS_SHOT, "Shot {0} arrows with a force of {1} on world '{2}'" +
            ".", "&3Shot " +
            "&2{0}&3 " +
            "arrows."),

    /**
     * Slept {0} times in a bed on world '{1}'.
     */
    ENTERED_BEDS(PlayerStat.ENTERED_BEDS, "Slept {0} times in a bed on world '{1}'.",
            "&3Slept &2{0}&3 " +
                    "times."),

    /**
     * Performed {0} {1} times on world '{2}'.
     */
    COMMANDS_PERFORMED(PlayerStat.COMMANDS_PERFORMED, "Performed {0} {1} times on " +
            "world '{2}'.",
            "&3Performed " +
                    "&2" +
                    "{0}&3 commands."),

    /**
     * Kicked {0} times on world '{1}' with reason '{2}'.
     */
    TIMES_KICKED(PlayerStat.TIMES_KICKED, "Kicked {0} times on world '{1}' with reason " +
            "'{2}'.",
            "&3Kicked " +
                    "&2{0}&3 " +
                    "times."),

    /**
     * Broken {0} {1} times on world '{2}'.
     */
    TOOLS_BROKEN(PlayerStat.TOOLS_BROKEN, "Broken {0} {1} times on world '{2}'.",
            "&3Broken " +
                    "&2{0}&3 tools."),

    /**
     * Thrown {0} eggs on world '{1}'.
     */
    EGGS_THROWN(PlayerStat.EGGS_THROWN, "Thrown {0} eggs on world '{1}'.", "&3Thrown &2{0}&3" +
            " eggs."),

    /**
     * Changed from {0} to {1} {2} times.
     */
    WORLDS_CHANGED(PlayerStat.WORLDS_CHANGED, "Changed from {0} to {1} {2} times.",
            "&3Changed worlds " +
                    "&2{0}&3 times."),

    /**
     * Filled {0} buckets on world '{1}'.
     */
    BUCKETS_FILLED(PlayerStat.BUCKETS_FILLED, "Filled {0} buckets on world '{1}'.",
            "&3Filled &2{0}&3 " +
                    "buckets."),

    /**
     * Emptied {0} buckets on world '{1}'.
     */
    BUCKETS_EMPTIED(PlayerStat.BUCKETS_EMPTIED, "Emptied {0} buckets on world '{1}'.",
            "&3Emptied " +
                    "&2{0}&3 " +
                    "buckets."),

    /**
     * Dropped {0} {1} times on world '{2}'.
     */
    ITEMS_DROPPED(PlayerStat.ITEMS_DROPPED, "Dropped {0} {1} times on world '{2}'.",
            "&3Dropped " +
                    "&2{0}&3 " +
                    "items."),

    /**
     * Picked up {0} {1} times on world '{2}'.
     */
    ITEMS_PICKED_UP(PlayerStat.ITEMS_PICKED_UP, "Picked up {0} {1} times on world '{2}'" +
            ".", "&3Picked " +
            "up " +
            "&2{0}&3 " +
            "items."),

    /**
     * Teleported from {0} to {1} {2} times because of {3}.
     */
    TELEPORTS(PlayerStat.TELEPORTS, "Teleported from {0} to {1} {2} times because of {3}.",
            "&3Teleported " +
                    "&2{0}&3 " +
                    "times."),

    /**
     * Traded with {0} Villagers on world '{1}' for item {2}.
     */
    VILLAGER_TRADES(PlayerStat.VILLAGER_TRADES, "Traded with {0} Villagers on world " +
            "'{1}' for item {2}" +
            ".",
            "&3Traded " +
                    "with &2{0}&3 Villagers."),;

    private static FileConfiguration file;
    private String highDetailDesc, totalDesc;
    private PlayerStat relatedPlayerStat;

    /**
     * Statistic enum constructor.
     */
    StatisticDescription(PlayerStat correspondingStat, String highDetailDesc, String totalDesc) {
        this.highDetailDesc = highDetailDesc;
        this.totalDesc = totalDesc;
        this.relatedPlayerStat = correspondingStat;
    }

    /**
     * Set the {@code FileConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(final FileConfiguration config) {
        file = config;
    }

    /**
     * Get the value in the config with certain arguments.
     *
     * @param args arguments that need to be given. (Can be null)
     * @return value in config or otherwise default value
     */
    private String getConfigValue(String description, final Object... args) {

        if (description == null) {
            return null;
        }

        String value = ChatColor.translateAlternateColorCodes('&', description);

        if (args == null)
            return value;
        else {
            if (args.length == 0)
                return value;

            for (int i = 0; i < args.length; i++) {
                value = value.replace("{" + i + "}", args[i].toString());
            }
        }

        return value;
    }

    /**
     * Get the default value of the path.
     *
     * @return The default value of the path.
     */
    public String getHighDetailDescription(final Object... args) {
        return getConfigValue(highDetailDesc, args);
    }

    public String getTotalDescription(final Object... args) {
        return getConfigValue(totalDesc, args);
    }

    /**
     * Get the PlayerStat enum that is related to this statistic description.
     *
     * @return {@link PlayerStat}
     */
    public PlayerStat getRelatedPlayerStat() {
        return this.relatedPlayerStat;
    }

    public String getStringIdentifier() {
        return relatedPlayerStat.getTableName();
    }

    public enum DescriptionDetail {HIGH, MEDIUM, LOW}
}
