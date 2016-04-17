package me.staartvin.statz.database.datatype;

import me.staartvin.statz.database.datatype.SQLiteTable.SQLDataType;

public class SQLiteEntry {

	private String columnName;
	private boolean primaryKey = false, notNull = false;
	private SQLDataType dataType = SQLDataType.TEXT;

	public SQLiteEntry(final String columnName, final boolean primaryKey, final SQLDataType dataType) {
		this.setColumnName(columnName);
		this.setPrimaryKey(primaryKey);
		this.setDataType(dataType);
		this.setNotNull(true);
	}

	public SQLiteEntry(final String columnName, final boolean primaryKey, final SQLDataType dataType,
			final boolean notNull) {
		this.setColumnName(columnName);
		this.setPrimaryKey(primaryKey);
		this.setDataType(dataType);
		this.setNotNull(notNull);
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(final String columnName) {
		this.columnName = columnName;
	}

	public SQLDataType getDataType() {
		return dataType;
	}

	public void setDataType(final SQLDataType dataType) {
		this.dataType = dataType;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(final boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(final boolean notNull) {
		this.notNull = notNull;
	}
}
