package me.staartvin.statz.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.staartvin.statz.Statz;
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
        
        for (Dependency dep : Dependency.values()) {
         // Register handlers
            try {
                handlers.put(dep, dep.getDependencyHandler());
            } catch (NoClassDefFoundError e) {
                plugin.debugMessage("Could not load " + dep.getInternalString() + "!");
            }   
        }
        
        // Register handlers
        /*try {
            handlers.put(Dependency.VOTIFIER, new VotifierHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load Votifier!");
        }

        try {
            handlers.put(Dependency.JOBS, new JobsHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load Jobs!");
        }

        try {
            handlers.put(Dependency.MCMMO, new McMMOHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load mcMMO!");
        }

        try {
            handlers.put(Dependency.ASKYBLOCK, new ASkyBlockHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load ASkyBlock!");
        }

        try {
            handlers.put(Dependency.ACIDISLAND, new AcidIslandHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load AcidIsland!");
        }

        try {
            handlers.put(Dependency.WORLDGUARD, new WorldGuardHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load WorldGuard!");
        }

        try {
            handlers.put(Dependency.ROYAL_COMMANDS, new RoyalCommandsHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load RoyalCommands!");
        }

        try {
            handlers.put(Dependency.ON_TIME, new OnTimeHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load OnTime!");
        }

        try {
            handlers.put(Dependency.AFKTERMINATOR, new AFKTerminatorHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load afkTerminator!");
        }
        try {
            handlers.put(Dependency.ESSENTIALS, new EssentialsHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load Essentials!");
        }
        try {
            handlers.put(Dependency.FACTIONS, new FactionsHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load Factions!");
        }

        try {
            handlers.put(Dependency.STATISTICS, new StatisticsAPIHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load Statistics!");
        }

        try {
            handlers.put(Dependency.STATS, new StatsAPIHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load Stats!");
        }

        try {
            handlers.put(Dependency.ULTIMATE_CORE, new UltimateCoreHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load UltimateCore!");
        }

        try {
            handlers.put(Dependency.VAULT, new VaultHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load Vault!");
        }

        try {
            handlers.put(Dependency.GRIEF_PREVENTION, new GriefPreventionHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load GriefPrevention!");
        }

        try {
            handlers.put(Dependency.RPGME, new RPGmeHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load RPGMe!");
        }

        try {
            handlers.put(Dependency.NUVOTIFIER, new NuVotifierHandler(instance));
        } catch (NoClassDefFoundError e) {
            plugin.debugMessage("Could not load NuVotifier!");
        }*/
    }

    /**
     * Gets a specific dependency.
     * 
     * @param dep
     *            Dependency to get.
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

                // NuVotifier has the same internal name, and hence cannot be
                // distinguished from Votifier.
                // That's why we provide a special case.
                if (dependency == Dependency.NUVOTIFIER) {
                    plugin.debugMessage(ChatColor.GREEN + "NuVotifier was found and Statz now tracks its data!");
                } else {
                    plugin.debugMessage(ChatColor.GREEN + dependency.getInternalString()
                            + " was found and Statz now tracks its data!");
                }

            }
        }

        // Make seperate stop loading bar
        plugin.debugMessage(ChatColor.YELLOW + "---------------[Statz Dependencies]---------------");

        plugin.debugMessage("Loaded libraries and dependencies");
    }

    /**
     * Get the Dependency by the Dependency Handler
     * 
     * @param depHandler
     *            The dependency handler to get the dependency from.
     * @return the dependency that is associated with this dependency handler or
     *         null if no association was found.
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

    public List<Dependency> getAvailableDependencies() {
        List<Dependency> dependencies = new ArrayList<>();

        for (Dependency d : Dependency.values()) {
            if (this.isAvailable(d)) {
                dependencies.add(d);
            }
        }

        return dependencies;
    }

}
