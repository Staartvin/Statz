package me.staartvin.statz;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.staartvin.statz.api.API;
import me.staartvin.statz.config.ConfigHandler;
import me.staartvin.statz.database.DatabaseConnector;
import me.staartvin.statz.database.MySQLConnector;
import me.staartvin.statz.database.SQLiteConnector;
import me.staartvin.statz.datamanager.DataManager;
import me.staartvin.statz.datamanager.DataPoolManager;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
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
import me.staartvin.statz.util.StatzUtil;

public class Statz extends JavaPlugin {

	private DatabaseConnector connector;
	private DataManager dataManager;
	private API statzAPI;
	private DataPoolManager dataPoolManager;
	private DependencyManager depManager;
	private ConfigHandler configHandler;

	@Override
	public void onEnable() {

		// Load confighandler
		this.setConfigHandler(new ConfigHandler(this));

		// Load config with default values
		this.getConfigHandler().loadConfig();

		// Load hooks
		this.setDependencyManager(new DependencyManager(this));

		// Load SQL connector
		if (this.getConfigHandler().isMySQLEnabled()) {
			this.debugMessage(ChatColor.GOLD + "Using MySQL database!");
			this.setSqlConnector(new MySQLConnector(this));
		} else {
			this.debugMessage(ChatColor.GOLD + "Using SQLite database!");
			this.setSqlConnector(new SQLiteConnector(this));
		}

		// Set up Data Pool Manager
		this.setDataPoolManager(new DataPoolManager(this));

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

		// Send pool update every 10 seconds
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {
				getDataPoolManager().sendPool();
			}
		}, 20, 20 * this.getConfigHandler().getPeriodicSaveTime());

		// Do performance test
		//this.doPerformanceTest();

		// Do a check on all present hooks
		this.getDependencyManager().loadDependencies();

		this.getLogger().info(this.getDescription().getFullName() + " has been enabled!");
	}

	@Override
	public void onDisable() {

		debugMessage(ChatColor.RED + "Saving updates to database!");

		// Send the complete pool.
		this.getDataPoolManager().forceSendPool();

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
		this.getServer().getConsoleSender()
				.sendMessage(ChatColor.translateAlternateColorCodes('&', "[Statz debug] " + message));
	}

	public void doPerformanceTest() {
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {

				debugMessage("Start stresstest");

				// 38 random players
				ArrayList<UUID> uuids = new ArrayList<UUID>();
				uuids.add(UUID.fromString("cb58fd93-567f-45f0-8a60-171f15b66e5f"));
				uuids.add(UUID.fromString("d44fe94f-6c26-408c-b05e-6746b3b79b7e"));
				uuids.add(UUID.fromString("48e11f72-2310-406f-ae47-1117e8d08547"));
				uuids.add(UUID.fromString("9f6ddfae-7398-4973-82a0-d1fb67635956"));
				uuids.add(UUID.fromString("0bec5711-4316-4a8f-b61b-2a119e652082"));
				uuids.add(UUID.fromString("5169e667-c43c-41cb-80b3-9b127ad1d64a"));
				uuids.add(UUID.fromString("54db3bd8-b206-411d-900f-ba56698a049e"));
				uuids.add(UUID.fromString("6b831421-b03b-45d6-bbee-c660c1ef3abf"));
				uuids.add(UUID.fromString("dcdaa593-6b01-43ea-9792-372f6ca4a646"));
				uuids.add(UUID.fromString("7747121e-1d51-44e3-8c7c-98deaf2d0f41"));
				uuids.add(UUID.fromString("311959d1-038f-4e82-9f28-d7fdc06b917e"));
				uuids.add(UUID.fromString("dee81da3-5d11-4630-964b-66cde2c3a50c"));
				uuids.add(UUID.fromString("ec9e8bd0-cdad-46b1-9da7-0d187a187d4a"));
				uuids.add(UUID.fromString("cc41ffa3-2f70-4b18-9ca8-c1e9af9f5041"));
				uuids.add(UUID.fromString("0f42c47b-5c9e-4b8c-a21d-9fa83239dad0"));
				uuids.add(UUID.fromString("d0853e6e-30d8-421b-b86b-bd5e635f01e1"));
				uuids.add(UUID.fromString("3c7db14d-ac4b-4e35-b2c6-3b2237f382be"));
				uuids.add(UUID.fromString("86464dd9-9a02-4cd0-895e-4c60b5766108"));
				uuids.add(UUID.fromString("2361ca08-44dc-4541-8fb1-4f02f3f9f222"));
				uuids.add(UUID.fromString("e8441bb2-2fa8-498c-919d-8aaba24bc414"));
				uuids.add(UUID.fromString("3c166720-24ea-4f0a-8630-04c7ee222c86"));
				uuids.add(UUID.fromString("6128dc3e-e826-4fe7-94df-6d6a2b3b4d38"));
				uuids.add(UUID.fromString("b1adf2ec-eed6-46d6-a770-40f409651913"));
				uuids.add(UUID.fromString("4de3afa2-b921-472a-93d5-a077ffdc40a9"));
				uuids.add(UUID.fromString("4c607d4b-3816-40de-b384-389f3b6ea8c4"));
				uuids.add(UUID.fromString("8112554c-ef4a-46bd-bb21-61ac3a7b30a2"));
				uuids.add(UUID.fromString("2c757641-1213-4a23-8285-5d3e578a525c"));
				uuids.add(UUID.fromString("d2feb792-e293-456e-8f65-d2c6557a3475"));
				uuids.add(UUID.fromString("67ac9634-44f4-481e-b6c9-7a6a9d03d041"));
				uuids.add(UUID.fromString("3e14a919-2e96-447e-a0c1-095123073c3e"));
				uuids.add(UUID.fromString("66869715-7435-4e87-b8a2-3bfafb166428"));
				uuids.add(UUID.fromString("9c68ddca-a37b-4873-936d-dc4160d4a155"));
				uuids.add(UUID.fromString("9935c756-b677-4769-a54c-78406c9954f1"));
				uuids.add(UUID.fromString("44359973-2977-4a96-bdd3-3ceaea6eb301"));
				uuids.add(UUID.fromString("eb37de95-404b-43ef-81cc-84f22b9d5d7f"));
				uuids.add(UUID.fromString("e373fd60-a1fb-41a1-95d0-df9c0ffb77c9"));
				uuids.add(UUID.fromString("8e1edca1-cc02-4d19-8226-366530ac649b"));
				uuids.add(UUID.fromString("0d0523ca-89c7-4ac8-94c5-da279091a6a2"));

				ArrayList<String> move = new ArrayList<>();
				move.add("WALK");
				move.add("FLY");
				move.add("MINECART");
				move.add("PIG IN MINECART");
				move.add("HORSE IN MINECART");
				move.add("PIG");
				move.add("BOAT");

				long startTime = System.currentTimeMillis();

				// 100 million interations
				for (int i = 0; i < 10000; i++) {
					// Send 100 million updates

					//debugMessage("--------------------------");
					// Get a random UUID
					Random randomizer = new Random();
					UUID random = uuids.get(randomizer.nextInt(uuids.size()));

					//debugMessage("i: " + i + ", UUID: " + random);

					final PlayerStat stat = PlayerStat.DISTANCE_TRAVELLED;

					String movementType = move.get(new Random().nextInt(move.size()));

					int distTravelled = new Random().nextInt(5);

					//debugMessage("Dist value: " + distTravelled);

					if (distTravelled == 0) {
						continue;
					}

					// Get player info.
					final PlayerInfo info = getDataManager().getPlayerInfo(random, stat,
							StatzUtil.makeQuery("world", "world", "moveType", movementType));

					// Get current value of stat.
					int currentValue = 0;

					// Check if it is valid!
					if (info.isValid()) {
						currentValue += info.getTotalValue();
					}

					//debugMessage("Current value: " + currentValue);

					// Update value to new stat.
					getDataManager().setPlayerInfo(random, stat, StatzUtil.makeQuery("uuid", random, "value",
							(currentValue + distTravelled), "moveType", movementType, "world", "world"));
				}

				long totalTime = System.currentTimeMillis() - startTime;

				debugMessage("End of stresstest");
				debugMessage("Took " + totalTime + " ms");
				//getDataPoolManager().printPool();

			}
		}, 20 * 10, 20 * 90);
	}

	public DatabaseConnector getSqlConnector() {
		return connector;
	}

	public void setSqlConnector(final DatabaseConnector sqlConnector) {
		this.connector = sqlConnector;
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
