package me.staartvin.statz.hooks.handlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.hm.achievement.AdvancedAchievements;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;

/**
 * Handles all connections with AdvancedAchievements
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class AdvancedAchievementsHandler implements DependencyHandler {

    private final Statz plugin;
    private AdvancedAchievements api;

    public AdvancedAchievementsHandler(final Statz instance) {
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
                .getPlugin(Dependency.ADVANCEDACHIEVEMENTS.getInternalString());

        if (plugin == null || !(plugin instanceof AdvancedAchievements)) {
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
        final AdvancedAchievements plugin = (AdvancedAchievements) get();

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
                        ChatColor.RED + Dependency.ADVANCEDACHIEVEMENTS.getInternalString() + " has not been found!");
            }
            return false;
        } else {

            api = (AdvancedAchievements) get();

            if (api != null) {
                return true;
            } else {
                if (verbose) {
                    plugin.debugMessage(ChatColor.RED + Dependency.ADVANCEDACHIEVEMENTS.getInternalString()
                            + " has been found but cannot be used!");
                }
                return false;
            }
        }
    }
    
    public boolean hasAchievement(Player player, String achievementName) {
        if (!this.isAvailable()) {
            return false;
        }
        
        return api.getPoolsManager().hasPlayerAchievement(player, achievementName);
    }
    
    public int getNumberOfAchievements(Player player) {
        if (!this.isAvailable()) {
            return -1;
        }
        
        return api.getDb().getPlayerAchievementsAmount(player);
    }
}
