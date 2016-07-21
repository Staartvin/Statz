package me.staartvin.statz.hooks.handlers;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;

/**
 * Handles all connections with GriefPrevention
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class GriefPreventionHandler implements DependencyHandler {

	private GriefPrevention api;
	private final Statz plugin;

	public GriefPreventionHandler(final Statz instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin(Dependency.GRIEF_PREVENTION.getInternalString());

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof GriefPrevention)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return api != null;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		final GriefPrevention plugin = (GriefPrevention) get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.debugMessage(
						ChatColor.RED + Dependency.GRIEF_PREVENTION.getInternalString() + " has not been found!");
			}
			return false;
		} else {
			api = (GriefPrevention) get();

			if (api != null) {
				return true;
			} else {
				if (verbose) {
					plugin.debugMessage(ChatColor.RED + Dependency.GRIEF_PREVENTION.getInternalString()
							+ " has been found but cannot be used!");
				}
				return false;
			}
		}
	}

	private PlayerData getPlayerData(UUID uuid) {
		return api.dataStore.getPlayerData(uuid);
	}

	public int getNumberOfClaims(UUID uuid) {
		if (this.isAvailable())
			return -1;

		PlayerData data = this.getPlayerData(uuid);

		return data.getClaims().size();
	}

	public int getNumberOfClaimedBlocks(UUID uuid) {
		if (this.isAvailable())
			return -1;

		PlayerData data = this.getPlayerData(uuid);

		return data.getAccruedClaimBlocks();
	}

	public int getNumberOfRemainingBlocks(UUID uuid) {
		if (this.isAvailable())
			return -1;

		PlayerData data = this.getPlayerData(uuid);

		return data.getRemainingClaimBlocks();
	}

	public int getNumberOfBonusBlocks(UUID uuid) {
		if (this.isAvailable())
			return -1;

		PlayerData data = this.getPlayerData(uuid);

		return data.getBonusClaimBlocks();
	}

}
