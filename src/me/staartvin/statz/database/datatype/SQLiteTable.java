package me.staartvin.statz.database.datatype;

import java.util.ArrayList;
import java.util.List;

import me.staartvin.statz.database.SQLiteConnector;

/**
 * Used to store a table and its columns
 * 
 * @author Staartvin
 *
 */
public class SQLiteTable {

	public static enum SQLDataType {
		TEXT, NUM, INT, REAL, NONE
	}

	private String tableName = "";

	private List<SQLiteEntry> columns = new ArrayList<SQLiteEntry>();
	
	private List<SQLiteEntry> uniqueMatched = new ArrayList<SQLiteEntry>();

	public SQLiteTable(final String tableName) {
		this.setTableName(tableName);
	}

	public List<SQLiteEntry> getColumns() {
		return columns;
	}

	public void setColumns(final List<SQLiteEntry> columns) {
		this.columns = columns;
	}

	public SQLiteTable addColumn(final String columnName, final boolean primaryKey, final SQLDataType type) {
		columns.add(new SQLiteEntry(columnName, primaryKey, type));
		return this; // Return this to allow chaining.
	}

	public SQLiteTable addColumn(final SQLiteEntry entry) {
		columns.add(entry);
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(final String tableName) {
		this.tableName = SQLiteConnector.prefix + tableName;
	}

	public String getPrimaryKey() {
		for (final SQLiteEntry column : columns) {
			if (column.isPrimaryKey())
				return column.getColumnName();
		}

		return null;
	}

	public void setPrimaryKey(final String primaryKey) {
		for (final SQLiteEntry column : columns) {
			column.setPrimaryKey(false);
		}

		for (final SQLiteEntry column : columns) {
			if (column.getColumnName().equalsIgnoreCase(primaryKey)) {
				column.setPrimaryKey(true);
			}
		}
	}

	public List<SQLiteEntry> getUniqueMatched() {
		return uniqueMatched;
	}

	public void setUniqueMatched(List<SQLiteEntry> uniqueMatched) {
		this.uniqueMatched = uniqueMatched;
	}
	
	public void addUniqueMatched(SQLiteEntry entry) {
		this.uniqueMatched.add(entry);
	}
	
	public SQLiteEntry getColumn(String columnName) {
		for (SQLiteEntry e: columns) {
			if (e.getColumnName().equalsIgnoreCase(columnName)) {
				return e;
			}
		}
		
		return null;
	}
	
	public boolean addUniqueMatched(String columnName) {
		SQLiteEntry entry = this.getColumn(columnName);
		
		// No entry found. -> Return false
		if (entry == null) {
			return false;
		}
		
		// Found entry, add it to the unique matched.
		this.addUniqueMatched(entry);
		return true;
	}

}
