package me.staartvin.statz.hooks;

import me.staartvin.statz.Statz;
import org.bukkit.Bukkit;

public enum StatzDependency {

    DUMMY("Dummy", null);

    StatzDependency(String internalName) {
        this.internalName = internalName;
    }

    StatzDependency(String internalName, DependencyHandler handler) {
        this.internalName = internalName;
        this.setDependencyHandler(handler);
    }

    private final String internalName;
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
