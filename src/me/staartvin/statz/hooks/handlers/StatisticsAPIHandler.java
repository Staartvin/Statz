package me.staartvin.statz.hooks.handlers;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.avaje.ebeaninternal.server.autofetch.Statistics;
import com.wolvencraft.yasp.StatisticsAPI;
import com.wolvencraft.yasp.session.OfflineSession;
import com.wolvencraft.yasp.session.OnlineSession;
import com.wolvencraft.yasp.util.NamedInteger;
import com.wolvencraft.yasp.util.cache.OfflineSessionCache;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;

/**
 * Handles all connections with Statistics
 * <p>
 * Date created: 21:02:34 15 mrt. 2014 TODO Statistics cannot currently look up
 * custom data. Thus Autorank cannot support it yet.
 * 
 * @author Staartvin
 * 
 */
public class StatisticsAPIHandler implements DependencyHandler {

	private StatisticsAPI api;
	private final Statz plugin;

	public StatisticsAPIHandler(final Statz instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin(Dependency.STATISTICS.getInternalString());

		try {
			// WorldGuard may not be loaded
			if (plugin == null || !(plugin instanceof Statistics)) {
				return null; // Maybe you want throw an exception instead
			}
		} catch (final NoClassDefFoundError exception) {
			this.plugin.debugMessage(ChatColor.RED + 
					"Could not find Statistics because it's probably disabled! Does Statistics properly connect to your MySQL database?");
			return null;
		}

		return plugin;
	}

	@SuppressWarnings("unused")
	public int getNormalStat(final UUID uuid, final String statType, final String worldName) {

		final OfflineSession offlineSession = StatisticsAPI.getSession(uuid);
		OnlineSession onlineSession = null;

		if (plugin.getServer().getPlayer(uuid) != null) {
			onlineSession = StatisticsAPI.getSession(plugin.getServer().getPlayer(uuid));
		}

		for (final NamedInteger n : OfflineSessionCache.fetch(uuid).getPlayerTotals().getNamedValues()) {
			System.out.print("n: " + n.getName() + " value: " + n.getValue());
		}

		// TODO: Finish shit

		return 0;
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

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@SuppressWarnings("unused")
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.debugMessage(ChatColor.RED + Dependency.STATISTICS.getInternalString() + " has not been found!");
			}
			return false;
		} else {
			final Statistics stats = (Statistics) get();

			api = new StatisticsAPI();

			if (api != null) {
				if (verbose) {
					plugin.debugMessage(ChatColor.RED + Dependency.STATISTICS.getInternalString() + " has been found and can be used!");
				}
				return true;
			} else {
				if (verbose) {
					plugin.debugMessage(ChatColor.RED + Dependency.STATISTICS.getInternalString() + " has been found but cannot be used!");
				}
				return false;
			}
		}
	}
}
