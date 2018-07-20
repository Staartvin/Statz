package me.staartvin.statz.database;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.database.datatype.Table;
import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public abstract class DatabaseConnector {
	private final Statz plugin;

	// All tables are stored here.
	private static List<Table> tables = new ArrayList<Table>();

	// Make sure we have distinct tables
	public static final String prefix = "statz_";

	public static String databaseName = "statz";

	public DatabaseConnector(final Statz instance) {
		plugin = instance;
	}

	/**
	 * Loads all tables into memory. This has to be run before
	 * {@linkplain #load()}
	 */
	public abstract void loadTables();

	/**
	 * Get a {@linkplain Table} object by table name.
	 * 
	 * @param tableName Name of the table
	 * @return Table object represented by that name or NULL if none was
	 *         found.
	 */
	public static Table getTable(String tableName) {

        // Add prefix if it wasn't added before.
        if (!tableName.startsWith(SQLiteConnector.prefix)) {
            tableName = SQLiteConnector.prefix + tableName;
        }

		for (final Table table : tables) {
			if (table.getTableName().equals(tableName)) {
				return table;
			}
		}

		return null;
	}

	/**
	 * @see #getTable(String)
	 * @param stat
	 * @return
	 */
	public static Table getTable(PlayerStat stat) {
		return getTable(stat.getTableName());
	}

	/**
	 * Sets up a connection between the plugin and the database.
	 * 
	 * @return a connection to the database or null if it couldn't connect.
	 */
	public abstract Connection getConnection();

	/**
	 * Connects to sqlite database and automatically creates tables when needed.
	 */
	public abstract void load();

	/**
	 * Tests whether there is a valid connection available between the
	 * database.
	 * <br>
	 * Will spit errors in the console when it could not properly connect.
	 */
	public void initialize() {
		Connection connection = getConnection();

		// Something went wrong
		if (connection == null) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection!");
			return;
		}

		this.refreshConnection();

		plugin.debugMessage(ChatColor.AQUA + "Statz is connected to its database!");

		return;
	}

	/**
	 * Gets a complete row of values from a specific table with a specific
	 * query.
	 * 
	 * @param table
	 *            Name of the table to get info from
	 * @param requirements
	 *            Requirements that should be met for this data to retrieve it. See {@link RowRequirement} for more
	 *            info about requirements.
	 * @return a list of {@link Query} objects, each representing one row in the database.
	 */
	public abstract List<Query> getObjects(final Table table, final RowRequirement... requirements);

	/**
	 * @see #getObjects(Table, RowRequirement...)
	 * @param tableName Name of the table to get data from
	 * @param requirements Requirements that the requested data should adhere to.
	 * @return a list of {@link Query} objects, each representing one row in the database.
	 */
	public List<Query> getObjects(final String tableName, final RowRequirement... requirements) {
		return this.getObjects(getTable(tableName), requirements);
	}

	/**
	 * Sets values to columns in a specific table. <br>
	 * <b>Note:</b> a linked hashmap is used to make sure that the order of the
	 * elements does not change.
	 * 
	 * @param table
	 *            Table to change values in.
	 * @param results
	 *            A hashmap that will specify what results should be applied.
	 *            <br>
	 *            You could call a hashmap with key: 'uuid' and value:
	 *            'c5f39a1d-3786-46a7-8953-d4efabf8880d'. This will make sure
	 *            that we set the value of <i>uuid</i> to
	 *            'c5f39a1d-3786-46a7-8953-d4efabf8880d'.
	 * @param mode What mode should the update run in? '1' means 'override current value in database', '2' means 'add the value to current value in database'
	 */
	public abstract void setObjects(final Table table, final Query results, int mode);

	/**
	 * Instead of updating one single row, you can also perform a batch of updates.
	 * This can drastically improve update time. See {@link #setObjects(Table, Query, int)} for more info.
	 */
	public abstract void setBatchObjects(final Table table, final List<Query> queries, int mode);

	/**
	 * Get a list of currently loaded tables.
	 * @return a list of loaded tables.
	 */
	public List<Table> getTables() {
		return tables;
	}

	/**
	 * Set the list of loaded tables.
	 * @param tables Tables to set the loaded list to.
	 */
	public void setTables(final List<Table> tables) {
		DatabaseConnector.tables = tables;
	}

	/**
	 * Add a table to the list of loaded tables.
	 * @param table Table to add.
	 */
	public void addTable(final Table table) {
		tables.add(table);
	}

	/**
	 * Removes all data in the database of the given UUID.
	 * @param uuid UUID to remove data of.
	 */
	public abstract void purgeData(UUID uuid);

	/**
	 * Refresh connection with database
	 */
	private void refreshConnection() {
		// Run this query to refresh the connection.
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				Connection conn = null;
				PreparedStatement ps = null;

				try {
					conn = getConnection();
					ps = conn.prepareStatement("SELECT 1");
					ps.executeQuery();

					return;
				} catch (final SQLException ex) {
					plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL statement:", ex);
				} finally {
					try {
						if (ps != null)
							ps.close();
						//if (conn != null)
						//conn.close();
					} catch (final SQLException ex) {
						plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
					}
				}

			}
		});
	}

    /**
     * Send a specific query (as a string) to the database. It is not recommended to use this method to obtain data
     * about the database. Use {@link #setObjects(Table, Query, int)} and {@link #getObjects(Table, Query)} instead.
     * @param query Query to perform
     * @param wantResult whether you want the result set back.
     * @return the result set if wantResult is true, otherwise it will return null.
     */
	public abstract ResultSet sendQuery(String query, boolean wantResult);

    /**
     * Send specific queries (as strings) to the database. It is not recommended to use this method to obtain data
     * about the database. Use {@link #setObjects(Table, Query, int)} and {@link #getObjects(Table, Query)} instead.
     * @param queries Queries to perform
     * @param wantResult whether you want the result sets back.
     * @return a list of result sets (in order of executing the queries) if wantResult is true, otherwise it will return null.
     */
	public abstract List<ResultSet> sendQueries(List<String> queries, boolean wantResult) throws Exception;
}
