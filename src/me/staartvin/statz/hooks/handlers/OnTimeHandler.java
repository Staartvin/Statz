package me.staartvin.statz.hooks.handlers;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import me.edge209.OnTime.OnTime;
import me.edge209.OnTime.OnTimeAPI;
import me.edge209.OnTime.OnTimeAPI.data;
import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;

/**
 * Handles all connections with OnTime
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class OnTimeHandler implements DependencyHandler {

    private OnTime api;
    private final Statz plugin;

    public OnTimeHandler(final Statz instance) {
        plugin = instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
     */
    @Override
    public Plugin get() {
        final Plugin plugin = this.plugin.getServer().getPluginManager()
                .getPlugin(Dependency.ON_TIME.getInternalString());

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof OnTime)) {
            return null; // Maybe you want throw an exception instead
        }

        return plugin;
    }

    public int getPlayTime(final String playerName) {
        if (!isAvailable())
            return -1;

        // Divide by 60000 because time is in milliseconds
        return (int) (OnTimeAPI.getPlayerTimeData(playerName, data.TOTALPLAY) / 60000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
     */
    @Override
    public boolean isAvailable() {
        return api != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
     */
    @Override
    public boolean isInstalled() {
        final Plugin plugin = get();

        return plugin != null && plugin.isEnabled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
     */
    @Override
    public boolean setup(final boolean verbose) {
        if (!isInstalled()) {
            if (verbose) {
                plugin.debugMessage(ChatColor.RED + Dependency.ON_TIME.getInternalString() + " has not been found!");
            }
            return false;
        } else {
            api = (OnTime) get();

            if (api != null) {
                return true;
            } else {
                if (verbose) {
                    plugin.debugMessage(ChatColor.RED + Dependency.ON_TIME.getInternalString()
                            + " has been found but cannot be used!");
                }
                return false;
            }
        }
    }
}
