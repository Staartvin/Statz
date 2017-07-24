package me.staartvin.statz.hooks;

import me.staartvin.statz.Statz;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * An interface class to use for a single dependency <br>
 * This class is used for single plugins only.
 * <p>
 * Date created: 17:52:02 4 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public abstract class DependencyHandler {

    /**
     * Get the main class of the dependency. <br>
     * <b>Note that you still have to cast it to the correct plugin.</b>
     * 
     * @return main class of plugin, or null if not found.
     */
    public abstract Plugin get();

    /**
     * Check whether Statz has hooked this dependency and thus can use it.
     * 
     * @return true if Statz hooked into it, false otherwise.
     */
    public abstract boolean isAvailable();

    /**
     * Check to see if this dependency is running on this server
     * 
     * @return true if it is, false otherwise.
     */
    public abstract boolean isInstalled();

    /**
     * Setup the hook between this dependency and Autorank
     * 
     * @param verbose
     *            Whether to show output or not
     * @return true if correctly setup, false otherwise.
     */
    public abstract boolean setup(boolean verbose);

    public Statz getPlugin() {
        return (Statz) Bukkit.getPluginManager().getPlugin("Statz");
    }
}
