package me.staartvin.statz.hooks.handlers;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.wasteofplastic.askyblock.ASkyBlockAPI;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;
import us.talabrek.ultimateskyblock.uSkyBlock;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

/**
 * Handles all connections with uSkyBlock
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class uSkyBlockHandler implements DependencyHandler {

    private final Statz plugin;
    private uSkyBlockAPI api;

    public uSkyBlockHandler(final Statz instance) {
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
                .getPlugin(Dependency.USKYBLOCK.getInternalString());

        if (plugin == null || !(plugin instanceof uSkyBlockAPI)) {
            return null; // Maybe you want throw an exception instead
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
        // API is static class
        return isInstalled();
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
                plugin.debugMessage(
                        ChatColor.RED + Dependency.USKYBLOCK.getInternalString() + " has not been found!");
            }
            return false;
        } else {

            api = (uSkyBlockAPI) get();

            if (api != null) {
                return true;
            } else {
                if (verbose) {
                    plugin.debugMessage(ChatColor.RED + Dependency.USKYBLOCK.getInternalString()
                            + " has been found but cannot be used!");
                }
                return false;
            }
        }
    }
    
    public double getIslandLevel(Player player) {
        if (!isAvailable())
            return -1;

        return api.getIslandLevel(player);
    }
    
    public int getIslandRank(Player player) {
        if (!isAvailable())
            return -1;

        return api.getIslandRank(player).getRank();
    }
}
