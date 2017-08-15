package me.staartvin.statz.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;

public class ConfigHandler {

    private Statz plugin;

    public ConfigHandler(Statz plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        // Load defaults
        reloadConfig();

        plugin.getConfig().options().header("This is the config file of " + plugin.getDescription().getFullName()
                + ". \nYou can configure Statz with this file."
                + "\nThe 'track stats' option can either be true of false. When it is true, Statz will record data of players. If it is false, Statz won't record any data."
                + "\nThe 'show database save message' option allows you to toggle the visibility of the 'Save message'. Every few minutes, the database of Statz is saved. If you"
                + "set this option to false, Statz will still save the database, but won't show the message."
                + "\nThe 'periodic save time' value represents the time (in seconds) after an update is sent to the database. The default is 10 seconds and I don't recommend changing it."
                + "\nYou can experiment with it and see whether it improves performance for your server. A lower value means the database is updated more often, resulting in a decrement of performance."
                + "\nDisabled statistics option can be used to disable a statistic. A list of statistics you can disable is provided here: https://github.com/Staartvin/Statz/blob/master/src/me/staartvin/statz/datamanager/PlayerStat.java#L20"
                + "\nThe 'ignore creative' option can either be true or false. When set to false, Statz will not log statistics of players that are in creative mode."
                + "\nEnabling debug output will show you a host of messages in the console that can help you debug problems when you have any."
                + "\nIf 'use custom statz list' is set to true, the /statz command will show a list of statistics for a player. The statistics that get displayed can be altered by changing the 'custom statz list' variable."
                + "\nFor a list of statistics to use in the custom list, click here: https://github.com/Staartvin/Statz/blob/master/src/me/staartvin/statz/datamanager/PlayerStat.java#L19"
                + "\nThe 'use statz gui' option enables you to view the statistics of a player via a gui. It is disabled by default ");

        plugin.getConfig().addDefault("track stats", true);
        plugin.getConfig().addDefault("show database save message", false);

        plugin.getConfig().addDefault("mysql.enabled", false);
        plugin.getConfig().addDefault("mysql.hostname", "localhost:3306");
        plugin.getConfig().addDefault("mysql.username", "root");
        plugin.getConfig().addDefault("mysql.password", "");
        plugin.getConfig().addDefault("mysql.database", "Statz");

        plugin.getConfig().addDefault("periodic save time", 10);

        plugin.getConfig().addDefault("disabled statistics",
                Arrays.asList("DISABLED_STAT_NAME_HERE", "OTHER_DISABLED_STAT_NAME"));

        plugin.getConfig().addDefault("ignore creative", false);

        plugin.getConfig().addDefault("enable debug output", true);

        plugin.getConfig().addDefault("latest patch mysql version", 0);
        plugin.getConfig().addDefault("latest patch sqlite version", 0);

        plugin.getConfig().addDefault("use custom statz list", false);

        plugin.getConfig().addDefault("custom statz list", Arrays.asList("JOINS", "FOOD_EATEN", "KILLS_PLAYERS"));

        plugin.getConfig().addDefault("use statz gui", false);

        plugin.getConfig().options().copyDefaults(true);

        this.saveConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    public void setStatsTracking(boolean value) {
        plugin.getConfig().set("track stats", value);
        saveConfig();
    }

    public boolean getStatsTracking() {
        return plugin.getConfig().getBoolean("track stats", true);
    }

    public boolean shouldShowDatabaseSave() {
        return plugin.getConfig().getBoolean("show database save message", false);
    }

    public void setShowDatabaseSave(boolean value) {
        plugin.getConfig().set("show database save message", value);
        saveConfig();
    }

    public boolean isMySQLEnabled() {
        return plugin.getConfig().getBoolean("mysql.enabled", false);
    }

    public String getMySQLHostname() {
        return plugin.getConfig().getString("mysql.hostname", "localhost:3306");
    }

    public String getMySQLUsername() {
        return plugin.getConfig().getString("mysql.username", "root");
    }

    public String getMySQLPassword() {
        return plugin.getConfig().getString("mysql.password", "");
    }

    public String getMySQLDatabase() {
        return plugin.getConfig().getString("mysql.database", "Statz");
    }

    public int getPeriodicSaveTime() {
        return plugin.getConfig().getInt("periodic save time", 10);
    }

    public List<PlayerStat> getDisabledStats() {
        List<String> disabledStatsString = plugin.getConfig().getStringList("disabled statistics");
        List<PlayerStat> disabledStats = new ArrayList<>();

        for (String disabledStatString : disabledStatsString) {
            PlayerStat stat = null;
            try {
                stat = PlayerStat.valueOf(disabledStatString.toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException e) {

            }

            if (stat == null)
                continue;

            disabledStats.add(stat);
        }

        return disabledStats;
    }

    public boolean isStatDisabled(PlayerStat stat) {
        return this.getDisabledStats().contains(stat);
    }

    public boolean shouldIgnoreCreative() {
        return plugin.getConfig().getBoolean("ignore creative", false);
    }

    public boolean isDebugEnabled() {
        return plugin.getConfig().getBoolean("enable debug output", true);
    }

    public boolean useCustomList() {
        return plugin.getConfig().getBoolean("use custom statz list", false);
    }

    public List<PlayerStat> getCustomList() {
        List<PlayerStat> customList = new ArrayList<PlayerStat>();

        for (String customListEntry : plugin.getConfig().getStringList("custom statz list")) {
            for (PlayerStat stat : PlayerStat.values()) {
                if (stat.getTableName().equalsIgnoreCase(customListEntry)) {
                    customList.add(stat);
                }
            }
        }

        return customList;
    }

    public int getLatestPatchMySQLVersion() {
        return plugin.getConfig().getInt("latest patch mysql version", 0);
    }

    public int getLatestPatchSQLiteVersion() {
        return plugin.getConfig().getInt("latest patch sqlite version", 0);
    }

    public void setLatestPatchMySQLVersion(int version) {
        plugin.getConfig().set("latest patch mysql version", version);

        this.saveConfig();
    }
    
    public void setLatestPatchSQLiteVersion(int version) {
        plugin.getConfig().set("latest patch sqlite version", version);

        this.saveConfig();
    }

    public boolean isStatzGUIenabled() {
        return plugin.getConfig().getBoolean("use statz gui", false);
    }

}
