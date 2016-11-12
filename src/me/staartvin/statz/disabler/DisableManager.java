package me.staartvin.statz.disabler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.GriefPreventionHandler;
import me.staartvin.statz.hooks.handlers.WorldGuardHandler;

/**
 * This class reads from the disabled-stats.yml and has methods to check whether a statistic should be tracked in a certain area.
 * @author Staartvin
 *
 */
public class DisableManager {

	private Statz plugin;

	private FileConfiguration disableConfig;
	private File disableConfigFile;

	public DisableManager(Statz plugin) {
		this.plugin = plugin;
	}

	/**
	 * Is a stat disabled in a certain location?
	 * @param loc Location to check
	 * @param stat Stat to check
	 * @return true if it is disabled on this location, false otherwise.
	 */
	public boolean isStatDisabledLocation(Location loc, PlayerStat stat) {

		// Only check for WG regions if WorldGuard is installed
		if (plugin.getDependencyManager().isAvailable(Dependency.WORLDGUARD)) {
			List<String> disabledRegions = this.getDisabledWorldGuardRegions(stat);

			WorldGuardHandler wgHandler = (WorldGuardHandler) plugin.getDependencyManager()
					.getDependency(Dependency.WORLDGUARD);

			// Check for all disabled regions if a player is in them.
			if (!disabledRegions.isEmpty()) {
				for (String regionName : disabledRegions) {
					if (wgHandler.isInRegion(loc, regionName)) {
						return true;
					}
				}
			}
		}

		// Check GriefPrevention claims
		if (plugin.getDependencyManager().isAvailable(Dependency.GRIEF_PREVENTION)) {
			List<String> disabledUUIDs = this.getDisabledGriefPreventionClaims(stat);

			GriefPreventionHandler gpHandler = (GriefPreventionHandler) plugin.getDependencyManager()
					.getDependency(Dependency.GRIEF_PREVENTION);

			// Check for all disabled uuid claims if a player is in them.
			if (!disabledUUIDs.isEmpty()) {
				for (String disabledClaim : disabledUUIDs) {
					if (gpHandler.isInRegion(loc, UUID.fromString(disabledClaim))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public List<String> getDisabledWorldGuardRegions(PlayerStat stat) {
		return this.disableConfig.getStringList(stat.toString() + ".WorldGuard regions");
	}

	public List<String> getDisabledGriefPreventionClaims(PlayerStat stat) {
		return this.disableConfig.getStringList(stat.toString() + ".GriefPrevention claims");
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
						+ "\nFor more information on how to use this file, go to: <link here>");

		disableConfig.options().copyDefaults(true);
		saveConfig();
	}

	@SuppressWarnings("deprecation")
	public void reloadConfig() {
		if (disableConfigFile == null) {
			disableConfigFile = new File(plugin.getDataFolder(), "disabled-stats.yml");
		}
		disableConfig = YamlConfiguration.loadConfiguration(disableConfigFile);

		// Look for defaults in the jar
		final InputStream defConfigStream = plugin.getResource("disabled-stats.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			disableConfig.setDefaults(defConfig);
		}
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
