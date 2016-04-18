package me.staartvin.statz;

import org.bukkit.plugin.java.JavaPlugin;

import me.staartvin.statz.database.SQLiteConnector;
import me.staartvin.statz.datamanager.DataManager;
import me.staartvin.statz.listeners.PlayerBlockBreakListener;
import me.staartvin.statz.listeners.PlayerBlockPlaceListener;
import me.staartvin.statz.listeners.PlayerDeathListener;
import me.staartvin.statz.listeners.PlayerFishListener;
import me.staartvin.statz.listeners.PlayerJoinListener;

public class Statz extends JavaPlugin {

	private SQLiteConnector sqlConnector;
	private DataManager dataManager;

	@Override
	public void onEnable() {
		this.setSqlConnector(new SQLiteConnector(this));

		// Load tables into hashmap
		this.getSqlConnector().loadTables();

		// Create and load database
		this.getSqlConnector().load();

		// Register listeners
		this.registerListeners();

		// Load data manager as database is loaded!
		this.setDataManager(new DataManager(this));

		this.getLogger().info(this.getDescription().getFullName() + " has been enabled!");
	}

	@Override
	public void onDisable() {
		this.getLogger().info(this.getDescription().getFullName() + " has been disabled!");
	}

	private void registerListeners() {
		this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerFishListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerBlockPlaceListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerBlockBreakListener(this), this);
	}

	public SQLiteConnector getSqlConnector() {
		return sqlConnector;
	}

	public void setSqlConnector(final SQLiteConnector sqlConnector) {
		this.sqlConnector = sqlConnector;
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(final DataManager dataManager) {
		this.dataManager = dataManager;
	}
}
