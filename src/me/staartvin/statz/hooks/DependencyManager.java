package me.staartvin.statz.hooks;

import java.util.HashMap;
import java.util.Map.Entry;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.handlers.AFKTerminatorHandler;
import me.staartvin.statz.hooks.handlers.ASkyBlockHandler;
import me.staartvin.statz.hooks.handlers.AcidIslandHandler;
import me.staartvin.statz.hooks.handlers.EssentialsHandler;
import me.staartvin.statz.hooks.handlers.FactionsHandler;
import me.staartvin.statz.hooks.handlers.GriefPreventionHandler;
import me.staartvin.statz.hooks.handlers.JobsHandler;
import me.staartvin.statz.hooks.handlers.McMMOHandler;
import me.staartvin.statz.hooks.handlers.NuVotifierHandler;
import me.staartvin.statz.hooks.handlers.OnTimeHandler;
import me.staartvin.statz.hooks.handlers.RPGmeHandler;
import me.staartvin.statz.hooks.handlers.RoyalCommandsHandler;
import me.staartvin.statz.hooks.handlers.StatisticsAPIHandler;
import me.staartvin.statz.hooks.handlers.StatsAPIHandler;
import me.staartvin.statz.hooks.handlers.UltimateCoreHandler;
import me.staartvin.statz.hooks.handlers.VaultHandler;
import me.staartvin.statz.hooks.handlers.VotifierHandler;
import me.staartvin.statz.hooks.handlers.WorldGuardHandler;
import net.md_5.bungee.api.ChatColor;

/**
 * This class is used for loading all the dependencies Statz has. <br>
 * Not all dependencies are required, some are optional.
 * <p>
 * Date created: 18:18:43 2 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class DependencyManager {

	private final HashMap<Dependency, DependencyHandler> handlers = new HashMap<Dependency, DependencyHandler>();

	private final Statz plugin;

	public DependencyManager(final Statz instance) {
		plugin = instance;

		// Register handlers
		handlers.put(Dependency.VOTIFIER, new VotifierHandler(instance));
		handlers.put(Dependency.JOBS, new JobsHandler(instance));
		handlers.put(Dependency.MCMMO, new McMMOHandler(instance));
		handlers.put(Dependency.ASKYBLOCK, new ASkyBlockHandler(instance));
		handlers.put(Dependency.ACIDISLAND, new AcidIslandHandler(instance));
		handlers.put(Dependency.WORLDGUARD, new WorldGuardHandler(instance));
		handlers.put(Dependency.ROYAL_COMMANDS, new RoyalCommandsHandler(instance));
		handlers.put(Dependency.ON_TIME, new OnTimeHandler(instance));
		handlers.put(Dependency.AFKTERMINATOR, new AFKTerminatorHandler(instance));
		handlers.put(Dependency.ESSENTIALS, new EssentialsHandler(instance));
		handlers.put(Dependency.FACTIONS, new FactionsHandler(instance));
		handlers.put(Dependency.STATISTICS, new StatisticsAPIHandler(instance));
		handlers.put(Dependency.STATS, new StatsAPIHandler(instance));
		handlers.put(Dependency.ULTIMATE_CORE, new UltimateCoreHandler(instance));
		handlers.put(Dependency.VAULT, new VaultHandler(instance));
		handlers.put(Dependency.GRIEF_PREVENTION, new GriefPreventionHandler(instance));
		handlers.put(Dependency.RPGME, new RPGmeHandler(instance));
		handlers.put(Dependency.NUVOTIFIER, new NuVotifierHandler(instance));
	}

	/**
	 * Gets a specific dependency.
	 * 
	 * @param dep Dependency to get.
	 * @return the {@linkplain DependencyHandler} that is associated with the
	 *         given {@linkplain Dependency}, can be null.
	 */
	public DependencyHandler getDependency(final Dependency dep) {

		if (!handlers.containsKey(dep)) {
			throw new IllegalArgumentException("Unknown dependency '" + dep.toString() + "'");
		} else {
			return handlers.get(dep);
		}
	}

	/*public StatsPlugin getStatsPlugin() {
		return statsPluginManager.getStatsPlugin();
	}*/

	/**
	 * Gets whether the given player is AFK.
	 * <br>
	 * Obeys the AFK setting in the Settings.yml.
	 * 
	 * @param player Player to check.
	 * @return true if the player is supspected of being AFK, false otherwise.
	 */
	/*public boolean isAFK(final Player player) {
		if (!plugin.getConfigHandler().useAFKIntegration()) {
			return false;
		}
	
		if (handlers.get(dependency.ESSENTIALS).isAvailable()) {
			plugin.debugMessage("Using Essentials for AFK");
			return ((EssentialsHandler) handlers.get(dependency.ESSENTIALS)).isAFK(player);
		} else if (handlers.get(dependency.ROYALCOMMANDS).isAvailable()) {
			plugin.debugMessage("Using RoyalCommands for AFK");
			return ((RoyalCommandsHandler) handlers.get(dependency.ROYALCOMMANDS)).isAFK(player);
		} else if (handlers.get(dependency.ULTIMATECORE).isAvailable()) {
			plugin.debugMessage("Using UltimateCore for AFK");
			return ((UltimateCoreHandler) handlers.get(dependency.ULTIMATECORE)).isAFK(player);
		} else if (handlers.get(dependency.AFKTERMINATOR).isAvailable()) {
			plugin.debugMessage("Using AFKTerminator for AFK");
			return ((AFKTerminatorHandler) handlers.get(dependency.AFKTERMINATOR)).isAFK(player);
		}
		// No suitable plugin found
		return false;
	}*/

	/**
	 * Loads all dependencies used for Statz. <br>
	 * Statz will check for dependencies and shows the output on the console.
	 * 
	 * 
	 */
	public void loadDependencies() {

		// Make seperate loading bar

		plugin.debugMessage(ChatColor.YELLOW + "---------------[Statz Dependencies]---------------");
		plugin.debugMessage(ChatColor.GREEN + "Searching dependencies...");

		// Load all dependencies
		for (final DependencyHandler depHandler : handlers.values()) {
			// Make sure to respect settings
			boolean succeeded = depHandler.setup(true);

			if (succeeded) {
				Dependency dependency = this.getDependencyByHandler(depHandler);

				if (dependency == null)
					continue;

				// NuVotifier has the same internal name, and hence cannot be distinguished from Votifier. 
				// That's why we provide a special case.
				if (dependency == Dependency.NUVOTIFIER) {
					plugin.debugMessage(
							ChatColor.GREEN + "NuVotifier was found and Statz now tracks its data!");
				} else {
					plugin.debugMessage(
							ChatColor.GREEN + dependency.getInternalString() + " was found and Statz now tracks its data!");
				}
				
				
			}
		}

		// Make seperate stop loading bar
		plugin.debugMessage(ChatColor.YELLOW + "---------------[Statz Dependencies]---------------");

		plugin.debugMessage("Loaded libraries and dependencies");
	}

	/**
	 * Get the Dependency by the Dependency Handler
	 * @param depHandler The dependency handler to get the dependency from.
	 * @return the dependency that is associated with this dependency handler or null if no association was found.
	 */
	public Dependency getDependencyByHandler(DependencyHandler depHandler) {
		for (Entry<Dependency, DependencyHandler> entry : handlers.entrySet()) {
			if (entry.getValue().equals(depHandler)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public boolean isAvailable(Dependency dep) {
		DependencyHandler handler = handlers.get(dep);

		if (handler == null)
			return false;

		return handler.isAvailable();
	}

}
