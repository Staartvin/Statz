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
public abstract class Table {

	public static enum SQLDataType {
		TEXT, INT, FLOAT, NONE, DOUBLE
	}

	private String tableName = "";

	private List<Column> columns = new ArrayList<Column>();

	private List<Column> uniqueMatched = new ArrayList<Column>();

	public Table(final String tableName) {
		this.setTableName(tableName);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(final List<Column> columns) {
		this.columns = columns;
	}

	public Table addColumn(final String columnName, final boolean primaryKey, final SQLDataType type) {
		columns.add(new Column(columnName, primaryKey, type));
		return this; // Return this to allow chaining.
	}

	public Table addColumn(final Column entry) {
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
		for (final Column column : columns) {
			if (column.isPrimaryKey())
				return column.getColumnName();
		}

		return null;
	}

	public void setPrimaryKey(final String primaryKey) {
		for (final Column column : columns) {
			column.setPrimaryKey(false);
		}

		for (final Column column : columns) {
			if (column.getColumnName().equalsIgnoreCase(primaryKey)) {
				column.setPrimaryKey(true);
			}
		}
	}

	public List<Column> getUniqueMatched() {
		return uniqueMatched;
	}

	public void setUniqueMatched(List<Column> uniqueMatched) {
		this.uniqueMatched = uniqueMatched;
	}

	public void addUniqueMatched(Column entry) {
		this.uniqueMatched.add(entry);
	}

	public Column getColumn(String columnName) {
		for (Column e : columns) {
			if (e.getColumnName().equalsIgnoreCase(columnName)) {
				return e;
			}
		}

		return null;
	}

	public boolean addUniqueMatched(String columnName) {
		Column entry = this.getColumn(columnName);

		// No entry found. -> Return false
		if (entry == null) {
			return false;
		}

		// Found entry, add it to the unique matched.
		this.addUniqueMatched(entry);
		return true;
	}

}
