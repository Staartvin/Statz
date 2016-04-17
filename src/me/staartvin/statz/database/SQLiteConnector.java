package me.staartvin.statz.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.SQLiteEntry;
import me.staartvin.statz.database.datatype.SQLiteTable;

public class SQLiteConnector extends Database {

	private final Statz plugin;

	public final static String databaseName = "statz";
	// Make sure we have distinct tables
	public static final String prefix = "statz_";

	public SQLiteConnector(final Statz instance) {
		super(instance);
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.staartvin.statz.database.Database#getSQLConnection()
	 */
	@Override
	public Connection getSQLConnection() {
		final File dataFile = new File(plugin.getDataFolder(), databaseName + ".db");
		if (!dataFile.exists()) {
			plugin.getLogger().info("Database not found! Creating one for you.");
			try {
				dataFile.getParentFile().mkdirs();
				dataFile.createNewFile();
				plugin.getLogger().info("Database created!");
			} catch (final IOException e) {
				plugin.getLogger().log(Level.SEVERE, "File write error: " + databaseName + ".db");
			}
		}

		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile);
			return connection;
		} catch (final SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
		} catch (final ClassNotFoundException ex) {
			plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see me.staartvin.statz.database.Database#load()
	 */
	@Override
	public void load() {
		connection = getSQLConnection();

		try {
			final Statement s = connection.createStatement();

			// Run all statements to create tables
			for (final String statement : this.createTablesStatement()) {
				s.executeUpdate(statement);
			}

			s.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		initialize();
	}

	/**
	 * This function creates multiple strings in 'SQL style' to create the
	 * proper tables.
	 * <br>
	 * It looks at the tables that are loaded in memory and dynamically creates
	 * proper SQL statements.
	 * 
	 * @return SQL statements that will create the necessary tables when run.
	 */
	public List<String> createTablesStatement() {
		// Returns a list of statements that need to be run to create the tables.

		final List<String> statements = new ArrayList<String>();

		for (final SQLiteTable table : this.getTables()) {
			final StringBuilder statement = new StringBuilder(
					"CREATE TABLE IF NOT EXISTS " + table.getTableName() + " (");

			// For each column in the table, add it to the table.
			for (final SQLiteEntry column : table.getColumns()) {

				statement.append("'" + column.getColumnName() + "' " + column.getDataType().toString() + " ");

				if (column.isNotNull()) {
					statement.append(" NOT NULL");
				}

				statement.append(",");

			}

			// All tables are added, now add primary key.

			statement.append("PRIMARY KEY('" + table.getPrimaryKey() + "'));");

			statements.add(statement.toString());
		}

		return statements;
	}
}