package me.staartvin.statz.hooks.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;

/**
 * Handle all connections with WorldGuard
 * <p>
 * Date created: 18:06:52 21 feb. 2014
 * 
 * @author Staartvin
 * 
 */
public class WorldGuardHandler implements DependencyHandler {

    private final Statz plugin;
    private WorldGuardPlugin worldGuardAPI;

    public WorldGuardHandler(final Statz instance) {
        plugin = instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
     */
    @Override
    public Plugin get() {
        final Plugin wgPlugin = plugin.getServer().getPluginManager()
                .getPlugin(Dependency.WORLDGUARD.getInternalString());

        // WorldGuard may not be loaded
        if (wgPlugin == null || !(wgPlugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return wgPlugin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
     */
    @Override
    public boolean isAvailable() {
        return worldGuardAPI != null;
    }

    /**
     * Check to see if a player is in a specific region
     * 
     * @param player
     *            Player that needs to be checked
     * @param regionName
     *            Name of the region to be checked
     * @return true if the player is in that region; false otherwise.
     */
    public boolean isInRegion(final Player player, final String regionName) {
        if (!isAvailable())
            return false;

        if (player == null || regionName == null)
            return false;

        return this.isInRegion(player.getLocation(), regionName);
    }

    /**
     * @see #isInRegion(Player, String)
     * @param location
     * @param regionName
     * @return
     */
    public boolean isInRegion(Location location, String regionName) {

        if (location == null)
            return false;

        final RegionManager regManager = worldGuardAPI.getRegionManager(location.getWorld());

        if (regManager == null)
            return false;

        final ApplicableRegionSet set = regManager.getApplicableRegions(location);

        if (set == null)
            return false;

        for (final ProtectedRegion region : set) {
            final String name = region.getId();

            if (name.equalsIgnoreCase(regionName)) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
     */
    @Override
    public boolean isInstalled() {
        final WorldGuardPlugin wg = (WorldGuardPlugin) get();

        return wg != null && wg.isEnabled();
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
                plugin.debugMessage(ChatColor.RED + Dependency.WORLDGUARD.getInternalString() + " has not been found!");
            }
            return false;
        } else {
            worldGuardAPI = (WorldGuardPlugin) get();
            if (worldGuardAPI != null) {
                return true;
            } else {
                if (verbose) {
                    plugin.debugMessage(ChatColor.RED + Dependency.WORLDGUARD.getInternalString()
                            + " has been found but cannot be used!");
                }
                return false;
            }
        }
    }

}
