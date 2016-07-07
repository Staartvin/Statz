package me.staartvin.statz.hooks.handlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MPlayer;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;


/**
 * Handles all connections with Factions
 * <p>
 * Date created: 21:01:50 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class FactionsHandler implements DependencyHandler {

	private Factions api;
	private final Statz plugin;

	public FactionsHandler(final Statz instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin(Dependency.FACTIONS.getInternalString());

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof Factions)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	public double getFactionPower(final Player player) {
		if (!isAvailable())
			return -1;

		final MPlayer uPlayer = MPlayer.get(player);

		if (!uPlayer.hasFaction()) {
			return -1;
		}

		return uPlayer.getFaction().getPower();
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
		final Factions plugin = (Factions) get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.debugMessage(ChatColor.RED + Dependency.FACTIONS.getInternalString() + " has not been found!");
			}

			return false;
		} else {
			api = (Factions) get();

			if (api != null) {
				if (verbose) {
					plugin.debugMessage(ChatColor.RED + Dependency.FACTIONS.getInternalString() + " has been found and can be used!");
				}
				return true;
			} else {
				if (verbose) {
					plugin.debugMessage(ChatColor.RED + Dependency.FACTIONS.getInternalString() + " has been found but cannot be used!");
				}
				return false;
			}
		}
	}
}
