package me.staartvin.statz;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.staartvin.statz.api.API;
import me.staartvin.statz.config.ConfigHandler;
import me.staartvin.statz.database.SQLiteConnector;
import me.staartvin.statz.datamanager.DataManager;
import me.staartvin.statz.datamanager.DataPoolManager;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyManager;
import me.staartvin.statz.listeners.CraftItemListener;
import me.staartvin.statz.listeners.EatFoodListener;
import me.staartvin.statz.listeners.EntityDeathListener;
import me.staartvin.statz.listeners.PlayerBlockBreakListener;
import me.staartvin.statz.listeners.PlayerBlockPlaceListener;
import me.staartvin.statz.listeners.PlayerDeathListener;
import me.staartvin.statz.listeners.PlayerFishListener;
import me.staartvin.statz.listeners.PlayerGainXPListener;
import me.staartvin.statz.listeners.PlayerJoinListener;
import me.staartvin.statz.listeners.PlayerMoveListener;
import me.staartvin.statz.listeners.PlayerShearListener;
import me.staartvin.statz.listeners.PlayerTakeDamageListener;
import me.staartvin.statz.listeners.PlayerVoteListener;
import me.staartvin.statz.listeners.VehicleMoveListener;

public class Statz extends JavaPlugin {

	private SQLiteConnector sqlConnector;
	private DataManager dataManager;
	private API statzAPI;
	private DataPoolManager dataPoolManager;
	private DependencyManager depManager;
	private ConfigHandler configHandler;

	@Override
	public void onEnable() {
		
		// Load hooks
		this.setDependencyManager(new DependencyManager(this));
		
		// Load SQL connector
		this.setSqlConnector(new SQLiteConnector(this));

		// Set up Data Pool Manager
		this.setDataPoolManager(new DataPoolManager(this));

		// Load tables into hashmap
		this.getSqlConnector().loadTables();

		// Create and load database
		this.getSqlConnector().load();
		
		// Load confighandler
		this.setConfigHandler(new ConfigHandler(this));
		
		// Load config with default values
		this.getConfigHandler().loadConfig();

		// Register listeners
		this.registerListeners();

		// Load data manager as database is loaded!
		this.setDataManager(new DataManager(this));

		// Load API
		this.setStatzAPI(new API(this));

		// Send pool update every 10 seconds
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {
				getDataPoolManager().sendPool();
			}
		}, 20, 20 * 10);
		
		// Do a check on all present hooks
		this.getDependencyManager().loadDependencies();		

		this.getLogger().info(this.getDescription().getFullName() + " has been enabled!");
	}

	@Override
	public void onDisable() {
		this.getLogger().info(this.getDescription().getFullName() + " has been disabled!");
	}

	private void registerListeners() {
		if (!this.getConfigHandler().getStatsTracking()) {
			this.debugMessage(ChatColor.GOLD + "Statz won't track stats of any player!");
			return; // We don't track stats, so we don't register listeners
		}
		
		this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerFishListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerBlockPlaceListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerBlockBreakListener(this), this);
		this.getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
		this.getServer().getPluginManager().registerEvents(new EatFoodListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerTakeDamageListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerShearListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
		this.getServer().getPluginManager().registerEvents(new VehicleMoveListener(this), this);
		this.getServer().getPluginManager().registerEvents(new CraftItemListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerGainXPListener(this), this);
		
		if (this.getDependencyManager().isAvailable(Dependency.VOTIFIER)) {
			this.getServer().getPluginManager().registerEvents(new PlayerVoteListener(this), this);
		}	
	}
	
	public void debugMessage(String message) {
		this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[Statz debug] " + message));
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

	public API getStatzAPI() {
		return statzAPI;
	}

	public void setStatzAPI(API statzAPI) {
		this.statzAPI = statzAPI;
	}

	public DataPoolManager getDataPoolManager() {
		return dataPoolManager;
	}

	public void setDataPoolManager(DataPoolManager dataPoolManager) {
		this.dataPoolManager = dataPoolManager;
	}

	public DependencyManager getDependencyManager() {
		return depManager;
	}

	public void setDependencyManager(DependencyManager depManager) {
		this.depManager = depManager;
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public void setConfigHandler(ConfigHandler configHandler) {
		this.configHandler = configHandler;
	}
}
