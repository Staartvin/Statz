package me.staartvin.statz;

import org.bukkit.plugin.java.JavaPlugin;

import me.staartvin.statz.api.API;
import me.staartvin.statz.database.SQLiteConnector;
import me.staartvin.statz.datamanager.DataManager;
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
		
		// Load API
		this.setStatzAPI(new API(this));

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
		this.getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
		this.getServer().getPluginManager().registerEvents(new EatFoodListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerTakeDamageListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerShearListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
		this.getServer().getPluginManager().registerEvents(new VehicleMoveListener(this), this);
		this.getServer().getPluginManager().registerEvents(new CraftItemListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerGainXPListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerVoteListener(this), this);
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
}
