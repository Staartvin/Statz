package me.staartvin.statz.hooks.handlers;

import me.staartvin.statz.hooks.StatzDependency;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.vexsoftware.votifier.NuVotifierBukkit;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.DependencyHandler;

/**
 * Handles all connections with Votifier
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class NuVotifierHandler extends DependencyHandler {

    private final Statz plugin;
    private NuVotifierBukkit api;

    public NuVotifierHandler() {
        plugin = this.getPlugin();
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
     */
    @Override
    public Plugin get() {
        final Plugin plugin = this.plugin.getServer().getPluginManager()
                .getPlugin(StatzDependency.NUVOTIFIER.getInternalString());

        try {
            // May not be loaded
            if (plugin == null || !(plugin instanceof NuVotifierBukkit)) {
                return null; // Maybe you want throw an exception instead
            }
        } catch (NoClassDefFoundError e) {
            // Votifier was not found, maybe try NuVotifier
            return null;
        }

        return plugin;
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
        Plugin plugin = get();

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
                plugin.debugMessage(ChatColor.RED + "NuVotifier has not been found!");
            }
            return false;
        } else {
            api = (NuVotifierBukkit) get();

            if (api != null) {
                return true;
            } else {
                if (verbose) {
                    plugin.debugMessage(ChatColor.RED + "NuVotifier has been found but cannot be used!");
                }
                return false;
            }
        }
    }
}
