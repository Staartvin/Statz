package me.staartvin.statz.hooks.handlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;


/**
 * Handles all connections with Essentials
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class EssentialsHandler implements DependencyHandler {

	private Essentials api;
	private final Statz plugin;

	public EssentialsHandler(final Statz instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin(Dependency.ESSENTIALS.getInternalString());

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof Essentials)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	public boolean isAFK(final Player player) {
		if (!isAvailable())
			return false;

		final User user = api.getUser(player);

		if (user == null) {
			return false;
		}

		return user.isAfk();
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
		final Plugin plugin = get();

		return plugin != null && plugin.isEnabled();
	}

	public boolean isJailed(final Player player) {
		if (!isAvailable())
			return false;

		final User user = api.getUser(player);

		if (user == null) {
			return false;
		}

		return user.isJailed();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.debugMessage(ChatColor.RED + Dependency.ESSENTIALS.getInternalString() + " has not been found!");
			}
			return false;
		} else {
			api = (Essentials) get();

			if (api != null) {
				if (verbose) {
					plugin.debugMessage(ChatColor.RED + Dependency.ESSENTIALS.getInternalString() + " has been found and can be used!");
				}
				return true;
			} else {
				if (verbose) {
					plugin.debugMessage(ChatColor.RED + Dependency.ESSENTIALS.getInternalString() + " has been found but cannot be used!");
				}
				return false;
			}
		}
	}

	public String getGeoIPLocation(final Player player) {
		if (!isAvailable())
			return null;

		final User user = api.getUser(player);

		if (user == null) {
			return null;
		}

		return user.getGeoLocation();
	}
}
