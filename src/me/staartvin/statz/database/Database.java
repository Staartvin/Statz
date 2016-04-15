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
	private Statz plugin;

	// All tables are stored here.
	private List<SQLiteTable> tables = new ArrayList<SQLiteTable>();

	public Connection connection;

	public Database(Statz instance) {
		plugin = instance;
	}

	public void loadTables() {
		// UUID table to look up uuid of players
		SQLiteTable newTable = new SQLiteTable("players");

		// Populate table
		newTable.addColumn("uuid", true, SQLDataType.TEXT); // UUID of the
															// player
		newTable.addColumn("playerName", false, SQLDataType.TEXT); // Name of
																	// the
		// player
		this.addTable(newTable);

		// How many times did a player join this server?
		newTable = new SQLiteTable("joins");

		newTable.addColumn("uuid", true, SQLDataType.TEXT); // UUID of the
															// player
		newTable.addColumn("value", false, SQLDataType.INT); // How many times
																// did the
		// player join.

		this.addTable(newTable);

		// How many times did a player die?
		newTable = new SQLiteTable("death");

		newTable.addColumn("uuid", true, SQLDataType.TEXT); // UUID of the
															// player
		newTable.addColumn("value", false, SQLDataType.INT); // How many times
																// did the
		// player die.

		this.addTable(newTable);

	}

	public SQLiteTable getSQLiteTable(String tableName) {

		tableName = SQLiteConnector.prefix + tableName;

		for (SQLiteTable table : tables) {
			if (table.getTableName().equals(tableName)) {
				return table;
			}
		}

		return null;
	}

	public abstract Connection getSQLConnection();

	public abstract void load();

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
	public Object getObject(SQLiteTable table, String columnName, HashMap<String, String> queries) {
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
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
			}
		}
		return null;
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
	public HashMap<String, Object> getObjects(SQLiteTable table, HashMap<String, String> queries) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		HashMap<String, Object> result = new HashMap<>();

		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(
					"SELECT * FROM " + table.getTableName() + " WHERE " + StatzUtil.convertQuery(queries) + ";");

			rs = ps.executeQuery();
			while (rs.next()) {

				// Populate hashmap
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					String columnName = rs.getMetaData().getColumnName(i + 1);
					Object value = rs.getObject(i + 1);

					// Put value in hashmap if not null, otherwise just put
					// empty string
					result.put(columnName, (value != null ? value : ""));
				}

				return result;
			}
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
			}
		}
		return result;
	}

	/**
	 * Sets values to columns in a specific table.
	 * <br><b>Note:</b> a linked hashmap is used to make sure that the order of the elements does not change.
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
	public void setObjects(SQLiteTable table, LinkedHashMap<String, String> results) {
		Connection conn = null;
		PreparedStatement ps = null;

		StringBuilder columnNames = new StringBuilder("(");
		StringBuilder resultNames = new StringBuilder("(");

		for (Entry<String, String> result : results.entrySet()) {
			columnNames.append(result.getKey() + ",");

			try {
				// Try to check if it is an integer
				Integer.parseInt(result.getValue());
				resultNames.append(result.getValue() + ",");
			} catch (NumberFormatException e) {
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
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
			}
		}
		return;
	}

	public void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
		}
	}

	public List<SQLiteTable> getTables() {
		return tables;
	}

	public void setTables(List<SQLiteTable> tables) {
		this.tables = tables;
	}

	public void addTable(SQLiteTable table) {
		tables.add(table);
	}
}
