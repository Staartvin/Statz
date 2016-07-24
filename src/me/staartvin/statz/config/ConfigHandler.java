package me.staartvin.statz.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;

public class ConfigHandler {

	private Statz plugin;

	public ConfigHandler(Statz plugin) {
		this.plugin = plugin;
	}

	public void loadConfig() {
		// Load defaults
		reloadConfig();

		plugin.getConfig().options().header("This is the config file of " + plugin.getDescription().getFullName()
				+ ". \nYou can configure Statz with this file."
				+ "\nThe 'track stats' option can either be true of false. When it is true, Statz will record data of players. If it is false, Statz won't record any data."
				+ "\nThe 'show database save message' option allows you to toggle the visibility of the 'Save message'. Every few minutes, the database of Statz is saved. If you"
				+ "set this option to false, Statz will still save the database, but won't show the message."
				+ "\nThe 'periodic save time' value represents the time (in seconds) after an update is sent to the database. The default is 10 seconds and I don't recommend changing it."
				+ "\nYou can experiment with it and see whether it improves performance for your server. A lower value means the database is updated more often, resulting in a decrement of performance.");

		plugin.getConfig().addDefault("track stats", true);
		plugin.getConfig().addDefault("show database save message", true);

		plugin.getConfig().addDefault("mysql.enabled", false);
		plugin.getConfig().addDefault("mysql.hostname", "localhost:3306");
		plugin.getConfig().addDefault("mysql.username", "root");
		plugin.getConfig().addDefault("mysql.password", "");
		plugin.getConfig().addDefault("mysql.database", "Statz");

		plugin.getConfig().addDefault("periodic save time", 10);
		
		plugin.getConfig().addDefault("disabled statistics", Arrays.asList("DISABLED_STAT_NAME_HERE", "OTHER_DISABLED_STAT_NAME"));

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

	public boolean isMySQLEnabled() {
		return plugin.getConfig().getBoolean("mysql.enabled", false);
	}

	public String getMySQLHostname() {
		return plugin.getConfig().getString("mysql.hostname", "localhost:3306");
	}

	public String getMySQLUsername() {
		return plugin.getConfig().getString("mysql.username", "root");
	}

	public String getMySQLPassword() {
		return plugin.getConfig().getString("mysql.password", "");
	}

	public String getMySQLDatabase() {
		return plugin.getConfig().getString("mysql.database", "Statz");
	}

	public int getPeriodicSaveTime() {
		return plugin.getConfig().getInt("periodic save time", 10);
	}
	
	public List<PlayerStat> getDisabledStats() {
		List<String> disabledStatsString = plugin.getConfig().getStringList("disabled statistics");
		List<PlayerStat> disabledStats = new ArrayList<>();
		
		for (String disabledStatString: disabledStatsString) {
			PlayerStat stat = null;
			try {
				stat = PlayerStat.valueOf(disabledStatString.toUpperCase().replace(" ", "_"));
			} catch (IllegalArgumentException e) {
				
			}
			
			
			if (stat == null) continue;
			
			disabledStats.add(stat);
		}
		
		return disabledStats;
	}
	
	public boolean isStatDisabled(PlayerStat stat) {
		return this.getDisabledStats().contains(stat);
	}
}
