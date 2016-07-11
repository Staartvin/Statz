package me.staartvin.statz.hooks.handlers;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.vexsoftware.votifier.Votifier;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;

/**
 * Handles all connections with Votifier
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class VotifierHandler implements DependencyHandler {

	private final Statz plugin;
	private Votifier api;

	public VotifierHandler(final Statz instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin(Dependency.VOTIFIER.getInternalString());

		// May not be loaded
		if (plugin == null || !(plugin instanceof Votifier)) {
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
		final Votifier plugin = (Votifier) get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.debugMessage(ChatColor.RED + Dependency.VOTIFIER.getInternalString() + " has not been found!");
			}
			return false;
		} else {
			api = (Votifier) get();

			if (api != null) {
				return true;
			} else {
				if (verbose) {
					plugin.debugMessage(ChatColor.RED + Dependency.VOTIFIER.getInternalString() + " has been found but cannot be used!");
				}
				return false;
			}
		}
	}
}
