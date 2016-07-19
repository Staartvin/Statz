package me.staartvin.statz.database;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.Table;
import me.staartvin.statz.database.datatype.sqlite.SQLiteTable;

public abstract class DatabaseConnector {
	private final Statz plugin;

	// All tables are stored here.
	private List<Table> tables = new ArrayList<Table>();
	
	// Make sure we have distinct tables
	public static final String prefix = "statz_";

	public static String databaseName = "statz";

	public Connection connection;

	public DatabaseConnector(final Statz instance) {
		plugin = instance;
	}

	/**
	 * Loads all tables into memory. This has to be run before
	 * {@linkplain #load()}
	 */
	public abstract void loadTables();

	/**
	 * Get a {@linkplain SQLiteTable} object by table name.
	 * 
	 * @param tableName Name of the table
	 * @return SQLiteTable object represented by that name or NULL if none was
	 *         found.
	 */
	public Table getTable(String tableName) {

		tableName = SQLiteConnector.prefix + tableName;

		for (final Table table : tables) {
			if (table.getTableName().equals(tableName)) {
				return table;
			}
		}

		return null;
	}

	/**
	 * Sets up a connection between the plugin and the sqlite database.
	 * 
	 * @return a connection to the database or null if it couldn't connect.
	 */
	public abstract Connection getConnection();

	/**
	 * Connects to sqlite database and automatically creates tables when needed.
	 */
	public abstract void load();

	/**
	 * Tests whether there is a valid connection available between sqlite
	 * database.
	 * <br>
	 * Will spit errors in the console when it could not properly connect.
	 */
	public void initialize() {
		connection = getConnection();

		// Something went wrong
		if (connection == null) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection!");
		}
		
		plugin.debugMessage(ChatColor.AQUA + "Statz is connected to its database!");

		return;
	}

	/**
	 * Gets a complete row of values from a specific table with a specific
	 * query.
	 * 
	 * @param table
	 *            Name of the table to get info from
	 * @param queries
	 *            A hashmap that will specify what queries should be applied.
	 *            <br>
	 *            You could call a hashmap with key: 'uuid' and value:
	 *            'c5f39a1d-3786-46a7-8953-d4efabf8880d'. This will make sure
	 *            that we only search for the value of <i>columnName</i> with
	 *            the condition that the 'uuid' column must be equal to
	 *            'c5f39a1d-3786-46a7-8953-d4efabf8880d'.
	 * @return a list of hashmaps where every key is a column and a key is the
	 *         value of
	 *         that column.
	 */
	public abstract List<Query> getObjects(final Table table, final Query queries);

	/**
	 * @see #getObjects(Table, Query)
	 * @param tableName Name of the table to get data from
	 * @param queries Queries to execute
	 * @return A hashmap that will specify what results should be applied.
	 *            <br>
	 *            You could call a hashmap with key: 'uuid' and value:
	 *            'c5f39a1d-3786-46a7-8953-d4efabf8880d'. This will make sure
	 *            that we set the value of <i>uuid</i> to
	 *            'c5f39a1d-3786-46a7-8953-d4efabf8880d'.
	 */
	public List<Query> getObjects(final String tableName, final Query queries) {
		return this.getObjects(this.getTable(tableName), queries);
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
	 */
	public abstract void setObjects(final Table table, final Query results);
	
	/**
	 * Instead of updating one single row, you can also perform a batch of updates.
	 * This can drastically improve update time. See {@link #setObjects(Table, Query)} for more info.
	 */
	public abstract void setBatchObjects(final Table table, final List<Query> queries);

//	/**
//	 * Closes sqlite connection.
//	 * 
//	 * @param ps PreparedStatement to be closed
//	 * @param rs ResultSet to be closed
//	 */
//	public void close(final PreparedStatement ps, final ResultSet rs) {
//		try {
//			if (ps != null)
//				ps.close();
//			if (rs != null)
//				rs.close();
//		} catch (final SQLException ex) {
//			plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
//		}
//	}

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
		this.tables = tables;
	}

	/**
	 * Add a table to the list of loaded tables.
	 * @param table Table to add.
	 */
	public void addTable(final Table table) {
		tables.add(table);
	}
}
