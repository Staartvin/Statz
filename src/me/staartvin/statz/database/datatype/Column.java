package me.staartvin.statz.database.datatype;

import me.staartvin.statz.database.datatype.Table.SQLDataType;

public class Column {

	private String columnName;
	private boolean primaryKey = false, notNull = false, isUnique = false, autoIncrement = false;
	private SQLDataType dataType = SQLDataType.TEXT;

	public Column(final String columnName, final boolean primaryKey, final SQLDataType dataType) {
		this.setColumnName(columnName);
		this.setPrimaryKey(primaryKey);
		this.setDataType(dataType);
		this.setNotNull(true);
	}

	public Column(final String columnName, final boolean primaryKey, final SQLDataType dataType,
			final boolean notNull) {
		this.setColumnName(columnName);
		this.setPrimaryKey(primaryKey);
		this.setDataType(dataType);
		this.setNotNull(notNull);
	}

	public Column(final String columnName, final boolean primaryKey, final SQLDataType dataType, final boolean notNull,
			final boolean isUnique) {
		this.setColumnName(columnName);
		this.setPrimaryKey(primaryKey);
		this.setDataType(dataType);
		this.setNotNull(notNull);
		this.setUnique(isUnique);
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

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
}
