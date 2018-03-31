package me.staartvin.statz.language;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * Class used to handle descriptions of statistics.
 *
 * @author Staartvin
 */
public class StatisticDescriptionConfig {

    private final Statz plugin;
    private FileConfiguration statisticsConfig;
    private File statisticsConfigFile;

    public StatisticDescriptionConfig(final Statz plugin) {
        this.plugin = plugin;
    }

    public void createNewFile() {
        reloadConfig();
        saveConfig();

        StatisticDescription.setFile(statisticsConfig);

        loadConfig();

        plugin.getLogger().info("Statistic description file loaded (statistics.yml)");
    }

    public FileConfiguration getConfig() {
        if (statisticsConfig == null) {
            this.reloadConfig();
        }
        return statisticsConfig;
    }

    public void loadConfig() {

        statisticsConfig.options().header("This file is used for messages of each statistic. You can alter the " +
                "messages as well as their appearance in the Statz GUI.");

        for (final StatisticDescription statistic : StatisticDescription.values()) {
            statisticsConfig.addDefault(statistic.getStringIdentifier() + ".human friendly title", statistic
                    .getRelatedPlayerStat().getHumanFriendlyName());

            statisticsConfig.addDefault(statistic.getStringIdentifier() + ".high detail desc", statistic
                    .getHighDetailDescription());
            statisticsConfig.addDefault(statistic.getStringIdentifier() + ".low detail desc", statistic
                    .getTotalDescription());
            statisticsConfig.addDefault(statistic.getStringIdentifier() + ".gui icon", statistic.getRelatedPlayerStat
                    ().getIconMaterial().toString());
        }

        statisticsConfig.options().copyDefaults(true);
        saveConfig();
    }

    @SuppressWarnings("deprecation")
    public void reloadConfig() {
        if (statisticsConfigFile == null) {
            statisticsConfigFile = new File(plugin.getDataFolder() + "/statistics", "statistics.yml");
        }
        statisticsConfig = YamlConfiguration.loadConfiguration(statisticsConfigFile);

        // Look for defaults in the jar
        final InputStream defConfigStream = plugin.getResource("statistics.yml");
        if (defConfigStream != null) {
            final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            statisticsConfig.setDefaults(defConfig);
        }
    }

    public void saveConfig() {
        if (statisticsConfig == null || statisticsConfigFile == null) {
            return;
        }
        try {
            getConfig().save(statisticsConfigFile);
        } catch (final IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + statisticsConfigFile, ex);
        }
    }

    private StatisticDescription getStatisticDescription(PlayerStat playerStat) {
        for (StatisticDescription desc : StatisticDescription.values()) {
            if (desc.getRelatedPlayerStat().equals(playerStat)) {
                return desc;
            }
        }

        return null;
    }

    public String getHighDetailDescription(PlayerStat playerStat, Object... args) {

        StatisticDescription description = getStatisticDescription(playerStat);

        if (description == null) {
            return null;
        }

        return statisticsConfig.getString(description.getStringIdentifier() + ".high detail desc", description
                .getHighDetailDescription(args));
    }

    public String getLowDetailDescription(PlayerStat playerStat, Object... args) {

        StatisticDescription description = getStatisticDescription(playerStat);

        if (description == null) {
            return null;
        }

        return statisticsConfig.getString(description.getStringIdentifier() + ".low detail desc", description
                .getTotalDescription(args));
    }

    public Material getIconMaterial(PlayerStat playerStat) {

        StatisticDescription description = getStatisticDescription(playerStat);

        if (description == null) {
            return playerStat.getIconMaterial();
        }

        String materialName = statisticsConfig.getString(description.getStringIdentifier() + ".gui icon", playerStat
                .getIconMaterial().toString());

        Material iconMaterial = Material.getMaterial(materialName);

        if (iconMaterial == null) {
            iconMaterial = playerStat.getIconMaterial();
        }

        return iconMaterial;
    }

    public String getHumanFriendlyTitle(PlayerStat playerStat) {

        StatisticDescription description = getStatisticDescription(playerStat);

        if (description == null) {
            return null;
        }

        return statisticsConfig.getString(description.getStringIdentifier() + ".human friendly title", playerStat
                .getHumanFriendlyName());
    }
}
