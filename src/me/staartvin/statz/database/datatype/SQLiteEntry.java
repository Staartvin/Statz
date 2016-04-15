package me.staartvin.statz.database.datatype;

import me.staartvin.statz.database.datatype.SQLiteTable.SQLDataType;

public class SQLiteEntry {

	private String columnName;
	private boolean primaryKey = false, notNull = false;
	private SQLDataType dataType = SQLDataType.TEXT;
	
	public SQLiteEntry(String columnName, boolean primaryKey, SQLDataType dataType) {
		this.setColumnName(columnName);
		this.setPrimaryKey(primaryKey);
		this.setDataType(dataType);
		this.setNotNull(true);
	}
	
	public SQLiteEntry(String columnName, boolean primaryKey, SQLDataType dataType, boolean notNull) {
		this.setColumnName(columnName);
		this.setPrimaryKey(primaryKey);
		this.setDataType(dataType);
		this.setNotNull(notNull);
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public SQLDataType getDataType() {
		return dataType;
	}

	public void setDataType(SQLDataType dataType) {
		this.dataType = dataType;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}
}
