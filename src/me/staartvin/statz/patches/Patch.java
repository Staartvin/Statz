package me.staartvin.statz.patches;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.DatabaseConnector;

public abstract class Patch {

	private Statz plugin;
	
	public Patch(Statz plugin) {
		this.plugin = plugin;
	}
	
	public Statz getStatz() {
		return plugin;
	}
	
	public abstract void applyMySQLChanges();
	
	public abstract void applySQLiteChanges();
	
	public abstract String getPatchName();
	
	public abstract int getPatchId();
	
	public DatabaseConnector getDatabaseConnector() {
		return plugin.getDatabaseConnector();
	}
}
