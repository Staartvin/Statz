package me.staartvin.statz.hooks;

import me.staartvin.statz.hooks.handlers.PluginLibraryHandler;
import org.bukkit.Bukkit;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.handlers.NuVotifierHandler;
import me.staartvin.statz.hooks.handlers.VotifierHandler;

public enum Dependency {

    VOTIFIER("Votifier", new VotifierHandler()),
    NUVOTIFIER("Votifier", new NuVotifierHandler()),
    PLUGINLIBRARY("PluginLibrary", new PluginLibraryHandler());

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
