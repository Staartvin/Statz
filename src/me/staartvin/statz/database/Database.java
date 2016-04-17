package me.staartvin.statz.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.SQLiteTable;
import me.staartvin.statz.database.datatype.SQLiteTable.SQLDataType;
import me.staartvin.statz.util.StatzUtil;

public abstract class Database {
	private final Statz plugin;

	// All tables are stored here.
	private List<SQLiteTable> tables = new ArrayList<SQLiteTable>();

	public Connection connection;

	public Database(final Statz instance) {
		plugin = instance;
	}

	/**
	 * Loads all tables into memory. This has to be run before
	 * {@linkplain #load()}
	 */
	public void loadTables() {
		// UUID table to look up uuid of players
		SQLiteTable newTable = new SQLiteTable("players");

		// Populate table
		newTable.addColumn("uuid", true, SQLDataType.TEXT); // UUID of the player
		newTable.addColumn("playerName", false, SQLDataType.TEXT); // Name of player
		this.addTable(newTable);

		// How many times did a player join this server?
		newTable = new SQLiteTable("joins");

		newTable.addColumn("uuid", true, SQLDataType.TEXT); // UUID of the player
		newTable.addColumn("value", false, SQLDataType.INT); // How many times did the player join.

		this.addTable(newTable);

		// How many times did a player die?
		newTable = new SQLiteTable("deaths");

		newTable.addColumn("uuid", true, SQLDataType.TEXT); // UUID of the player
		newTable.addColumn("value", false, SQLDataType.INT); // How many times did the player die.

		this.addTable(newTable);

	}

	/**
	 * Get a {@linkplain SQLiteTable} object by table name.
	 * 
	 * @param tableName Name of the table
	 * @return SQLiteTable object represented by that name or NULL if none was
	 *         found.
	 */
	public SQLiteTable getSQLiteTable(String tableName) {

		tableName = SQLiteConnector.prefix + tableName;

		for (final SQLiteTable table : tables) {
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
	public abstract Connection getSQLConnection();

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
		connection = getSQLConnection();

		// Something went wrong
		if (connection == null) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection!");
		}

		return;
	}

	/**
	 * Gets a column value from a specific table with a specific query.
	 * 
	 * @param table
	 *            Name of the table to get info from
	 * @param columnName
	 *            Name of the column to get the value from
	 * @param queries
	 *            A hashmap that will specify what queries should be applied.
	 *            <br>
	 *            You could call a hashmap with key: 'uuid' and value:
	 *            'c5f39a1d-3786-46a7-8953-d4efabf8880d'. This will make sure
	 *            that we only search for the value of <i>columnName</i> with
	 *            the condition that the 'uuid' column must be equal to
	 *            'c5f39a1d-3786-46a7-8953-d4efabf8880d'.
	 * 
	 * @return An object (either integer or string) if anything was found
	 *         matching the conditions. NULL otherwise.
	 */
	public Object getObject(final SQLiteTable table, final String columnName, final HashMap<String, String> queries) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(
					"SELECT * FROM " + table.getTableName() + " WHERE " + StatzUtil.convertQuery(queries) + ";");

			rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getObject(columnName);
			}
		} catch (final SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (final SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
			}
		}
		return null;
	}
	
	/**
	 * @see #getObject(SQLiteTable, String, HashMap)
	 * @param tableName Name of the table to get data from
	 * @param columnName Name of column to get value from
	 * @param queries Queries to search for specifics
	 */
	public Object getObject(final String tableName, final String columnName, final HashMap<String, String> queries) {
		return this.getObject(this.getSQLiteTable(tableName), columnName, queries);
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
	 * @return a hashmap where every key is a column and a key is the value of
	 *         that column.
	 */
	public HashMap<String, Object> getObjects(final SQLiteTable table, final HashMap<String, String> queries) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		final HashMap<String, Object> result = new HashMap<>();

		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(
					"SELECT * FROM " + table.getTableName() + " WHERE " + StatzUtil.convertQuery(queries) + ";");

			rs = ps.executeQuery();
			while (rs.next()) {

				// Populate hashmap
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					final String columnName = rs.getMetaData().getColumnName(i + 1);
					final Object value = rs.getObject(i + 1);

					// Put value in hashmap if not null, otherwise just put
					// empty string
					result.put(columnName, (value != null ? value : ""));
				}

				return result;
			}
		} catch (final SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (final SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
			}
		}
		return result;
	}
	
	/**
	 * @see #getObjects(SQLiteTable, HashMap)
	 * @param tableName Name of the table to get data from
	 * @param queries Queries to execute
	 * @return
	 */
	public HashMap<String, Object> getObjects(String tableName, final HashMap<String, String> queries) {
		return this.getObjects(this.getSQLiteTable(tableName), queries);
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
	public void setObjects(final SQLiteTable table, final LinkedHashMap<String, String> results) {
		Connection conn = null;
		PreparedStatement ps = null;

		StringBuilder columnNames = new StringBuilder("(");
		StringBuilder resultNames = new StringBuilder("(");

		for (final Entry<String, String> result : results.entrySet()) {
			columnNames.append(result.getKey() + ",");

			try {
				// Try to check if it is an integer
				Integer.parseInt(result.getValue());
				resultNames.append(result.getValue() + ",");
			} catch (final NumberFormatException e) {
				resultNames.append("'" + result.getValue() + "',");
			}

		}

		// Remove last comma
		columnNames = new StringBuilder(columnNames.substring(0, columnNames.lastIndexOf(",")) + ")");
		resultNames = new StringBuilder(resultNames.substring(0, resultNames.lastIndexOf(",")) + ")");

		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("INSERT OR REPLACE INTO " + table.getTableName() + " " + columnNames.toString()
					+ " VALUES" + resultNames);
			ps.executeUpdate();
			return;
		} catch (final SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (final SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
			}
		}
		return;
	}

	/**
	 * Closes sqlite connection.
	 * 
	 * @param ps PreparedStatement to be closed
	 * @param rs ResultSet to be closed
	 */
	public void close(final PreparedStatement ps, final ResultSet rs) {
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (final SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
		}
	}

	public List<SQLiteTable> getTables() {
		return tables;
	}

	public void setTables(final List<SQLiteTable> tables) {
		this.tables = tables;
	}

	public void addTable(final SQLiteTable table) {
		tables.add(table);
	}
}
