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

import org.bukkit.ChatColor;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.SQLiteEntry;
import me.staartvin.statz.database.datatype.SQLiteTable;
import me.staartvin.statz.database.datatype.SQLiteTable.SQLDataType;

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
		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		final File dataFile = new File(plugin.getDataFolder(), databaseName + ".db");
		if (!dataFile.exists()) {
			plugin.debugMessage(ChatColor.YELLOW + "Database not found! Creating one for you.");
			try {
				dataFile.getParentFile().mkdirs();
				dataFile.createNewFile();
				plugin.debugMessage(ChatColor.GREEN + "Database created!");
			} catch (final IOException e) {
				plugin.getLogger().log(Level.SEVERE, "File write error: " + databaseName + ".db");
			}
		}

		try {
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
			StringBuilder statement = new StringBuilder(
					"CREATE TABLE IF NOT EXISTS " + table.getTableName() + " (");

			// For each column in the table, add it to the table.
			for (final SQLiteEntry column : table.getColumns()) {

				if (column.getDataType().equals(SQLDataType.INT)) {
					statement.append("'" + column.getColumnName() + "' INTEGER");
				} else {
					statement.append("'" + column.getColumnName() + "' " + column.getDataType().toString());
				}
				

				if (column.isPrimaryKey()) {
					statement.append(" PRIMARY KEY");
				}
				
				if (column.isAutoIncrement()) {
					statement.append(" AUTOINCREMENT");
				}
				
				if (column.isNotNull()) {
					statement.append(" NOT NULL");
				}
				
				if (column.isUnique()) {
					statement.append(" UNIQUE");
				}

				statement.append(",");

			}

			/*if (table.getPrimaryKey() == null) {
				// Remove last comma
				statement = new StringBuilder(statement.substring(0, statement.lastIndexOf(",")));
			}*/
			
			if (!table.getUniqueMatched().isEmpty()) {
				
				statement.append("UNIQUE (");
				
				for (SQLiteEntry matched : table.getUniqueMatched()) {
					statement.append(matched.getColumnName() + ",");
				}
				
				// Remove last comma
				statement = new StringBuilder(statement.substring(0, statement.lastIndexOf(",")) + ")");
			} else {
				statement = new StringBuilder(statement.substring(0, statement.lastIndexOf(",")));
			}
			
			
			
			statement.append(");");

			statements.add(statement.toString());
			
			plugin.debugMessage(ChatColor.BLUE + "Loaded table '" + table.getTableName() + "'");
		}

		return statements;
	}
}