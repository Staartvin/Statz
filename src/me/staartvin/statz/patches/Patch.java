package me.staartvin.statz.patches;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.DatabaseConnector;

public abstract class Patch {

    public Statz plugin;
	
	public Patch(Statz plugin) {
		this.plugin = plugin;
	}
	
	public Statz getStatz() {
		return plugin;
	}
	
	public abstract boolean applyMySQLChanges();
	
	public abstract boolean applySQLiteChanges();
	
	public abstract String getPatchName();
	
	public abstract int getPatchId();

    /**
     * Check to see if this patch should be applied. If this returns false, the patch is not needed.
     *
     * @return true if the patch should be applied, false if not.
     */
    public abstract boolean isPatchNeeded();
	
	public DatabaseConnector getDatabaseConnector() {
		return plugin.getDatabaseConnector();
	}
}
