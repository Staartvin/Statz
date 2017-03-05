package me.staartvin.statz.hooks;

import org.bukkit.Bukkit;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.handlers.AFKTerminatorHandler;
import me.staartvin.statz.hooks.handlers.ASkyBlockHandler;
import me.staartvin.statz.hooks.handlers.AcidIslandHandler;
import me.staartvin.statz.hooks.handlers.AdvancedAchievementsHandler;
import me.staartvin.statz.hooks.handlers.BattleLevelsHandler;
import me.staartvin.statz.hooks.handlers.EssentialsHandler;
import me.staartvin.statz.hooks.handlers.FactionsHandler;
import me.staartvin.statz.hooks.handlers.GriefPreventionHandler;
import me.staartvin.statz.hooks.handlers.JobsHandler;
import me.staartvin.statz.hooks.handlers.McMMOHandler;
import me.staartvin.statz.hooks.handlers.NuVotifierHandler;
import me.staartvin.statz.hooks.handlers.OnTimeHandler;
import me.staartvin.statz.hooks.handlers.RPGmeHandler;
import me.staartvin.statz.hooks.handlers.RoyalCommandsHandler;
import me.staartvin.statz.hooks.handlers.StatisticsAPIHandler;
import me.staartvin.statz.hooks.handlers.StatsAPIHandler;
import me.staartvin.statz.hooks.handlers.UltimateCoreHandler;
import me.staartvin.statz.hooks.handlers.VaultHandler;
import me.staartvin.statz.hooks.handlers.VotifierHandler;
import me.staartvin.statz.hooks.handlers.WorldGuardHandler;
import me.staartvin.statz.hooks.handlers.uSkyBlockHandler;

public enum Dependency {

    VOTIFIER("Votifier", new VotifierHandler(getStatz())),
    JOBS("Jobs", new JobsHandler(getStatz())),
    MCMMO("mcMMO", new McMMOHandler(getStatz())),
    ASKYBLOCK("ASkyBlock", new ASkyBlockHandler(getStatz())),
    ACIDISLAND("AcidIsland", new AcidIslandHandler(getStatz())),
    WORLDGUARD("WorldGuard", new WorldGuardHandler(getStatz())),
    ROYAL_COMMANDS("RoyalCommands", new RoyalCommandsHandler(getStatz())),
    ON_TIME("OnTime", new OnTimeHandler(getStatz())),
    AFKTERMINATOR("afkTerminator", new AFKTerminatorHandler(getStatz())),
    ESSENTIALS("Essentials", new EssentialsHandler(getStatz())),
    FACTIONS("Factions", new FactionsHandler(getStatz())),
    STATISTICS("Statistics", new StatisticsAPIHandler(getStatz())),
    STATS("Stats", new StatsAPIHandler(getStatz())),
    ULTIMATE_CORE("UltimateCore", new UltimateCoreHandler(getStatz())),
    VAULT("Vault", new VaultHandler(getStatz())),
    GRIEF_PREVENTION("GriefPrevention", new GriefPreventionHandler(getStatz())),
    RPGME("RPGme", new RPGmeHandler(getStatz())),
    NUVOTIFIER("Votifier", new NuVotifierHandler(getStatz())),
    ADVANCEDACHIEVEMENTS("AdvancedAchievements", new AdvancedAchievementsHandler(getStatz())),
    BATTLELEVELS("BattleLevels", new BattleLevelsHandler(getStatz())),
    USKYBLOCK("uSkyBlock", new uSkyBlockHandler(getStatz())),;

    Dependency(String internalName) {
        this.internalName = internalName;
    }

    Dependency(String internalName, DependencyHandler handler) {
        this.internalName = internalName;
        this.setDependencyHandler(handler);
    }

    private String internalName;
    private DependencyHandler dependencyHandler;

    public String getInternalString() {
        return this.internalName;
    }

    public DependencyHandler getDependencyHandler() {
        return dependencyHandler;
    }

    private void setDependencyHandler(DependencyHandler dependencyHandler) {
        this.dependencyHandler = dependencyHandler;
    }

    public static Statz getStatz() {
        return (Statz) Bukkit.getPluginManager().getPlugin("Statz");
    }
}
