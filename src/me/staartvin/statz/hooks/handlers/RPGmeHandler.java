package me.staartvin.statz.hooks.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;
import net.flamedek.rpgme.RPGme;
import net.flamedek.rpgme.player.RPGPlayer;
import net.flamedek.rpgme.skills.SkillType;

/**
 * Handles all connections with RPGme
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class RPGmeHandler implements DependencyHandler {

    private RPGme api;
    private final Statz plugin;

    public RPGmeHandler(final Statz instance) {
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
                .getPlugin(Dependency.RPGME.getInternalString());

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof RPGme)) {
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
        final RPGme plugin = (RPGme) get();

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
                plugin.debugMessage(ChatColor.RED + Dependency.RPGME.getInternalString() + " has not been found!");
            }
            return false;
        } else {
            api = (RPGme) get();

            if (api != null) {
                return true;
            } else {
                if (verbose) {
                    plugin.debugMessage(ChatColor.RED + Dependency.RPGME.getInternalString()
                            + " has been found but cannot be used!");
                }
                return false;
            }
        }
    }

    private SkillType getSkill(String skillName) {
        return SkillType.getByAlias(skillName);
    }

    public int getSkillLevel(Player player, String skillName) {

        if (!this.isAvailable())
            return -1;

        SkillType type = this.getSkill(skillName);

        if (type == null)
            return -1;

        return RPGme.getAPI().getLevel(player, type);
    }

    public float getSkillExp(Player player, String skillName) {
        if (!this.isAvailable())
            return -1;

        SkillType type = this.getSkill(skillName);

        if (type == null)
            return -1;

        return RPGme.getAPI().getExp(player, type);
    }

    public int getTotalLevel(Player player) {
        if (!this.isAvailable())
            return -1;

        RPGPlayer RPGPlayer = RPGme.getAPI().get(player);

        if (RPGPlayer == null)
            return -1;

        return RPGPlayer.getSkillSet().getTotalLevel();
    }

    public int getCombatLevel(Player player) {
        if (!this.isAvailable())
            return -1;

        RPGPlayer RPGPlayer = RPGme.getAPI().get(player);

        if (RPGPlayer == null)
            return -1;

        return RPGPlayer.getSkillSet().getCombatLevel();
    }

    public int getAverageLevel(Player player) {
        if (!this.isAvailable())
            return -1;

        RPGPlayer RPGPlayer = RPGme.getAPI().get(player);

        if (RPGPlayer == null)
            return -1;

        return RPGPlayer.getSkillSet().getAverageLevel();
    }

    public List<UUID> getPlayersInParty(Player player) {
        List<UUID> uuids = new ArrayList<>();

        if (!this.isAvailable())
            return uuids;

        RPGPlayer RPGPlayer = RPGme.getAPI().get(player);

        if (RPGPlayer == null)
            return uuids;

        for (RPGPlayer rPlayer : RPGPlayer.getParty()) {
            uuids.add(rPlayer.getPlayerID());
        }

        return uuids;
    }

}
