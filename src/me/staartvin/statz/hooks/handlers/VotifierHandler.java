package me.staartvin.statz.hooks.handlers;

import com.vexsoftware.votifier.Votifier;
import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.DependencyHandler;
import me.staartvin.statz.hooks.StatzDependency;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * Handles all connections with Votifier
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class VotifierHandler extends DependencyHandler {

    private final Statz plugin;
    private Votifier api;

    public VotifierHandler() {
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
                .getPlugin(StatzDependency.VOTIFIER.getInternalString());

        try {
            // May not be loaded
            if (plugin == null || !(plugin instanceof Votifier)) {
                return null; // Maybe you want throw an exception instead
            }
        } catch (NoClassDefFoundError e) {
            // Votifier was not found, maybe try NuVotifier
            return null;
            // e.printStackTrace();
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
                plugin.debugMessage(ChatColor.RED + StatzDependency.VOTIFIER.getInternalString() + " has not been found!");
            }
            return false;
        } else {
            try {
                api = (Votifier) get();
            } catch (NoClassDefFoundError e) {
                // Do nothing atm
            }

            if (api != null) {
                return true;
            } else {
                if (verbose) {
                    plugin.debugMessage(ChatColor.RED + StatzDependency.VOTIFIER.getInternalString()
                            + " has been found but cannot be used!");
                }
                return false;
            }
        }
    }
}
