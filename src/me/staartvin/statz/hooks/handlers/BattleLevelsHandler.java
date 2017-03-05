package me.staartvin.statz.hooks.handlers;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import me.robin.battlelevels.api.BattleLevelsAPI;
import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;

/**
 * Handles all connections with BatteLevels
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class BattleLevelsHandler implements DependencyHandler {

    private final Statz plugin;

    public BattleLevelsHandler(final Statz instance) {
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
                .getPlugin(Dependency.BATTLELEVELS.getInternalString());

        if (plugin == null) {
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
                        ChatColor.RED + Dependency.BATTLELEVELS.getInternalString() + " has not been found!");
            }
            return false;
        } else {
            if (verbose) {
                plugin.debugMessage(ChatColor.RED + Dependency.BATTLELEVELS.getInternalString()
                        + " has been found but cannot be used!");
            }
            return false;
        }
    }

    public double getKillDeathRatio(UUID uuid) {

        if (!this.isAvailable()) {
            return -1;
        }

        return BattleLevelsAPI.getKdr(uuid);
    }

    public int getKills(UUID uuid) {

        if (!this.isAvailable()) {
            return -1;
        }

        return BattleLevelsAPI.getKills(uuid);
    }
    
    public int getDeaths(UUID uuid) {

        if (!this.isAvailable()) {
            return -1;
        }

        return BattleLevelsAPI.getDeaths(uuid);
    }
    
    public int getLevel(UUID uuid) {

        if (!this.isAvailable()) {
            return -1;
        }

        return BattleLevelsAPI.getLevel(uuid);
    }
    
    public double getScore(UUID uuid) {

        if (!this.isAvailable()) {
            return -1;
        }

        return BattleLevelsAPI.getScore(uuid);
    }
    
    public int getKillStreak(UUID uuid) {

        if (!this.isAvailable()) {
            return -1;
        }

        return BattleLevelsAPI.getKillstreak(uuid);
    }
    
    public int getTopKillStreak(UUID uuid) {

        if (!this.isAvailable()) {
            return -1;
        }

        return BattleLevelsAPI.getTopKillstreak(uuid);
    }
}
