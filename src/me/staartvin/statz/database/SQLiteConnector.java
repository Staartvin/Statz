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

	private Statz plugin;

	public final static String databaseName = "statz";
	// Make sure we have distinct tables
	public static final String prefix = "statz_";

	public SQLiteConnector(Statz instance) {
		super(instance);
		plugin = instance;
	}

	// SQL creation stuff, You can leave the blow stuff untouched.
	public Connection getSQLConnection() {
		File dataFile = new File(plugin.getDataFolder(), databaseName + ".db");
		if (!dataFile.exists()) {
			plugin.getLogger().info("Database not found! Creating one for you.");
			try {
				dataFile.getParentFile().mkdirs();
				dataFile.createNewFile();
				plugin.getLogger().info("Database created!");
			} catch (IOException e) {
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
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
		} catch (ClassNotFoundException ex) {
			plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
		}
		return null;
	}

	public void load() {
		connection = getSQLConnection();

		try {
			Statement s = connection.createStatement();
			
			// Run all statements to create tables
			for (String statement: this.createTablesStatement()) {
				s.executeUpdate(statement);
			}
			
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		initialize();
	}

	public List<String> createTablesStatement() {
    	// Returns a list of statements that need to be run to create the tables.
    	
    	List<String> statements = new ArrayList<String>();
    	
    	for (SQLiteTable table : this.getTables()) {
        	StringBuilder statement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table.getTableName() + " (");
        	
        	// For each column in the table, add it to the table.
        	for (SQLiteEntry column : table.getColumns()) {
        				
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