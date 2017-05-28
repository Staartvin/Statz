package me.staartvin.statz.language;

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
    JOINS("statistic-joins", "Joined the server {0} times.", "Joined the server {0} times."),

    /**
     * Died {0} times on world '{1}'
     */
    DEATHS("statistic-deaths", "Died {0} times on world '{1}'.", "Died {0} times."),

    /**
     * Caught {0} {1} times on world '{2}'
     */
    ITEMS_CAUGHT("statistic-items-caught", "Caught {0} {1} times on world '{2}'.", "Caught {0} items."),

    /**
     * Caught {0} {1} times on world '{2}'
     */
    BLOCKS_PLACED("statistic-blocks-placed", "Placed {0} blocks of item id {1} and damage value {2} on world '{3}'.", "Placed {0} blocks."),

    /**
     * Broke {0} blocks of item id {1} and damage value {2} on world '{3}'.
     */
    BLOCKS_BROKEN("statistic-blocks-broken", "Broke {0} blocks of item id {1} and damage value {2} on world '{3}'.", "Broke {0} blocks."),

    /**
     * Killed {0} {1}s on world '{2}'.
     */
    KILLS_MOBS("statistic-kills-mobs", "Killed {0} {1}s on world '{2}'.", "Killed {0} mobs."),

    /**
     *Killed {0} {1} times on world '{2}'.
     */
    KILLS_PLAYERS("statistic-kills-players", "Killed {0} {1} times on world '{2}'.", "Killed {0} players."),

    /**
     * Played for {0} on world '{1}'.
     */
    TIME_PLAYED("statistic-time-played", "Played for {0} on world '{1}'.", "Played {0}."),

    /**
     * Eaten {0} {1} on world '{2}'.
     */
    FOOD_EATEN("statistic-food-eaten", "Eaten {0} {1} on world '{2}'.", "Ate {0} consumables."),

    /**
     * Took {0} points of damage by {1} on world '{2}'.
     */
    DAMAGE_TAKEN("statistic-damage-taken", "Took {0} points of damage by {1} on world '{2}'.", "Took {0} points of damage."),

    /**
     * Shorn {0} sheep on world '{1}'.
     */
    TIMES_SHORN("statistic-times-shorn", "Shorn {0} sheep on world '{1}'.", "Shorn {0} sheep."),

    /**
     * Travelled {0} blocks on world '{1}' by {2}.
     */
    DISTANCE_TRAVELLED("statistic-distance-travelled", "Travelled {0} blocks on world '{1}' by {2}.", "Travelled {0} blocks."),

    /**
     * Crafted {0} {1} times on world '{2}'.
     */
    ITEMS_CRAFTED("statistic-items-crafted", "Crafted {0} {1} times on world '{2}'.", "Crafted {0} items."),

    /**
     * Gained {0} points of xp on world '{1}'
     */
    XP_GAINED("statistic-xp-gained", "Gained {0} points of xp on world '{1}'.", "Gained {0} points of xp."),

    /**
     * Voted {0} times
     */
    VOTES("statistic-votes", "Voted {0} times.", "Voted {0} times."),

    /**
     * Shot {0} arrows with a force of {1} on world '{2}'.
     */
    ARROWS_SHOT("statistic-arrows-shot", "Shot {0} arrows with a force of {1} on world '{2}'.", "Shot {0} arrows."),

    /**
     * Slept {0} times in a bed on world '{1}'.
     */
    ENTERED_BEDS("statistic-entered-beds", "Slept {0} times in a bed on world '{1}'.", "Slept {0} times."),

    /**
     * Performed {0} {1} times on world '{2}'.
     */
    COMMANDS_PERFORMED("statistic-commands-performed", "Performed {0} {1} times on world '{2}'.", "Performed {0} commands."),

    /**
     * Kicked {0} times on world '{1}' with reason '{2}'.
     */
    TIMES_KICKED("statistic-times-kicked", "Kicked {0} times on world '{1}' with reason '{2}'.", "Kicked {0} times."),

    /**
     * Broken {0} {1} times on world '{2}'.
     */
    TOOLS_BROKEN("statistic-tools-broken", "Broken {0} {1} times on world '{2}'.", "Broken {0} tools."),

    /**
     * Thrown {0} eggs on world '{1}'.
     */
    EGGS_THROWN("statistic-eggs-thrown", "Thrown {0} eggs on world '{1}'.", "Thrown {0} eggs."),

    /**
     * Changed from {0} to {1} {2} times.
     */
    WORLDS_CHANGED("statistic-worlds-changed", "Changed from {0} to {1} {2} times.", "Changed worlds {0} times."),

    /**
     * Filled {0} buckets on world '{1}'.
     */
    BUCKETS_FILLED("statistic-buckets-filled", "Filled {0} buckets on world '{1}'.", "Filled {0} buckets."),

    /**
     * Emptied {0} buckets on world '{1}'.
     */
    BUCKETS_EMPTIED("statistic-buckets-emptied", "Emptied {0} buckets on world '{1}'.", "Emptied {0} buckets."),

    /**
     * Dropped {0} {1} times on world '{2}'.
     */
    ITEMS_DROPPED("statistic-items-dropped", "Dropped {0} {1} times on world '{2}'.", "Dropped {0} items."),

    /**
     * Picked up {0} {1} times on world '{2}'.
     */
    ITEMS_PICKED_UP("statistic-items-picked-up", "Picked up {0} {1} times on world '{2}'.", "Picked up {0} items."),

    /**
     * Teleported from {0} to {1} {2} times because of {3}.
     */
    TELEPORTS("statistic-teleports", "Teleported from {0} to {1} {2} times because of {3}.", "Teleported {0} times."),

    /**
     * Traded with {0} Villagers on world '{1}' for item {2}.
     */
    VILLAGER_TRADES("statistic-villager-trades", "Traded with {0} Villagers on world '{1}' for item {2}.", "Traded with {0} Villagers."),



    ;

    private static FileConfiguration LANG;

    /**
     * Set the {@code FileConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(final FileConfiguration config) {
        LANG = config;
    }

    private String path, highDetailDesc, totalDesc;

    public enum DescriptionDetail {HIGH, MEDIUM, LOW}

    /**
     * Lang enum constructor.
     *
     * @param path The string path.
     */
    StatisticDescription(final String path, String highDetailDesc, String totalDesc) {
        this.path = path;
        this.highDetailDesc = highDetailDesc;
        this.totalDesc = totalDesc;
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

        String value = ChatColor.translateAlternateColorCodes('&',  description);

        //ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, description));

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
     * Get the path to the string.
     *
     * @return The path to the string.
     */
    public String getPath() {
        return this.path;
    }
}
