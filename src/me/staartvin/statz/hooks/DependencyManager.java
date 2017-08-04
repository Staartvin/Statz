package me.staartvin.statz.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.LibraryHook;
import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.handlers.PluginLibraryHandler;
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

    private final HashMap<StatzDependency, DependencyHandler> handlers = new HashMap<StatzDependency, DependencyHandler>();

    private final Statz plugin;

    public DependencyManager(final Statz instance) {
        plugin = instance;
        
        for (StatzDependency dep : StatzDependency.values()) {
         // Register handlers
            try {
                handlers.put(dep, dep.getDependencyHandler());
            } catch (NoClassDefFoundError e) {
                plugin.debugMessage("Could not load " + dep.getInternalString() + "!");
            }   
        }
    }

    /**
     * Gets a specific dependency.
     * 
     * @param dep
     *            StatzDependency to get.
     * @return the {@linkplain DependencyHandler} that is associated with the
     *         given {@linkplain StatzDependency}, can be null.
     */
    public DependencyHandler getDependency(final StatzDependency dep) {

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
                StatzDependency dependency = this.getDependencyByHandler(depHandler);

                if (dependency == null)
                    continue;

                // NuVotifier has the same internal name, and hence cannot be
                // distinguished from Votifier.
                // That's why we provide a special case.
                if (dependency == StatzDependency.NUVOTIFIER) {
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
     * Get the StatzDependency by the StatzDependency Handler
     * 
     * @param depHandler
     *            The dependency handler to get the dependency from.
     * @return the dependency that is associated with this dependency handler or
     *         null if no association was found.
     */
    public StatzDependency getDependencyByHandler(DependencyHandler depHandler) {
        for (Entry<StatzDependency, DependencyHandler> entry : handlers.entrySet()) {
            if (entry.getValue().equals(depHandler)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean isAvailable(StatzDependency dep) {
        DependencyHandler handler = handlers.get(dep);

        if (handler == null)
            return false;

        return handler.isAvailable();
    }

    public List<StatzDependency> getAvailableDependencies() {
        List<StatzDependency> dependencies = new ArrayList<>();

        for (StatzDependency d : StatzDependency.values()) {
            if (this.isAvailable(d)) {
                dependencies.add(d);
            }
        }

        return dependencies;
    }

    /**
     * Get library hook of PluginLibrary
     * @param library library to get
     * @return hook used by PluginLibrary (if available) or null if not found.
     */
    public LibraryHook getLibraryHook(Library library) {
        if (!this.isAvailable(StatzDependency.PLUGINLIBRARY)) return null;

        if (library == null) return null;

        PluginLibraryHandler handler = (PluginLibraryHandler) getDependency(StatzDependency.PLUGINLIBRARY);

        if (handler == null) {
            return null;
        }

        return handler.getLibraryHook(library);
    }

    /**
     * Check whether a plugin is available using PluginLibrary.
     * @param library Library to check
     * @return true if it is available, false otherwise.
     */
    public boolean isAvailable(Library library) {

        if (!this.isAvailable(StatzDependency.PLUGINLIBRARY)) return false;

        if (library == null) return false;

        PluginLibraryHandler handler = (PluginLibraryHandler) getDependency(StatzDependency.PLUGINLIBRARY);

        if (handler == null) {
            return false;
        }

        LibraryHook hook = handler.getLibraryHook(library);

        if (hook == null) {
            return false;
        }

        return hook.isAvailable();
    }

}
