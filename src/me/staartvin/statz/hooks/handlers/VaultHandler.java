package me.staartvin.statz.hooks.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * Handles all connections with Vault
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class VaultHandler implements DependencyHandler {

    public static Economy economy = null;
    public static Permission permission = null;
    private Vault api;
    private final Statz plugin;

    public VaultHandler(final Statz instance) {
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
                .getPlugin(Dependency.VAULT.getInternalString());

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof Vault)) {
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
        return api != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
     */
    @Override
    public boolean isInstalled() {
        final Vault plugin = (Vault) get();

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
                plugin.debugMessage(ChatColor.RED + Dependency.VAULT.getInternalString() + " has not been found!");
            }
            return false;
        } else {
            api = (Vault) get();

            if (api != null && setupEconomy() && setupPermissions()) {
                return true;
            } else {
                if (verbose) {
                    plugin.debugMessage(ChatColor.RED + Dependency.VAULT.getInternalString()
                            + " has been found but cannot be used!");
                }
                return false;
            }
        }
    }

    private boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
}
