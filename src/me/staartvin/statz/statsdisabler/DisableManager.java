package me.staartvin.statz.statsdisabler;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.utils.pluginlibrary.statz.Library;
import me.staartvin.utils.pluginlibrary.statz.hooks.GriefPreventionHook;
import me.staartvin.utils.pluginlibrary.statz.hooks.LibraryHook;
import me.staartvin.utils.pluginlibrary.statz.hooks.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class reads from the disabled-stats.yml and has methods to check whether a statistic should be tracked in a certain area.
 *
 * @author Staartvin
 */
public class DisableManager {

    private final Statz plugin;

    private FileConfiguration disableConfig;
    private File disableConfigFile;

    public DisableManager(Statz plugin) {
        this.plugin = plugin;
    }

    /**
     * Is a stat disabled in a certain location?
     *
     * @param loc  Location to check
     * @param stat Stat to check
     * @return true if it is disabled on this location, false otherwise.
     */
    public boolean isStatDisabledLocation(Location loc, PlayerStat stat) {

        // We cannot check if PluginLibrary is not available.
        if (!plugin.getDependencyManager().isPluginLibraryLoaded()) {
            return false;
        }

        LibraryHook worldGuardHook = plugin.getDependencyManager().getLibraryHook(Library.WORLDGUARD).orElse(null);

        // Only check for WG regions if WorldGuard is installed
        if (worldGuardHook != null && worldGuardHook.isHooked()) {
            List<String> disabledRegions = this.getDisabledWorldGuardRegions(stat);

            WorldGuardHook wgHook = (WorldGuardHook) plugin.getDependencyManager().getLibraryHook(Library.WORLDGUARD).orElse(null);

            // Check for all disabled regions if a player is in them.
            if (!disabledRegions.isEmpty()) {
                for (String regionName : disabledRegions) {
                    if (wgHook.isInRegion(loc, regionName)) {
                        return true;
                    }
                }
            }
        }

        LibraryHook griefPreventionHook = plugin.getDependencyManager().getLibraryHook(Library.GRIEFPREVENTION).orElse(null);

        // Check GriefPrevention claims
        if (griefPreventionHook != null && griefPreventionHook.isHooked()) {
            List<String> disabledUUIDs = this.getDisabledGriefPreventionClaims(stat);

            GriefPreventionHook gpHook =
                    (GriefPreventionHook) plugin.getDependencyManager().getLibraryHook(Library.GRIEFPREVENTION).orElse(null);

            // Check for all disabled uuid claims if a player is in them.
            if (!disabledUUIDs.isEmpty()) {
                for (String disabledClaim : disabledUUIDs) {
                    if (gpHook.isInRegion(loc, UUID.fromString(disabledClaim))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<String> getDisabledWorldGuardRegions(PlayerStat stat) {

        for (String definedStat : this.disableConfig.getKeys(false)) {
            if (definedStat.equalsIgnoreCase(stat.toString())) {
                return this.disableConfig.getStringList(definedStat + ".WorldGuard regions");
            }
        }

        return Collections.emptyList();
    }

    public List<String> getDisabledGriefPreventionClaims(PlayerStat stat) {

        for (String definedStat : this.disableConfig.getKeys(false)) {
            if (definedStat.equalsIgnoreCase(stat.toString())) {
                return this.disableConfig.getStringList(definedStat + ".GriefPrevention claims");
            }
        }

        return Collections.emptyList();
    }

    public void createNewFile() {
        reloadConfig();
        saveConfig();

        loadConfig();

        plugin.getLogger().info("Stats disabled file loaded (disabled-stats.yml)");
    }

    public FileConfiguration getConfig() {
        if (disableConfig == null) {
            this.reloadConfig();
        }
        return disableConfig;
    }

    public void loadConfig() {

        disableConfig.options()
                .header("This file is used for disabling certain statistics in certain regions (e.g. WorldGuard) or when some requirements are met."
                        + "\nFor more information on how to use this file, go to: https://github.com/Staartvin/Statz/wiki/How-do-I-use-the-disabled-stats.yml-file%3F");

        disableConfig.options().copyDefaults(true);
        saveConfig();
    }

    public void reloadConfig() {
        if (disableConfigFile == null) {
            disableConfigFile = new File(plugin.getDataFolder(), "disabled-stats.yml");
        }
        disableConfig = YamlConfiguration.loadConfiguration(disableConfigFile);
    }

    public void saveConfig() {
        if (disableConfig == null || disableConfigFile == null) {
            return;
        }
        try {
            getConfig().save(disableConfigFile);
        } catch (final IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + disableConfigFile, ex);
        }
    }
}
