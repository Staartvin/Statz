package me.staartvin.statz.hooks;

import me.staartvin.statz.Statz;
import me.staartvin.utils.pluginlibrary.statz.Library;
import me.staartvin.utils.pluginlibrary.statz.PluginLibrary;
import me.staartvin.utils.pluginlibrary.statz.hooks.LibraryHook;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * This class is used for loading all the dependencies Statz has. <br>
 * Not all dependencies are required, some are optional.
 * <p>
 * Date created: 18:18:43 2 mrt. 2014
 *
 * @author Staartvin
 */
public class DependencyManager {

    private final HashMap<StatzDependency, DependencyHandler> handlers = new HashMap<StatzDependency,
            DependencyHandler>();

    private final Statz plugin;

    private PluginLibrary pluginLibrary = null;

    public DependencyManager(final Statz instance) {
        plugin = instance;

        this.loadPluginLibrary();
    }

    /**
     * Gets a specific dependency.
     *
     * @param dep StatzDependency to get.
     * @return the {@linkplain DependencyHandler} that is associated with the
     * given {@linkplain StatzDependency}, can be null.
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

                plugin.debugMessage(ChatColor.GREEN + dependency.getInternalString()
                        + " was found and Statz now tracks its data!");
            }
        }

        // Make seperate stop loading bar
        plugin.debugMessage(ChatColor.YELLOW + "---------------[Statz Dependencies]---------------");

        plugin.debugMessage("Loaded libraries and dependencies");
    }

    /**
     * Get the StatzDependency by the StatzDependency Handler
     *
     * @param depHandler The dependency handler to get the dependency from.
     * @return the dependency that is associated with this dependency handler or
     * null if no association was found.
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

    /**
     * Get library hook of PluginLibrary
     *
     * @param library library to get
     * @return hook used by PluginLibrary (if available) or null if not found.
     */
    public Optional<LibraryHook> getLibraryHook(Library library) {
        if (library == null) return Optional.empty();

        if (!this.isPluginLibraryLoaded()) return Optional.empty();


        return PluginLibrary.getLibrary(library);
    }

    /**
     * Check whether a plugin is available using PluginLibrary.
     *
     * @param library Library to check
     * @return true if it is available, false otherwise.
     */
    public boolean isAvailable(Library library) {

        if (!this.isPluginLibraryLoaded()) return false;

        if (library == null) return false;

        Optional<LibraryHook> hook = this.getLibraryHook(library);

        return hook.filter(libraryHook -> LibraryHook.isPluginAvailable(library) && libraryHook.isHooked()).isPresent();
    }

    private boolean loadPluginLibrary() {
        pluginLibrary = PluginLibrary.getPluginLibrary(this.plugin);

        return pluginLibrary.enablePluginLibrary() > 0;
    }

    public boolean isPluginLibraryLoaded() {
        return pluginLibrary != null;
    }
}
