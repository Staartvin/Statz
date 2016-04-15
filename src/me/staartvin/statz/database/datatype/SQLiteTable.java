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

	public SQLiteTable(String tableName) {
		this.setTableName(tableName);
	}

	public List<SQLiteEntry> getColumns() {
		return columns;
	}

	public void setColumns(List<SQLiteEntry> columns) {
		this.columns = columns;
	}

	public SQLiteTable addColumn(String columnName, boolean primaryKey, SQLDataType type) {
		columns.add(new SQLiteEntry(columnName, primaryKey, type));
		return this; // Return this to allow chaining.
	}
	
	public SQLiteTable addColumn(SQLiteEntry entry) {
		columns.add(entry);
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = SQLiteConnector.prefix + tableName;
	}

	public String getPrimaryKey() {
		for (SQLiteEntry column : columns) {
			if (column.isPrimaryKey())
				return column.getColumnName();
		}

		return null;
	}

	public void setPrimaryKey(String primaryKey) {
		for (SQLiteEntry column: columns) {
			column.setPrimaryKey(false);
		}
		
		for (SQLiteEntry column: columns) {
			if (column.getColumnName().equalsIgnoreCase(primaryKey)) {
				column.setPrimaryKey(true);
			}
		}
	}

}
