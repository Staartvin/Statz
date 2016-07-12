package me.staartvin.statz.config;

import me.staartvin.statz.Statz;

public class ConfigHandler {
	
	private Statz plugin;
	
	public ConfigHandler(Statz plugin) {
		this.plugin = plugin;
	}
	
	public void loadConfig() {
		// Load defaults
		reloadConfig();
		
		plugin.getConfig().options()
		.header("This is the config file of " + plugin.getDescription().getFullName() + ". \nYou can configure Statz with this file."
				+ "\nThe 'track stats' option can either be true of false. When it is true, Statz will record data of players. If it is false, Statz won't record any data."
				+ "\nThe 'show database save message' option allows you to toggle the visibility of the 'Save message'. Every few minutes, the database of Statz is saved. If you"
				+ "set this option to false, Statz will still save the database, but won't show the message.");
		
		plugin.getConfig().addDefault("track stats", true);
		plugin.getConfig().addDefault("show database save message", true);
		
		plugin.getConfig().options().copyDefaults(true);

		this.saveConfig();
	}
	
	public void reloadConfig() {
		plugin.reloadConfig();
	}
	
	public void saveConfig() {
		plugin.saveConfig();
	}
	
	public void setStatsTracking(boolean value) {
		plugin.getConfig().set("track stats", value);
		saveConfig();
	}
	
	public boolean getStatsTracking() {
		return plugin.getConfig().getBoolean("track stats", true);
	}
	
	public boolean shouldShowDatabaseSave() {
		return plugin.getConfig().getBoolean("show database save message", true);
	}
	
	public void setShowDatabaseSave(boolean value) {
		plugin.getConfig().set("show database save message", value);
		plugin.saveConfig();
	}
}
