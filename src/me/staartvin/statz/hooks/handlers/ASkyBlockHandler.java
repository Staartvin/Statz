package me.staartvin.statz.hooks.handlers;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.ASkyBlockAPI;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;

/**
 * Handles all connections with ASkyBlock
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class ASkyBlockHandler implements DependencyHandler {

    private ASkyBlock api;
    private final Statz plugin;

    public ASkyBlockHandler(final Statz instance) {
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
                .getPlugin(Dependency.ASKYBLOCK.getInternalString());

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof ASkyBlock)) {
            return null; // Maybe you want throw an exception instead
        }

        return plugin;
    }

    public int getIslandLevel(final UUID uuid) {
        if (!isAvailable())
            return -1;

        plugin.debugMessage(
                "ASkyBlock Island level of uuid '" + uuid + "' is " + ASkyBlockAPI.getInstance().getIslandLevel(uuid));

        return ASkyBlockAPI.getInstance().getIslandLevel(uuid);
    }

    public boolean hasIsland(final UUID uuid) {
        if (!isAvailable())
            return false;

        return ASkyBlockAPI.getInstance().hasIsland(uuid);
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
                plugin.debugMessage(ChatColor.RED + Dependency.ASKYBLOCK.getInternalString() + " has not been found!");
            }
            return false;
        } else {
            api = (ASkyBlock) get();

            if (api != null) {
                return true;
            } else {
                if (verbose) {
                    plugin.debugMessage(ChatColor.RED + Dependency.ASKYBLOCK.getInternalString()
                            + " has been found but cannot be used!");
                }
                return false;
            }
        }
    }
}
